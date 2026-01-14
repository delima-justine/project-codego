package com.example.project_codego

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EmergencyContactDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "emergency_contacts.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EMERGENCY_CONTACTS = "emergency_contacts"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_ICON = "icon"
        const val COLUMN_CATEGORY = "category"
    }

    init {
        // Copy pre-created database from assets
        copyDatabaseFromAssets()
    }

    private fun copyDatabaseFromAssets() {
        Log.d("DatabaseHelper", "=== STARTING DATABASE COPY ===")
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        val dbDir = File(dbFile.parent)
        
        Log.d("DatabaseHelper", "Database file path: ${dbFile.absolutePath}")
        Log.d("DatabaseHelper", "Database directory: ${dbDir.absolutePath}")
        Log.d("DatabaseHelper", "Database file exists: ${dbFile.exists()}")
        Log.d("DatabaseHelper", "Database directory exists: ${dbDir.exists()}")
        
        // Create database directory if it doesn't exist
        if (!dbDir.exists()) {
            Log.d("DatabaseHelper", "Creating database directory")
            dbDir.mkdirs()
        }
        
        if (!dbFile.exists()) {
            try {
                Log.d("DatabaseHelper", "Database file doesn't exist, copying from assets/database/")
                // Copy database from assets/database/ subdirectory
                context.assets.open("database/$DATABASE_NAME").use { inputStream ->
                    FileOutputStream(dbFile).use { outputStream ->
                        val bytesCopied = inputStream.copyTo(outputStream)
                        Log.d("DatabaseHelper", "Copied $bytesCopied bytes from assets/database/")
                    }
                }
                Log.d("DatabaseHelper", "Database copied from assets successfully")
                Log.d("DatabaseHelper", "Final database path: ${dbFile.absolutePath}")
                Log.d("DatabaseHelper", "Final database exists: ${dbFile.exists()}")
                Log.d("DatabaseHelper", "Final database size: ${dbFile.length()} bytes")
            } catch (e: IOException) {
                Log.e("DatabaseHelper", "Error copying database from assets/database/", e)
            }
        } else {
            Log.d("DatabaseHelper", "Database already exists at: ${dbFile.absolutePath}")
            Log.d("DatabaseHelper", "Existing database size: ${dbFile.length()} bytes")
        }
        Log.d("DatabaseHelper", "=== FINISHED DATABASE COPY ===")
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Using pre-created database from assets")
        // Fallback: create table if it doesn't exist
        val CREATE_TABLE_EMERGENCY_CONTACTS = """
            CREATE TABLE IF NOT EXISTS $TABLE_EMERGENCY_CONTACTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE_NUMBER TEXT NOT NULL,
                $COLUMN_ICON TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(CREATE_TABLE_EMERGENCY_CONTACTS)
        Log.d("DatabaseHelper", "Table creation fallback executed")
        
        // Ultimate fallback: insert data directly if table is empty
        insertDataDirectly(db)
    }
    
    private fun insertDataDirectly(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "=== STARTING DIRECT DATA INSERTION ===")
        // Check if table is empty
        val cursor = db.query(TABLE_EMERGENCY_CONTACTS, null, null, null, null, null, null)
        val isEmpty = cursor.count == 0
        cursor.close()
        
        if (isEmpty) {
            Log.d("DatabaseHelper", "Table is empty, inserting emergency contacts directly")
            
            val emergencyContacts = listOf(
                "INSERT INTO $TABLE_EMERGENCY_CONTACTS ($COLUMN_NAME, $COLUMN_PHONE_NUMBER, $COLUMN_ICON, $COLUMN_CATEGORY) VALUES ('National Emergency Hotline', '911', 'emergency', 'National')",
                "INSERT INTO $TABLE_EMERGENCY_CONTACTS ($COLUMN_NAME, $COLUMN_PHONE_NUMBER, $COLUMN_ICON, $COLUMN_CATEGORY) VALUES ('Philippine Coast Guard', '(02) 8527-3877', 'coast_guard', 'Maritime')",
                "INSERT INTO $TABLE_EMERGENCY_CONTACTS ($COLUMN_NAME, $COLUMN_PHONE_NUMBER, $COLUMN_ICON, $COLUMN_CATEGORY) VALUES ('Bureau of Fire Protection', '(02) 8426-0219, (02) 8426-0246', 'fire', 'Fire')",
                "INSERT INTO $TABLE_EMERGENCY_CONTACTS ($COLUMN_NAME, $COLUMN_PHONE_NUMBER, $COLUMN_ICON, $COLUMN_CATEGORY) VALUES ('Philippine Red Cross', '143 or (02) 8527-8385 to 95', 'red_cross', 'Medical')",
                "INSERT INTO $TABLE_EMERGENCY_CONTACTS ($COLUMN_NAME, $COLUMN_PHONE_NUMBER, $COLUMN_ICON, $COLUMN_CATEGORY) VALUES ('Department of Public Works and Highways', '165-02', 'public_works', 'Infrastructure')"
            )

            Log.d("DatabaseHelper", "About to insert ${emergencyContacts.size} contacts")
            emergencyContacts.forEachIndexed { index, query ->
                try {
                    db.execSQL(query)
                    Log.d("DatabaseHelper", "Successfully inserted contact #$index: $query")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "Error inserting contact #$index", e)
                }
            }
            
            // Verify insertion
            val verifyCursor = db.query(TABLE_EMERGENCY_CONTACTS, null, null, null, null, null, null)
            Log.d("DatabaseHelper", "Final record count after insertion: ${verifyCursor.count}")
            verifyCursor.close()
            
            Log.d("DatabaseHelper", "Inserted ${emergencyContacts.size} emergency contacts directly")
        } else {
            Log.d("DatabaseHelper", "Table already has data, skipping direct insertion")
        }
        Log.d("DatabaseHelper", "=== FINISHED DIRECT DATA INSERTION ===")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")
        // For manual database management, you might want to handle upgrades differently
    }
}
