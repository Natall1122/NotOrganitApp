package es.nlc.notorganitapp.Mongo

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

object MongoDBDataAPIClient {
    private const val API_KEY = "FFvJqmBD3bIfEmZ8pH2D4f56Q1PNDcq6HqG7xjG1xI1YdvqBtTd0TY1OwyHwbXK2"
    private const val BASE_URL = "https://eu-central-1.aws.data.mongodb-api.com/app/data-odqekzu/endpoint/data/v1/action"

    private val client = OkHttpClient()

    private suspend fun makeRequest(endpoint: String, jsonBody: String): String? {
        val body = jsonBody.toRequestBody()

        val request = Request.Builder()
            .url("$BASE_URL/$endpoint")
            .post(body)
            .addHeader("api-key", API_KEY)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "*/*")
            .addHeader("Access-Control-Request-Headers", "*")
            .build()

        Log.d("MongoDBDataAPIClient", "Request URL: ${request.url}")
        Log.d("MongoDBDataAPIClient", "Request Headers: ${request.headers}")
        Log.d("MongoDBDataAPIClient", "Request Body: $jsonBody")

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            if (response.isSuccessful) {
                responseBody
            } else {
                Log.e("MongoDBDataAPIClient", "Response not successful: ${response.code}")
                Log.e("MongoDBDataAPIClient", "Response body: $responseBody")
                null
            }
        }
    }

    suspend fun findMany(collection: String, database: String, dataSource: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "projection": {}
            }
        """.trimIndent()

        return makeRequest("find", jsonBody)
    }

    suspend fun findNotesByCategory(category: String, collection: String, database: String, dataSource: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": {"categoria": "$category"}
            }
        """.trimIndent()

        return makeRequest("find", jsonBody)
    }

    suspend fun insertOne(collection: String, database: String, dataSource: String, document: String): String? {
        val jsonBody = """
        {
            "collection":"$collection",
            "database":"$database",
            "dataSource":"$dataSource",
            "document": $document
        }
        """.trimIndent()

        Log.d("MongoDBDataAPIClient", "JSON Body: $jsonBody")

        return makeRequest("insertOne", jsonBody)
    }

    suspend fun deleteCategory(collection: String, database: String, dataSource: String, nomCat: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter":  {"Nom": "$nomCat"}
            }
        """.trimIndent()

        return makeRequest("deleteOne", jsonBody)
    }

    suspend fun deleteNotesByCategory(category: String, collection: String, database: String, dataSource: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": {"categoria": "$category"}
            }
        """.trimIndent()

        return makeRequest("deleteMany", jsonBody)
    }



    suspend fun deleteMany(collection: String, database: String, dataSource: String, filter: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": $filter
            }
        """.trimIndent()

        return makeRequest("deleteMany", jsonBody)
    }

    suspend fun updateOne(collection: String, database: String, dataSource: String, filter: String, update: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": $filter,
                "update": $update
            }
        """.trimIndent()

        return makeRequest("updateOne", jsonBody)
    }

    suspend fun updateNotesCategoria(collection: String, database: String, dataSource: String, filter: String, update: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": $filter,
                "update": $update
            }
        """.trimIndent()

        return makeRequest("updateMany", jsonBody)
    }
}

