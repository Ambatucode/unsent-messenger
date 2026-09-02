package com.unsent.messenger.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AiService {

    private const val PREFS_NAME = "ai_prefs"
    private const val KEY_API_KEY = "gemini_api_key"
    private const val TAG = "AiService"

    fun getApiKey(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun saveApiKey(context: Context, apiKey: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
    }

    fun hasApiKey(context: Context): Boolean {
        return getApiKey(context).isNotBlank()
    }

    suspend fun generateResponse(
        apiKey: String,
        prompt: String,
        systemInstruction: String = "You are an intelligent, helpful AI assistant built into the Unsent Messenger app. Answer questions, solve forms, draft smart replies, and provide concise, accurate information."
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API Key is missing. Please enter your Gemini API Key in Settings or the AI tab."))
        }

        try {
            // Using Gemini 1.5 Flash (ultra-fast, accurate, free tier on Google AI Studio)
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 25000

            // Build Gemini REST JSON payload
            val rootJson = JSONObject()

            // System instruction
            if (systemInstruction.isNotBlank()) {
                val systemContent = JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                }
                rootJson.put("systemInstruction", systemContent)
            }

            // User prompt
            val contentsArray = JSONArray()
            val userContent = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            }
            contentsArray.put(userContent)
            rootJson.put("contents", contentsArray)

            // Send request
            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(rootJson.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val responseJson = JSONObject(responseText)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        return@withContext Result.success(text)
                    }
                }
                Result.failure(Exception("Empty response from AI model."))
            } else {
                val errorStream = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } }
                Log.e(TAG, "Gemini API error ($responseCode): $errorStream")
                Result.failure(Exception("Gemini API error ($responseCode). Please verify your API Key."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network or API call failed", e)
            Result.failure(e)
        }
    }
}
