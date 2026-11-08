package es.angel.mygarage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "users.db"
        const val DB_VERSION = 1

        const val USERS = "users"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
    }

    //Create the table users
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    //If we put a new version it will erase the old one
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USERS")
        onCreate(db)
    }

    //Will return the new id of the user or -1 if it fails
    fun insert(user: User): Long {
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username)
            put(COL_PASSWORD, user.password)
        }
        return writableDatabase.insert(USERS, null, values)
    }

    //Return the user found on the database or null, if it couldn't find it
    fun findUser(username : String) : User? {
        var user : User? = null
        readableDatabase.rawQuery("SELECT * FROM $USERS WHERE $COL_USERNAME = ?", arrayOf(username)).use{
            if (it.moveToFirst()){
                user = User(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(COL_USERNAME)),
                    password = it.getString(it.getColumnIndexOrThrow(COL_PASSWORD))
                )
            }
        }
        return user
    }

    fun listAll(): List<User> {
        val list = mutableListOf<User>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $USERS ORDER BY $COL_ID", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    User(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        username = it.getString(it.getColumnIndexOrThrow(COL_USERNAME)),
                        password = it.getString(it.getColumnIndexOrThrow(COL_PASSWORD))
                    )
                )
            }
        }
        return list
    }
}