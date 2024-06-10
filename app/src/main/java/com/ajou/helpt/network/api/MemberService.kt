package com.ajou.helpt.network.api

import com.ajou.helpt.auth.Member
import com.ajou.helpt.mypage.model.MyInfo
import com.ajou.helpt.network.model.MemberInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MemberService {
//    @POST("members/register")
//    suspend fun register(
//        @Body memberInfo : Member
//    ): Response<ResponseBody>

    @POST("members/register")
    suspend fun register(
        @Body memberInfo : Member,
    ): Response<ResponseBody>

    @POST("members/login")
    suspend fun login(
        @Body body : RequestBody
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

    @GET("members/attendance")
    suspend fun getMyAttendance(
        @Header("Authorization") accessToken : String
    ): Response<ResponseBody>

}