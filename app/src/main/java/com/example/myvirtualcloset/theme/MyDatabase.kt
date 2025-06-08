package com.example.mvc.ui.theme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class MyDatabase(
    private var context: Context?,
    name: String? = "MVCDatabase",
    version: Int = 1,
    openParams: SQLiteDatabase.OpenParams
) : SQLiteOpenHelper(context, name, version, openParams) {

    private val TABLE_NAME: String = "Enthusiast"
    private val COLUMN_NAME: String = "NAME"
    private val COLUMN_EMAIL: String = "EMAIL"
    private val COLUMN_PASSWORD: String = "PASSWORD"

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $TABLE_NAME ($COLUMN_NAME VARCHAR, $COLUMN_EMAIL VARCHAR, $COLUMN_PASSWORD VARCHAR);"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addEnthusiast(name: String, email: String, password: String) {
        val sqlite = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_PASSWORD, password)

        val result = sqlite.insert(TABLE_NAME, null, contentValues)

        if (result == -1L) {
            context?.let {
                Toast.makeText(it, "Enthusiast not Registered. Check details and Try Again!", Toast.LENGTH_SHORT).show()
            }
        } else {
            context?.let {
                Toast.makeText(it, "Enthusiast Registered", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
