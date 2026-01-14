package com.example.project_codego

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmergencyContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmergencyContactRepository(application)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val filteredContacts: StateFlow<List<EmergencyContact>> = _filteredContacts.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.allContacts.collect { contacts ->
                    _filteredContacts.value = contacts
                    _totalPages.value = 1 // Simple pagination for now
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun searchContacts(query: String) {
        _searchQuery.value = query
        _filteredContacts.value = if (query.isBlank()) {
            repository.allContacts.value
        } else {
            repository.searchContacts(query)
        }
    }

    fun previousPage() {
        // Simple pagination - just for UI demo
    }

    fun nextPage() {
        // Simple pagination - just for UI demo
    }

    fun insertContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.insertContact(contact)
        }
    }

    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}
