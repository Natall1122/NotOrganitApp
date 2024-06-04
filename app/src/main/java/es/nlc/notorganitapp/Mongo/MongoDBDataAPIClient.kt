package es.nlc.notorganitapp.Mongo

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MongoDBDataAPIClient {
    private const val API_KEY = "FFvJqmBD3bIfEmZ8pH2D4f56Q1PNDcq6HqG7xjG1xI1YdvqBtTd0TY1OwyHwbXK2"
    private const val BASE_URL = "https://eu-central-1.aws.data.mongodb-api.com/app/data-odqekzu/endpoint/data/v1/action"

    private val client = OkHttpClient()

    private suspend fun makeRequest(endpoint: String, jsonBody: String): String? {
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL/$endpoint")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Access-Control-Request-Headers", "*")
            .addHeader("api-key", API_KEY)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        }
    }

    suspend fun findOne(collection: String, database: String, dataSource: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "projection": {"_id": 1}
            }
        """.trimIndent()

        return makeRequest("findOne", jsonBody)
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

    suspend fun insertOne(collection: String, database: String, dataSource: String, document: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "document": $document
            }
        """.trimIndent()

        return try {
            makeRequest("insertOne", jsonBody)
        } catch (e: Exception) {
            Log.e("MongoDBDataAPIClient", "Error inserting document: ${e.message}")
            null
        }
    }

    suspend fun deleteOne(collection: String, database: String, dataSource: String, filter: String): String? {
        val jsonBody = """
            {
                "collection":"$collection",
                "database":"$database",
                "dataSource":"$dataSource",
                "filter": $filter
            }
        """.trimIndent()

        return makeRequest("deleteOne", jsonBody)
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

    suspend fun updateMany(collection: String, database: String, dataSource: String, filter: String, update: String): String? {
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

