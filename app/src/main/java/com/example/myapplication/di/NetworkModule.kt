// di/NetworkModule.kt
package com.example.myapplication.di

import com.example.myapplication.network.ApiService
import com.example.myapplication.network.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }
}