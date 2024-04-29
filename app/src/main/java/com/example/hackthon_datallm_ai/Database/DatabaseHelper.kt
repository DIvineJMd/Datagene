package com.example.hackthon_datallm_ai.Database
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale

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
    @SuppressLint("Range")
    fun getTableAttributes(tableName: String): List<Pair<String, String>> {
        val db = readableDatabase
        val attributes = mutableListOf<Pair<String, String>>()

        // Query the PRAGMA table_info for column information
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val columnName = cursor.getString(cursor.getColumnIndex("name"))
                val columnType = cursor.getString(cursor.getColumnIndex("type"))
                attributes.add(Pair(columnName, columnType))
            }
        }

        return attributes
    }

    fun queryData(tableName: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = readableDatabase
        return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder)
    }


    fun convertSqlToContentValues(sqlQuery: String): ContentValues? {
        val contentValues = ContentValues()

        // Detecting the type of SQL operation (INSERT, UPDATE, DELETE)
        val operation = sqlQuery.substringBefore(" ").uppercase(Locale.ROOT)
        println(operation)
        return when (operation) {
            "INSERT" -> {

                convertInsertToContentValues(sqlQuery)
            }
            "UPDATE" -> convertUpdateToContentValues(sqlQuery)
//            "Delete" ->
            else -> null
        }
    }
    private fun convertInsertToContentValues(sqlQuery: String): ContentValues? {
        val contentValues = ContentValues()

        // Extracting table name
        val tableNameRegex = "(?<=INSERT INTO )\\w+".toRegex()
        val tableNameMatch = tableNameRegex.find(sqlQuery)
        val tableName = tableNameMatch?.value ?: return null
        println(tableName)

        val s = sqlQuery
        val n = s.length
        println(s)
        println(n)
        var i = 0
        var keyflag = true
        var keystr = ""
        var valuesstr = ""

        while (i < n) {
            if (s[i] == '(' && keyflag) {
                i++
                while (s[i] != ')') {
                    keystr += s[i]
                    i++
                }
                keyflag = false
            } else if (s[i] == '(' && !keyflag) {
                i++
                while (s[i] != ')') {
                    valuesstr += s[i]
                    i++
                }
                keyflag = false
            }
            i++
        }

        var k = 0
        val keyarr = mutableListOf<String>()
        while (k < keystr.length) {
            var key = ""
            while (k < keystr.length && keystr[k] != ',' ) {
                key += keystr[k]
                k++
            }
            k++
            keyarr.add(key)
            k++
        }

        var v = 0
        val valarr = mutableListOf<String>()
        while (v < valuesstr.length) {
            var value = ""
            while (v < valuesstr.length && valuesstr[v] != ',' ) {
                value += valuesstr[v]
                v++
            }
            v++
            valarr.add(value)
            v++
        }

        val mp = mutableMapOf<String, String>()
        for (index in keyarr.indices) {
            var key = keyarr[index];
            var value = valarr[index];
            contentValues.put(key, value)
        }

        return contentValues
    }


    private fun convertUpdateToContentValues(sqlQuery: String): ContentValues? {
        val contentValues = ContentValues()

        // Extracting table name
        val tableNameRegex = "(?<=UPDATE )\\w+".toRegex()
        val tableNameMatch = tableNameRegex.find(sqlQuery)
        val tableName = tableNameMatch?.value ?: return null

        // Extracting SET clause
        val setClauseRegex = "(?<=SET ).+".toRegex()
        val setClauseMatch = setClauseRegex.find(sqlQuery)
        val setClause = setClauseMatch?.value ?: return null

        val keyValuePairs = setClause.split(", ")
        for (keyValuePair in keyValuePairs) {
            val splitPair = keyValuePair.split("=")
            if (splitPair.size == 2) {
                val key = splitPair[0].trim()
                val value = splitPair[1].trim()
                contentValues.put(key, value)
            }
        }

        return contentValues
    }

}
