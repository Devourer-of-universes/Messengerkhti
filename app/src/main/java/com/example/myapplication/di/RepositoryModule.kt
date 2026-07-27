// di/RepositoryModule.kt
package com.example.myapplication.di

import com.example.myapplication.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: com.example.myapplication.network.ApiService
    ): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: com.example.myapplication.network.ApiService
    ): UserRepository {
        return UserRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        apiService: com.example.myapplication.network.ApiService
    ): ChatRepository {
        return ChatRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        apiService: com.example.myapplication.network.ApiService
    ): TaskRepository {
        return TaskRepository(apiService)
    }
}