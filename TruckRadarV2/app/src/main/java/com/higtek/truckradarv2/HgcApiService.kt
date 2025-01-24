package com.higtek.truckradarv2

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.converter.simplexml.SimpleXmlConverterFactory


private var mBaseUrl = "http://192.168.0.212"

private val interceptor = HttpLoggingInterceptor()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build();

private var retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(mBaseUrl)
    .client(okHttpClient)
    .build()

interface HgcApiService{

    @Headers(
        "Content-Type: text/xml",
        "Accept-Charset: utf-8"
    )
    @GET("/communication.asmx/GetEventParsedData")
    suspend fun GetEventParsedData(@Query("login") login : String,
                                   @Query("password") password : String,
                                   @Query("eventsCount") eventsCount : Int): String

    @GET("/communication.asmx/EraseUnsentEventsForUser")
    suspend fun EraseUnsentEventsForUser(@Query("login") login : String,
                                         @Query("password") password : String): String

    @GET("/communication.asmx/ExecuteCommand")
    suspend fun ExecuteCommand(@Query("login") login : String,
                               @Query("password") password : String,
                               @Query("modemSN") modemSN : String,
                               @Query("protocol") protocol : String,
                               @Query("commandCode") commandCode : Int,
                               @Query("parameters") parameters : String): String
}

object HgcApi{
    public fun updateUrl(url : String){
        mBaseUrl = url
    }

    public fun getRetrofitService() : HgcApiService{
        if (retrofit.baseUrl().toString() != mBaseUrl){

            retrofit = Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build()
            // Save URL after autoformat.
            mBaseUrl = retrofit.baseUrl().toString()
        }
        return retrofit.create(HgcApiService::class.java)
    }
}
