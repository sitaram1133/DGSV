package akshay.client.exam.services

import akshay.client.exam.Module.*
import retrofit2.Call
import retrofit2.http.*

interface UsersService {
    @GET("getSubInfo")
    fun getUsersList(@QueryMap filter: HashMap<String, String>): Call<List<Subjects>>

    @POST("sendData")
    fun addUsers(@Body newDestination: Userinfo): Call<Userinfo>

    @DELETE("sendbyId/{id}")
    fun deleteUsers(@Path("id") id: Int): Call<Unit>


//////////////////////////////////////////////////////////////////////
    @POST("addstudents")
    fun addUser(@Body newDestination: Userinfo): Call<Userinfo>

    @GET("checkLoginstudents/{stdphone}")
    fun getUser(@Path("stdphone") phonenum: String,
                @QueryMap filter: HashMap<String, String>): Call<List<Userinfo>>


    @GET("getuserPreff/{stdphone}")
    fun getuserPreff(@Path("stdphone") stdphone: String,
                @QueryMap filter: HashMap<String, String>): Call<List<Userinfo>>

////////////////////////////////update login status//////////////////////////////////////
    @FormUrlEncoded
    @PUT("updateLoginstatus/{stdphone}")
    fun updateLoginstatus(
    @Path("stdphone") stdphone: String,
    @Field("loginstatus") loginstatus: Boolean): Call<Userinfo>
    ////////////////////////////////////////////////////////////////////////////////////



    @POST("sendresult/{phonenum}")
    fun sendResult(@Path("phonenum") phonenum: String,
                   @Body newDestination: Results): Call<Results>

    @GET("getOrganization")
    fun getOrgList(@QueryMap filter: HashMap<String, String>): Call<List<Organization>>
////////////////////////////////////////////////////////////////////



}