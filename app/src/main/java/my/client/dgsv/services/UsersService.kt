package my.client.dgsv.services

import my.client.dgsv.Module.Subjects
import my.client.dgsv.Question
import retrofit2.Call
import retrofit2.http.*

interface UsersService {
    @GET("getSubInfo")
    fun getUsersList(@QueryMap filter: HashMap<String, String>): Call<List<Subjects>>

    @POST("sendData")
    fun addUsers(@Body newDestination: Subjects): Call<Subjects>

    @DELETE("sendbyId/{id}")
    fun deleteUsers(@Path("id") id: Int): Call<Unit>

}