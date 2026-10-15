package com.zubtech.rohingyashikho.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleCloudTtsApi {
    @POST("v1/text:synthesize")
    suspend fun synthesize(
        @Query("key") apiKey: String,
        @Body request: GoogleTtsRequest
    ): Response<GoogleTtsResponse>

    companion object {
        const val BASE_URL = "https://texttospeech.googleapis.com/"
    }
}

data class GoogleTtsRequest(
    val input: GoogleTtsInput,
    val voice: GoogleTtsVoice,
    val audioConfig: GoogleTtsAudioConfig
)

data class GoogleTtsInput(val text: String)

data class GoogleTtsVoice(
    val languageCode: String = "en-IN",
    val name: String = "en-IN-Neural2-B"
)

data class GoogleTtsAudioConfig(
    val audioEncoding: String = "MP3"
)

data class GoogleTtsResponse(
    val audioContent: String // Base64 encoded string
)
