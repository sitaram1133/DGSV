package akshay.client.exam.services


import akshay.client.exam.Module.UserSession
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface SessionService {

    @GET("getExam/{organization}")
    fun getSession(@Path("organization") organization: String, @QueryMap filter: HashMap<String, String>): Call<List<UserSession>>
}