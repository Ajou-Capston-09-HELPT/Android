package com.ajou.helpt.network.api

import com.ajou.helpt.auth.Member
import com.ajou.helpt.mypage.profile.MyInfo
import com.ajou.helpt.network.model.MemberInfoResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface MemberService {
//    @POST("members/update")
//    fun updateMember(
//
//    )
//    @POST("members/register")
//    fun registerMember(
//
//    )
    @POST("members/register")
    suspend fun register(
        @Body memberInfo : Member
    ): Response<ResponseBody>

    @POST("api/auth/token/refresh")
    suspend fun getNewToken(
        @Header("Authorization") refreshToken : String
    ): Response<ResponseBody>

    @GET("members/me")
    suspend fun getMyInfo(
        @Header("Authorization") accessToken : String
    ): Response<MemberInfoResponse>

    @DELETE("members")
    suspend fun quit(
        @Header("Authorization") accessToken : String
    ):Response<ResponseBody>

    @PUT("members")
    suspend fun updateMyInfo(
        @Header("Authorization") accessToken : String,
        @Body memberInfo : MyInfo
    ): Response<ResponseBody>

}