package akshay.client.exam.services

import akshay.client.exam.Module.Security
import akshay.client.exam.Module.Userinfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DataService {
    @POST("sendToken")
    fun addToken(@Body newDestination: Security): Call<Security>
}