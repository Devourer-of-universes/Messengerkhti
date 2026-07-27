package com.example.myapplication.screen.Contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts: StateFlow<List<User>> = _contacts.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadContacts(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.getContacts().collect { contactsList ->
                _contacts.value = contactsList
                _isLoading.value = false
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            userRepository.getUsers().collect { usersList ->
                _allUsers.value = usersList
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}