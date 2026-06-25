package gotogether.frontend.android.api

import gotogether.frontend.android.model.*
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: UUID): Response<User>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @POST("users/signup")
    suspend fun signup(@Body request: Map<String, String>): Response<UUID>

    @POST("users/login")
    suspend fun login(@Body request: Map<String, String>): Response<UUID>

    @PUT("users/preferences/socialBattery/{userId}")
    suspend fun updateSocialBattery(@Path("userId") userId: UUID, @Body socialBattery: Int): Response<Int>

    @PUT("users/preferences/interests/{userId}")
    suspend fun updateInterests(@Path("userId") userId: UUID, @Body interestIds: List<UUID>): Response<List<UUID>>

    @GET("topics")
    suspend fun getTopics(): Response<List<Topic>>

    @POST("challenges/filter")
    suspend fun filterChallenges(@Body filter: Map<String, Any?>): Response<List<Challenge>>

    @POST("challenges/participate")
    suspend fun participate(@Body request: Map<String, Any?>): Response<UUID>
}
