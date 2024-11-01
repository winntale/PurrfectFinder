package com.example.purrfectfinder

import android.content.ContentValues
import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper

/*
class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "PurffectFinderDB", factory, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (id INT PRIMARY KEY," +
                "email TEXT NOT NULL," +
                "pass TEXT NOT NULL," +
                "secondname TEXT NOT NULL," +
                "firstname TEXT NOT NULL," +
                "middlename TEXT," +
                "birthday TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "gender TEXT NOT NULL);"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("email", user.email)
        values.put("pass", user.password)
        values.put("secondname", user.secondName)
        values.put("firstname", user.firstName)
        values.put("middlename", user.middleName)
        values.put("birthday", user.birthday)
        values.put("role", user.role)
        values.put("gender", user.gender)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }

    fun getUser(email: String, pass: String) : Boolean {
        val db = this.readableDatabase

        val result = db.rawQuery("SELECT * FROM users WHERE email = '$email' AND pass = '$pass'", null)
        return result.moveToFirst()
    }
}*/


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

    suspend fun getUser(email : String, password : String) : User? {
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
}