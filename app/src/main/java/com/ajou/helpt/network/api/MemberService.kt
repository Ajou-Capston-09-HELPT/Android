package com.ajou.helpt.network.api

import com.ajou.helpt.auth.Member
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

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

    @DELETE("members")
    suspend fun quit(
        @Header("Authorization") accessToken : String
    ):Response<ResponseBody>
}