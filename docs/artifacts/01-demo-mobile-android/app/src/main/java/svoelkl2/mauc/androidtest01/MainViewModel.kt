package svoelkl2.mauc.androidtest01

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import svoelkl2.mauc.androidtest01.ui.quests.Quest

data class Friend(
    val name: String, 
    val birthday: String, 
    val level: Int, 
    val virtualScore: Int, 
    val badgeCount: Int,
    val ownedItems: List<String> = emptyList()
)
data class ShopItem(val id: String, val name: String, val price: Int, val emoji: String, val purchaseCount: Int = 0)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _socialBattery = MutableLiveData<Int>(50)
    val socialBattery: LiveData<Int> = _socialBattery

    private val _socialXP = MutableLiveData<Int>(0)
    val socialXP: LiveData<Int> = _socialXP

    private val _interests = MutableLiveData<List<String>>(emptyList())
    val interests: LiveData<List<String>> = _interests

    private val _allAvailableInterests = listOf(
        "Hiking", "Cooking", "Gaming", "Reading", "Politics", "Urban", "Nature", "Tech", "AI", "Food", 
        "Culture", "Photography", "Music", "Art", "Movies", "Sports", "Fitness", "Travel", "Fashion", "Beauty", 
        "Science", "History", "Philosophy", "Psychology", "Environment", "Sustainability", "Animals", "Pets", "Gardening", "DIY",
        "Crafting", "Writing", "Poetry", "Dancing", "Singing", "Theater", "Comedy", "Board Games", "Card Games", "Video Games",
        "Programming", "Web Design", "Graphic Design", "Marketing", "Business", "Entrepreneurship", "Finance", "Investing", "Economics", "Law",
        "Education", "Teaching", "Learning", "Languages", "Translation", "Volunteering", "Charity", "Activism", "Spirituality", "Meditation", "Yoga"
    )
    val allAvailableInterests: List<String> = _allAvailableInterests

    private val _selectedTeam = MutableLiveData<String>("None")
    val selectedTeam: LiveData<String> = _selectedTeam

    private val _userName = MutableLiveData<String>("User")
    val userName: LiveData<String> = _userName

    private val _birthday = MutableLiveData<String>("01.01.2000")
    val birthday: LiveData<String> = _birthday

    private val _badges = MutableLiveData<List<String>>(emptyList())
    val badges: LiveData<List<String>> = _badges

    private val _currency = MutableLiveData<Int>(0)
    val currency: LiveData<Int> = _currency

    private val _ownedItems = MutableLiveData<List<String>>(emptyList())
    val ownedItems: LiveData<List<String>> = _ownedItems

    private val _friends = MutableLiveData<List<Friend>>(listOf(
        Friend("Alice", "12.05.1998", 5, 100, 3, listOf("Rocket Emoji")),
        Friend("Bob", "23.11.1995", 3, 50, 1, listOf("Pioneer Skin")),
        Friend("Charlie", "05.02.2001", 7, 200, 5, listOf("Gold Medal", "Master Title")),
        Friend("Diana", "30.08.1999", 4, 80, 2, emptyList())
    ))
    val friends: LiveData<List<Friend>> = _friends

    private val _quests = MutableLiveData<List<Quest>>(listOf(
        Quest("q1", "City Hall Meeting", "Discuss urban development", "100 XP + 20 Gold", "Social", "2h", 70, listOf("Politics", "Urban"), 100, 20, 48.1375, 11.5755, listOf("Alice")),
        Quest("q2", "English Garden Walk", "Relaxing walk in the park", "50 XP + 10 Gold", "Casual", "1.5h", 30, listOf("Nature", "Hiking"), 50, 10, 48.1428, 11.5778, listOf("Bob", "Charlie")),
        Quest("q3", "Tech Hub Meetup", "New AI trends discussion", "80 XP + 15 Gold", "Professional", "3h", 50, listOf("Tech", "AI"), 80, 15, 49.731694, 12.193381, listOf("Diana")),
        Quest("q4", "River Side Grill", "BBQ at the river", "120 XP + 25 Gold", "Food", "4h", 60, listOf("Cooking", "Nature"), 120, 25, 49.741694, 12.183381),
        Quest("q5", "Local Library Club", "Monthly book discussion", "40 XP + 5 Gold", "Culture", "2h", 20, listOf("Reading"), 40, 5, 49.721694, 12.203381),
        Quest("q6", "Weiden City Center Run", "Morning jogging group", "60 XP + 12 Gold", "Sports", "1h", 40, listOf("Sports", "Fitness"), 60, 12, 49.6750, 12.1600, listOf("Alice", "Bob")),
        Quest("q7", "Old Town Coffee", "Casual chat at the café", "30 XP + 5 Gold", "Casual", "1h", 10, listOf("Food", "Culture"), 30, 5, 49.6758, 12.1610, listOf("Charlie")),
        Quest("q8", "OMV Park Clean-up", "Volunteering to clean the park", "150 XP + 30 Gold", "Social", "3h", 80, listOf("Environment", "Volunteering"), 150, 30, 49.6780, 12.1550, listOf("Diana", "Alice")),
        Quest("q9", "Campus Tech Talk", "Guest lecture on Cyber Security", "90 XP + 20 Gold", "Professional", "2h", 60, listOf("Tech", "Programming"), 90, 20, 49.6790, 12.1640, listOf("Bob")),
        Quest("q10", "Weiden Board Game Night", "Play strategy games with locals", "70 XP + 15 Gold", "Gaming", "3h", 50, listOf("Gaming", "Board Games"), 70, 15, 49.6740, 12.1620, listOf("Charlie", "Diana"))
    ))
    val quests: LiveData<List<Quest>> = _quests

    private val _selectedQuest = MutableLiveData<Quest?>()
    val selectedQuest: LiveData<Quest?> = _selectedQuest

    private val _navToQuests = MutableLiveData<Boolean>(false)
    val navToQuests: LiveData<Boolean> = _navToQuests

    private val _shopItems = MutableLiveData<List<ShopItem>>(listOf(
        ShopItem("s1", "Pioneer Skin", 50, "🎭"),
        ShopItem("s2", "Gold Medal", 100, "🥇"),
        ShopItem("s3", "Rocket Emoji", 20, "🚀"),
        ShopItem("s4", "Master Title", 200, "👑")
    ))
    val shopItems: LiveData<List<ShopItem>> = _shopItems

    fun selectQuest(quest: Quest) {
        _selectedQuest.value = quest
        _navToQuests.value = true
    }

    fun clearNav() {
        _navToQuests.value = false
    }

    fun setSocialBattery(value: Int) {
        _socialBattery.value = value
    }
    
    fun addXP(amount: Int) {
        _socialXP.value = (_socialXP.value ?: 0) + amount
    }

    fun addCurrency(amount: Int) {
        _currency.value = (_currency.value ?: 0) + amount
    }

    fun verifyQuest(questId: String) {
        val currentQuests = _quests.value ?: return
        val updatedQuests = currentQuests.map { quest ->
            if (quest.id == questId && !quest.isDone) {
                addXP(quest.xpValue)
                addCurrency(quest.currencyValue)
                
                // Increase Friend Score for participants
                if (quest.participants.isNotEmpty()) {
                    val currentFriends = _friends.value ?: emptyList()
                    val updatedFriends = currentFriends.map { friend ->
                        if (quest.participants.contains(friend.name)) {
                            friend.copy(virtualScore = friend.virtualScore + 10)
                        } else friend
                    }
                    _friends.value = updatedFriends
                }
                
                quest.copy(isDone = true)
            } else quest
        }
        _quests.value = updatedQuests
    }

    fun buyItem(item: ShopItem) {
        val currentCurrency = _currency.value ?: 0
        if (currentCurrency >= item.price) {
            _currency.value = currentCurrency - item.price
            
            // Add to owned items
            val owned = _ownedItems.value?.toMutableList() ?: mutableListOf()
            owned.add(item.name)
            _ownedItems.value = owned

            // Update shop item purchase count
            val items = _shopItems.value?.map {
                if (it.id == item.id) it.copy(purchaseCount = it.purchaseCount + 1)
                else it
            }
            if (items != null) {
                _shopItems.value = items
            }
        }
    }

    fun toggleInterest(interest: String) {
        val current = _interests.value?.toMutableList() ?: mutableListOf()
        if (current.contains(interest)) {
            current.remove(interest)
        } else {
            current.add(interest)
        }
        _interests.value = current
    }

    fun setTeam(team: String) {
        _selectedTeam.value = team
    }
}