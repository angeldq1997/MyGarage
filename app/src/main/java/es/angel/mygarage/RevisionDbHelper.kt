package es.angel.mygarage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import es.angel.mygarage.VehicleDbHelper.Companion.COL_BRAND
import es.angel.mygarage.VehicleDbHelper.Companion.COL_MILEAGE
import es.angel.mygarage.VehicleDbHelper.Companion.COL_MODEL
import es.angel.mygarage.VehicleDbHelper.Companion.COL_USER_ID
import es.angel.mygarage.VehicleDbHelper.Companion.COL_YEAR
import es.angel.mygarage.VehicleDbHelper.Companion.VEHICLES

class RevisionDbHelper (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "revisions.db"
        const val DB_VERSION = 1

        const val REVISIONS = "revisions"
        const val COL_ID = "id"
        const val COL_PLATE = "plate"
        const val COL_DESCRIPTION = "description"
        const val COL_TITLE = "title"
        const val COL_WORKSHOP = "workshop"
        const val COL_DATE = "date"
    }

    //Create the table revisions
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $REVISIONS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PLATE TEXT NOT NULL,
                $COL_DESCRIPTION TEXT NOT NULL,
                $COL_TITLE TEXT NOT NULL,
                $COL_WORKSHOP TEXT,
                $COL_DATE TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    //If we put a new version it will erase the old one
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $REVISIONS")
        onCreate(db)
    }

    //Will return the number of columns affected or 0 if it doesn't update anything
    fun update (oldRevisionId: Int, newRevision: Revision): Int{
        val values = ContentValues().apply {
            put(COL_PLATE, newRevision.plate)
            put(COL_DESCRIPTION, newRevision.description)
            put(COL_TITLE, newRevision.title)
            put(COL_WORKSHOP, newRevision.workshop)
            put(COL_DATE, newRevision.date)
        }
        return writableDatabase.update(REVISIONS, values, VehicleDbHelper.COL_ID +"=?", arrayOf(oldRevisionId.toString()))
    }

    //Will return the new id of the revision or -1 if it fails
    fun insert(revision: Revision): Long {
        val values = ContentValues().apply {
            put(COL_PLATE, revision.plate)
            put(COL_DESCRIPTION, revision.description)
            put(COL_TITLE, revision.title)
            put(COL_WORKSHOP, revision.workshop)
            put(COL_DATE, revision.date)
        }
        return writableDatabase.insert(REVISIONS, null, values)
    }

    //Will return the number of rows deleted (1 if the revision is deleted or 0 it cannot remove any)
    fun remove(id: Long) : Int{
        return writableDatabase.delete(REVISIONS, "$COL_ID =?", arrayOf(id.toString()))
    }

    //Return the revision found on the database or null, if it couldn't find it
    fun findRevision(id : Int) : Revision? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM $REVISIONS WHERE $COL_ID = ?", arrayOf(id.toString()))
        var revision: Revision? = null
        cursor.use {
            revision = Revision(
                id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                plate = it.getString(it.getColumnIndexOrThrow(COL_PLATE)),
                description = it.getString(it.getColumnIndexOrThrow(COL_DESCRIPTION)),
                title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                workshop = it.getString(it.getColumnIndexOrThrow(COL_WORKSHOP)),
                date = it.getString(it.getColumnIndexOrThrow(COL_DATE))
            )
        }
        return revision
    }

    fun listRevision(plate : String) : List<Revision> {
        val list = mutableListOf<Revision>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $REVISIONS WHERE $COL_PLATE = ? ORDER BY $COL_ID", arrayOf(plate))
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Revision(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        plate = it.getString(it.getColumnIndexOrThrow(COL_PLATE)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                        workshop = it.getString(it.getColumnIndexOrThrow(COL_WORKSHOP)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_DATE))
                    )
                )
            }
        }
        return list
    }
}