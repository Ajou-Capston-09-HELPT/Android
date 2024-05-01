package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.Member
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
    @POST("members/login")
    suspend fun login(
        @Body memberInfo : Member
    ) : Response<ResponseBody>

    @DELETE("members")
    suspend fun quit(
        @Header("Authorization") accessToken : String
    ):Response<ResponseBody>
}