package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.MemberShipResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface MemberShipService {
    @GET("memberships/detail")
    suspend fun getMembershipDetail(
        @Header("Authorization") accessToken : String
    ): Response<MemberShipResponse>
}