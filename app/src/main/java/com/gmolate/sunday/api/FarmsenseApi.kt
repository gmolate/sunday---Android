```kotlin
package com.gmolate.sunday.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface FarmsenseApi {
    @GET("uv")
    suspend fun getUVIndex(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): UVResponse
    
    // ...existing code...
}
```