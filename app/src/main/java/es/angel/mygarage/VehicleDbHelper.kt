package es.angel.mygarage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VehicleDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "vehicles.db"
        const val DB_VERSION = 1

        const val VEHICLES = "vehicles"
        const val COL_ID = "id"
        const val COL_PLATE = "plate"
        const val COL_BRAND = "brand"
        const val COL_MODEL = "model"
        const val COL_YEAR = "year"
        const val COL_MILEAGE = "mileage"
        const val COL_USER_ID = "userId"
    }

    //Create the table vehicles
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $VEHICLES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PLATE TEXT NOT NULL UNIQUE,
                $COL_BRAND TEXT NOT NULL,
                $COL_MODEL TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL,
                $COL_MILEAGE INTEGER NOT NULL,
                $COL_USER_ID INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    //If we put a new version it will erase the old one
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $VEHICLES")
        onCreate(db)
    }

    //Will return the new id of the vehicle or -1 if it fails
    fun insert(vehicle: Vehicle): Long {
        val values = ContentValues().apply {
            put(COL_PLATE, vehicle.plate)
            put(COL_BRAND, vehicle.brand)
            put(COL_MODEL, vehicle.model)
            put(COL_YEAR, vehicle.year)
            put(COL_MILEAGE, vehicle.mileage)
            put(COL_USER_ID, vehicle.userId)
        }
        return writableDatabase.insert(VEHICLES, null, values)
    }

    //Will return the number of columns affected or 0 if it doesn't update anything
    fun update (oldVehicle: Vehicle, newVehicle: Vehicle): Int{
        val values = ContentValues().apply {
            put(COL_PLATE, newVehicle.plate)
            put(COL_BRAND, newVehicle.brand)
            put(COL_MODEL, newVehicle.model)
            put(COL_YEAR, newVehicle.year)
            put(COL_MILEAGE, newVehicle.mileage)
            put(COL_USER_ID, newVehicle.userId)
        }
        return writableDatabase.update(VEHICLES, values, COL_ID+"=?", arrayOf(oldVehicle.id.toString()))
    }

    //Will return the number of rows deleted (1 if the vehicle is deleted or 0 it cannot remove any)
    fun remove(plate: String) : Int{
        return writableDatabase.delete(VEHICLES, "$COL_PLATE =?", arrayOf(plate))
    }

    //Return the vehicle found on the database or null, if it couldn't find it
    fun findVehicle(plate : String) : Vehicle? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM $VEHICLES WHERE $COL_PLATE = ?", arrayOf(plate))
        var vehicle: Vehicle? = null
        if (cursor.count == 0){
            return null
        } else{
            cursor.moveToFirst()
            cursor.use {
                vehicle = Vehicle(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    plate = it.getString(it.getColumnIndexOrThrow(COL_PLATE)),
                    brand = it.getString(it.getColumnIndexOrThrow(COL_BRAND)),
                    model = it.getString(it.getColumnIndexOrThrow(COL_MODEL)),
                    year = it.getInt(it.getColumnIndexOrThrow(COL_YEAR)),
                    mileage = it.getInt(it.getColumnIndexOrThrow(COL_MILEAGE)),
                    userId = it.getInt(it.getColumnIndexOrThrow(COL_USER_ID))
                )
            }
        }
        return vehicle
    }

    //Return the vehicle found on the database or null, if it couldn't find it
    fun findVehicle(id : Int) : Vehicle? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM $VEHICLES WHERE $COL_ID = ? ORDER BY $COL_ID", arrayOf(id.toString()))
        var vehicle: Vehicle? = null
        cursor.use {
            vehicle = Vehicle(
                id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                plate = it.getString(it.getColumnIndexOrThrow(COL_PLATE)),
                brand = it.getString(it.getColumnIndexOrThrow(COL_BRAND)),
                model = it.getString(it.getColumnIndexOrThrow(COL_MODEL)),
                year = it.getInt(it.getColumnIndexOrThrow(COL_YEAR)),
                mileage = it.getInt(it.getColumnIndexOrThrow(COL_MILEAGE)),
                userId = it.getInt(it.getColumnIndexOrThrow(COL_USER_ID))
            )
        }
        return vehicle
    }

    fun listAll(userId : Int): List<Vehicle> {
        val list = mutableListOf<Vehicle>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $VEHICLES WHERE $COL_USER_ID = ? ORDER BY $COL_ID", arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Vehicle(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        plate = it.getString(it.getColumnIndexOrThrow(COL_PLATE)),
                        brand = it.getString(it.getColumnIndexOrThrow(COL_BRAND)),
                        model = it.getString(it.getColumnIndexOrThrow(COL_MODEL)),
                        year = it.getInt(it.getColumnIndexOrThrow(COL_YEAR)),
                        mileage = it.getInt(it.getColumnIndexOrThrow(COL_MILEAGE)),
                        userId = it.getInt(it.getColumnIndexOrThrow(COL_USER_ID))
                    )
                )
            }
        }
        return list
    }
}