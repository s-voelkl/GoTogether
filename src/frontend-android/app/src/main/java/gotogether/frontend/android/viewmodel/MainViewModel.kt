package gotogether.frontend.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gotogether.frontend.android.api.RetrofitClient
import gotogether.frontend.android.model.Challenge
import gotogether.frontend.android.model.Topic
import gotogether.frontend.android.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {
    private val api = RetrofitClient.instance

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _aiMessage = MutableStateFlow<String?>(null)
    val aiMessage: StateFlow<String?> = _aiMessage

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchTopics()
        fetchChallenges(52.52, 13.40)
    }

    private fun triggerAiWelcome() {
        viewModelScope.launch {
            // Simulated AI logic: Suggest a challenge based on current time or data
            _aiMessage.value = "Hey! Based on your 'High' social battery, I recommend the 'Coffee & Code' challenge nearby. Ready to meet some people?"
        }
    }

    fun dismissAiMessage() {
        _aiMessage.value = null
    }

    fun dismissErrorMessage() {
        _errorMessage.value = null
    }

    fun fetchTopics() {
        viewModelScope.launch {
            try {
                val response = api.getTopics()
                if (response.isSuccessful) {
                    _topics.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchChallenges(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val filter = mapOf(
                    "latitude" to lat,
                    "longitude" to lon,
                    "radiusMeters" to 5000
                )
                val response = api.filterChallenges(filter)
                if (response.isSuccessful) {
                    _challenges.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf("email" to email, "password" to password)
                val response = api.login(request)
                if (response.isSuccessful) {
                    val userId = response.body()
                    if (userId != null) {
                        fetchUser(userId)
                    }
                } else {
                    _errorMessage.value = "Login failed. Please check your credentials."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error during login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
                val response = api.signup(request)
                if (response.isSuccessful) {
                    val userId = response.body()
                    if (userId != null) {
                        fetchUser(userId)
                    }
                } else {
                    _errorMessage.value = "Signup failed. Email might already be in use."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error during signup: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchUser(userId: UUID) {
        viewModelScope.launch {
            try {
                val response = api.getUser(userId)
                if (response.isSuccessful) {
                    _currentUser.value = response.body()
                    triggerAiWelcome()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching user data."
            }
        }
    }

    fun loginDemoUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersResponse = api.getAllUsers()
                if (usersResponse.isSuccessful && usersResponse.body()?.isNotEmpty() == true) {
                    _currentUser.value = usersResponse.body()?.first()
                    triggerAiWelcome()
                } else {
                    _errorMessage.value = "No demo users found."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error connecting to demo service."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSocialBattery(value: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            try {
                val response = api.updateSocialBattery(user.id, value)
                if (response.isSuccessful) {
                    _currentUser.value = user.copy(socialBattery = value)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleInterest(topicId: UUID) {
        val user = _currentUser.value ?: return
        val currentInterests = user.interests.toMutableList()
        if (currentInterests.contains(topicId)) {
            currentInterests.remove(topicId)
        } else {
            currentInterests.add(topicId)
        }

        viewModelScope.launch {
            try {
                val response = api.updateInterests(user.id, currentInterests)
                if (response.isSuccessful) {
                    _currentUser.value = user.copy(interests = currentInterests)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun participateInChallenge(challengeId: UUID, verificationCode: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            try {
                // Based on backend ChallengeParticipanceDTO
                val request = mapOf(
                    "userEmail" to user.email,
                    "userPassword" to "password", // In a real app, this would be handled via session/auth
                    "userLatitude" to 52.52,
                    "userLongitude" to 13.40,
                    "challengeId" to challengeId,
                    "verificationCode" to verificationCode
                )
                val response = api.participate(request)
                if (response.isSuccessful) {
                    // Refresh user to get new XP/Currency
                    val userResponse = api.getUser(user.id)
                    if (userResponse.isSuccessful) {
                        _currentUser.value = userResponse.body()
                    }
                    // Refresh challenges to update player count
                    fetchChallenges(52.52, 13.40)
                    _aiMessage.value = "Awesome! You've joined the challenge. Go meet your team!"
                } else {
                    _aiMessage.value = "Verification failed. Are you at the right location?"
                }
            } catch (e: Exception) {
                _aiMessage.value = "Error: Could not join challenge."
            }
        }
    }
}
