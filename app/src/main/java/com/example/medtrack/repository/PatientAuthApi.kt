package com.example.medtrack.repository

import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.net.HttpURLConnection
import java.net.URL

// Patient claim call to the real backend POST /claim.
// The emulator reaches the PC as 10.0.2.2 (a real phone must use the PC's LAN IP).
object PatientAuthApi {

    private const val BASE_URL = "http://10.0.2.2:8000"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun claim(
        code: String,
        age: Int,
        deviceId: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        try {
            val body = buildJsonObject {
                put("code", code)
                put("age", age)
                put("device_id", deviceId)
            }.toString()

            val connection = URL("$BASE_URL/claim").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.doOutput = true
            connection.outputStream.use { stream ->
                stream.write(body.toByteArray())
            }

            val status = connection.responseCode
            val text = (
                if (status in 200..299) connection.inputStream
                else connection.errorStream
                )?.bufferedReader()?.use { it.readText() } ?: ""
            connection.disconnect()

            if (status !in 200..299) {
                val message = runCatching {
                    json.parseToJsonElement(text)
                        .jsonObject["detail"]?.jsonPrimitive?.content
                }.getOrNull() ?: "Code or age is wrong."
                return@withContext Result.failure(
                    IllegalArgumentException(message)
                )
            }

            val patient = json.parseToJsonElement(text).jsonObject
            Result.success(
                AuthUser(
                    id = patient["patient_id"]!!.jsonPrimitive.content,
                    role = UserRole.PATIENT,
                    name = patient["name"]?.let { it.jsonPrimitive.content }
                )
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
