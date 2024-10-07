package com.example.purrfectfinder

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "PurffectFinderDB", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (id INT PRIMARY KEY, email TEXT, pass TEXT, secondmame TEXT," +
                "firstname TEXT, middlename TEXT, birtday TEXT, role TEXT, gender TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put("EMAIL", user.email)
        values.put("PASS", user.password)
        values.put("SECONDNAME", user.secondName)
        values.put("FIRSTNAME", user.firstName)
        values.put("MIDDLENAME", user.middleName)
        values.put("BIRTHDAY", user.birthday)
        values.put("ROLE", user.role)
        values.put("GENDER", user.gender)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }


}