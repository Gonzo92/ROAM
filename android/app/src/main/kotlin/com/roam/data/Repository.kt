package com.roam.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class TravelRepository(private val apiService: ApiService) {
    suspend fun searchTravelOptions(request: TravelSearchRequest): Result<TravelSearchResponse> =
        withContext(Dispatchers.IO) {
            var lastException: Exception? = null
            repeat(3) { attempt ->
                try {
                    val response = apiService.searchTravelOptions(request)
                    return@withContext Result.success(response)
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < 2) {
                        delay(1000L * (attempt + 1))
                    }
                }
            }
            Result.failure(lastException ?: Exception("Unknown error"))
        }
}

