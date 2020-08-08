package my.client.dgsv.services

import my.client.dgsv.Question
import retrofit2.Call
import retrofit2.http.*

interface CheckoutService {
    @GET("loadData/{phonenum}")
    fun getCheckoutList(@Path("phonenum") sharedphonevalue: String?,
        @QueryMap filter: HashMap<String, String>): Call<List<Question>>

    @POST("sendData")
    fun addCheckout(@Body newDestination: Question): Call<Question>

    @DELETE("sendbyId/{id}")
    fun deleteDestination(@Path("id") id: Int): Call<Unit>

}