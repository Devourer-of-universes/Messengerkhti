// data/repository/UserRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.model.User
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getUsers(): Flow<List<User>> = flow {
        try {
            val response = apiService.getUsers()
            emit(response.users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getContacts(): Flow<List<User>> = flow {
        try {
            val response = apiService.getContacts()
            emit(response.contacts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addContact(userId: Int): Boolean {
        return try {
            val response = apiService.addContact(userId)
            response.success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeContact(userId: Int): Boolean {
        return try {
            val response = apiService.removeContact(userId)
            response.success
        } catch (e: Exception) {
            false
        }
    }
}