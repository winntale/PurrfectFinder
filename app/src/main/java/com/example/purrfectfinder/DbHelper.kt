package com.example.purrfectfinder

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.SerializableDataClasses.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch


class DbHelper () {
    fun getClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://apddnnxfhleknlnmxwqz.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFwZGRubnhmaGxla25sbm14d3F6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzAyMjY2MTQsImV4cCI6MjA0NTgwMjYxNH0.SfqbR3q5mLvL_1B-4uxoqAp_IpXTIV70-x_5Y5g68UU",
        ) {
            install(Postgrest)
        }
    }

    suspend fun insertUser(user: User) {
        try {
            val client = getClient()
            client
                .postgrest["Users"]  // Указываем таблицу, в которую будем вставлять
                .insert(
                    mapOf(
                        "email" to user.email,
                        "password" to user.password,
                        "secondName" to user.secondName,
                        "firstName" to user.firstName,
                        "middleName" to user.middleName,
                        "birthday" to user.birthday,
                        "role" to user.role,
                        "gender" to user.gender
                    )
                ) // Вставляем объект user

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

    suspend inline fun <reified T : Any> getData(tableName: String) : List<T> {
        val client = getClient()
        val supabaseResponse = client.postgrest[tableName].select()
        Log.e("supabase", supabaseResponse.decodeList<T>().toString())
        return supabaseResponse.decodeList<T>()
    }

    suspend fun getUser(email : String, password : String) : Int? {
        val client = getClient()
        return client.postgrest["Users"]
            .select()
            {
                filter {
                    eq("email", email)
                    eq("password", password)
                }
            }.decodeSingleOrNull<UserId>()
            ?.id
    }

    suspend fun getAllFavAds(id: Int) : List<Int> {
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

}

@kotlinx.serialization.Serializable
data class UserId(val id: Int?)
