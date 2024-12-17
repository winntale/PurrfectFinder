package com.example.purrfectfinder

import android.util.Log
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.SerializableDataClasses.Post
import com.example.purrfectfinder.SerializableDataClasses.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes


class DbHelper () {
    fun getClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://apddnnxfhleknlnmxwqz.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFwZGRubnhmaGxla25sbm14d3F6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzAyMjY2MTQsImV4cCI6MjA0NTgwMjYxNH0.SfqbR3q5mLvL_1B-4uxoqAp_IpXTIV70-x_5Y5g68UU",
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
        }
    }

    suspend fun insertUser(user: User) {
        try {
            val client = getClient()

            client
                .postgrest["Users"]  // Указываем таблицу, в которую будем вставлять
                .insert(
                    mapOf(
                        "uuid" to user.uuid,
                        "email" to user.email,
                        "password" to user.password,
                        "secondName" to user.secondName,
                        "firstName" to user.firstName,
                        "middleName" to user.middleName,
                        "birthday" to user.birthday,
                        "role" to user.role,
                        "gender" to user.gender,
                        "createdAt" to user.createdAt,
                        "pfp" to user.pfp
                    )
                ) // добавление объект user в бд

        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    suspend fun insertAdvertisement(createdAd: Advertisement) {
        try {
            val client = getClient()

            client
                .postgrest["Advertisements"]
                .insert(
                    createdAd
                )

        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    suspend fun insertPost(createdPost: Post) {
        try {
            val client = getClient()

            client
                .postgrest["Posts"]
                .insert(
                    createdPost
                )

        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    suspend fun insertFavourite(userId: Int, advertisementId: Int) {
        try {
            val client = getClient()
            client
                .postgrest["Favourites"]
                .insert(
                    mapOf(
                        "userId" to userId,
                        "advertisementId" to advertisementId
                    )
                )

        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    suspend fun deleteFavourite(userId: Int, advertisementId: Int) {
        try {
            val client = getClient()
            client
                .postgrest["Favourites"]
                .delete {
                    filter {
                        eq("userId", userId)
                        eq("advertisementId", advertisementId)
                    }
                }
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
        }
    }

    suspend inline fun <reified T : Any> getAllData(tableName: String): List<T> {
        val client = getClient()
        val supabaseResponse = client.postgrest[tableName].select()
        Log.e("supabase", supabaseResponse.decodeList<T>().toString())
        return supabaseResponse.decodeList<T>()
    }

    suspend fun getUser(email: String, password: String): User? {
        val client = getClient()
        return client.postgrest["Users"]
            .select()
            {
                filter {
                    eq("email", email)
                    eq("password", password)
                }
            }.decodeSingleOrNull<User>()
    }

    suspend fun getAllFavAds(id: Int): List<Int> {
        val client = getClient()
        val data = client.postgrest["Favourites"]
            .select(columns = Columns.list("advertisementId"))
            {
                filter {
                    eq("userId", id)
                }
            }.decodeList<Map<String, Int>>()
            .mapNotNull { it["advertisementId"] }

        Log.e("Favourites", data.toString())
        return data
    }

    suspend fun getAllPosts(id: Int): List<Post> {
        val client = getClient()
        val data = client.postgrest["Posts"]
            .select()
            {
                filter {
                    eq("sellerId", id)
                }
            }.decodeList<Post>()

        return data
    }

    suspend fun getAllFilters(filterName: String): List<String> {
        var client = getClient()

        val data = client.postgrest[filterName]
            .select(columns = Columns.list("name"))
            .decodeList<Map<String, String>>()
            .mapNotNull { it["name"] }

        Log.e(filterName, data.toString())
        return data
    }

    suspend fun isUserAuthenticated(): Boolean {
        val client = getClient()

        client.auth.signInWith(Email) {
            email = MainActivity.currentUserEmail!!
            password = MainActivity.currentUserPassword!!
        }

        val session = client.auth.currentSessionOrNull()
        Log.e("session", session.toString())
        return session != null
    }

    suspend fun uploadFile(client: SupabaseClient, bucketName: String, folderName: String, fileName: String, byteArray: ByteArray): String {

        val bucket = client.storage[bucketName]
        bucket.upload("$folderName/$fileName.jpg", byteArray)

        val projectUrl = "https://apddnnxfhleknlnmxwqz.supabase.co"
        return "$projectUrl/storage/v1/object/public/$bucketName/$folderName/$fileName.jpg"
    }

    suspend fun readFile(client: SupabaseClient, bucketName: String, fileName: String, onImageUrlRetrieved:(url: String) -> Unit,) {
        val bucket = client.storage[bucketName]
        val url = bucket.createSignedUrl("public/$fileName.jpg", expiresIn = 20.minutes)
        onImageUrlRetrieved(url)
    }

}