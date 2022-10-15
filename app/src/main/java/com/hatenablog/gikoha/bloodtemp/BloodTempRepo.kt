package com.hatenablog.gikoha.bloodtemp

import androidx.compose.runtime.Stable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// API GET

@Stable
data class BloodTemp(
    val date: String,
    val temp: String,
    val memo: String?,
)

interface BloodTempGetAPI
{
    @GET(BuildConfig.bloodgetapi)
    suspend fun getItems(): Response<Array<BloodTemp>>

}

// API POST

@Stable
data class BloodTempPost(
    val apikey: String,
    val temp: String,
    val memo: String,
)

interface BloodTempPostAPI
{
    @POST(BuildConfig.bloodpostapi)
    suspend fun postItem(
        @Body postdata: BloodTempPost
    ): Response<Void>
}
