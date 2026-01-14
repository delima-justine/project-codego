package com.example.project_codego

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmergencyContactRepository(context: Context) {

    private val databaseHelper = EmergencyContactDatabaseHelper(context)
    private val readableDatabase = databaseHelper.readableDatabase
    private val writableDatabase = databaseHelper.writableDatabase

    private val _allContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val allContacts: StateFlow<List<EmergencyContact>> = _allContacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val contacts = mutableListOf<EmergencyContact>()
        val cursor = readableDatabase.query(
            EmergencyContactDatabaseHelper.TABLE_EMERGENCY_CONTACTS,
            null, null, null, null, null, null
        )

        Log.d("Repository", "Loading contacts from database")
        Log.d("Repository", "Cursor count: ${cursor?.count}")
        
        cursor?.use {
            while (it.moveToNext()) {
                val contact = EmergencyContact(
                    id = it.getLong(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_NAME)),
                    phoneNumber = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_PHONE_NUMBER)),
                    icon = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_ICON)),
                    category = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_CATEGORY))
                )
                contacts.add(contact)
                Log.d("Repository", "Loaded contact: ${contact.name}")
            }
        }
        
        Log.d("Repository", "Total contacts loaded: ${contacts.size}")
        _allContacts.value = contacts
    }

    fun insertContact(contact: EmergencyContact): Long {
        val values = ContentValues().apply {
            put(EmergencyContactDatabaseHelper.COLUMN_NAME, contact.name)
            put(EmergencyContactDatabaseHelper.COLUMN_PHONE_NUMBER, contact.phoneNumber)
            put(EmergencyContactDatabaseHelper.COLUMN_ICON, contact.icon)
            put(EmergencyContactDatabaseHelper.COLUMN_CATEGORY, contact.category)
        }
        
        val result = writableDatabase.insert(
            EmergencyContactDatabaseHelper.TABLE_EMERGENCY_CONTACTS,
            null,
            values
        )
        
        loadContacts() // Refresh the data
        return result
    }

    fun updateContact(contact: EmergencyContact): Int {
        val values = ContentValues().apply {
            put(EmergencyContactDatabaseHelper.COLUMN_NAME, contact.name)
            put(EmergencyContactDatabaseHelper.COLUMN_PHONE_NUMBER, contact.phoneNumber)
            put(EmergencyContactDatabaseHelper.COLUMN_ICON, contact.icon)
            put(EmergencyContactDatabaseHelper.COLUMN_CATEGORY, contact.category)
        }
        
        val result = writableDatabase.update(
            EmergencyContactDatabaseHelper.TABLE_EMERGENCY_CONTACTS,
            values,
            "${EmergencyContactDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
        
        loadContacts() // Refresh the data
        return result
    }

    fun deleteContact(contactId: Long): Int {
        val result = writableDatabase.delete(
            EmergencyContactDatabaseHelper.TABLE_EMERGENCY_CONTACTS,
            "${EmergencyContactDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(contactId.toString())
        )
        
        loadContacts() // Refresh the data
        return result
    }

    fun searchContacts(query: String): List<EmergencyContact> {
        val contacts = mutableListOf<EmergencyContact>()
        val selection = "${EmergencyContactDatabaseHelper.COLUMN_NAME} LIKE ? OR ${EmergencyContactDatabaseHelper.COLUMN_PHONE_NUMBER} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        
        val cursor = readableDatabase.query(
            EmergencyContactDatabaseHelper.TABLE_EMERGENCY_CONTACTS,
            null, selection, selectionArgs, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val contact = EmergencyContact(
                    id = it.getLong(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_NAME)),
                    phoneNumber = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_PHONE_NUMBER)),
                    icon = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_ICON)),
                    category = it.getString(it.getColumnIndexOrThrow(EmergencyContactDatabaseHelper.COLUMN_CATEGORY))
                )
                contacts.add(contact)
            }
        }
        
        return contacts
    }

    fun close() {
        readableDatabase.close()
        writableDatabase.close()
        databaseHelper.close()
    }
}
