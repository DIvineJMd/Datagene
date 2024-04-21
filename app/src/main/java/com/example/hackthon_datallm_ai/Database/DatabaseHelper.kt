package com.example.hackthon_datallm_ai.Database
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Geminidb.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Do nothing here because tables will be created dynamically
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here if needed
    }
    fun createTable(tableName: String, columnDefinitions: MutableList<Pair<String, String>>) {
        val db = writableDatabase
        val columns = columnDefinitions.joinToString(", ") { (columnName, dataType) ->
            // Map custom data types to SQLite data types
            val sqlDataType = when (dataType) {
                "string" -> "TEXT"
                "number" -> "INTEGER"
                "boolean" -> "INTEGER"
                "map" -> "TEXT"
                "array" -> "TEXT"
                "null" -> "NULL"
                "timestamp" -> "INTEGER"
                "primaryKey" -> "$columnName $dataType PRIMARY KEY"
                else -> throw IllegalArgumentException("Unsupported data type: $dataType")
            }
            "$columnName $sqlDataType"
        }
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $tableName ($columns)"
        db.execSQL(createTableQuery)
    }




    fun dropTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
    }

    fun insertData(tableName: String, values: ContentValues): Long {
        val db = writableDatabase
        return db.insert(tableName, null, values)
    }

    fun updateData(tableName: String, values: ContentValues, whereClause: String, whereArgs: Array<String>?): Int {
        val db = writableDatabase
        return db.update(tableName, values, whereClause, whereArgs)
    }
    // Function to execute custom query
    fun executeQuery(query: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery(query, null)
    }
    fun deleteData(tableName: String, whereClause: String, whereArgs: Array<String>?): Int {
        val db = writableDatabase
        return db.delete(tableName, whereClause, whereArgs)
    }
    fun getTableNames(): List<String> {
        val db = readableDatabase
        val tableNames = mutableListOf<String>()

        // Query the sqlite_master table for table names
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val tableName = cursor.getString(0)
                if (tableName != "android_metadata" && tableName != "sqlite_sequence") {
                    // Exclude system tables
                    tableNames.add(tableName)
                }
            }
        }

        return tableNames
    }

    fun queryData(tableName: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = readableDatabase
        return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder)
    }
}
