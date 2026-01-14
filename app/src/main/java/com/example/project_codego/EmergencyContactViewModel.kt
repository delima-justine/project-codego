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

    private val _allContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val allContacts: StateFlow<List<EmergencyContact>> = _allContacts.asStateFlow()

    private val _filteredContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val filteredContacts: StateFlow<List<EmergencyContact>> = _filteredContacts.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _currentPageContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val currentPageContacts: StateFlow<List<EmergencyContact>> = _currentPageContacts.asStateFlow()

    companion object {
        private const val PAGE_SIZE = 3
    }

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.allContacts.collect { contacts ->
                    _allContacts.value = contacts
                    updatePagination(contacts)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    private fun updatePagination(contacts: List<EmergencyContact>) {
        val totalPages = (contacts.size + PAGE_SIZE - 1) / PAGE_SIZE
        _totalPages.value = totalPages
        
        val startIndex = _currentPage.value * PAGE_SIZE
        val endIndex = minOf(startIndex + PAGE_SIZE, contacts.size)
        _currentPageContacts.value = contacts.subList(startIndex, endIndex)
    }

    fun searchContacts(query: String) {
        _searchQuery.value = query
        val filtered = if (query.isBlank()) {
            repository.allContacts.value
        } else {
            repository.searchContacts(query)
        }
        _filteredContacts.value = filtered
        updatePagination(filtered)
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value = _currentPage.value - 1
            updatePagination(_filteredContacts.value)
        }
    }

    fun nextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value = _currentPage.value + 1
            updatePagination(_filteredContacts.value)
        }
    }

    fun goToPage(page: Int) {
        if (page >= 0 && page < _totalPages.value) {
            _currentPage.value = page
            updatePagination(_filteredContacts.value)
        }
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
