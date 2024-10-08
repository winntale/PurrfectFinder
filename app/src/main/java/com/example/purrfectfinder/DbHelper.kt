package com.example.purrfectfinder

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
}