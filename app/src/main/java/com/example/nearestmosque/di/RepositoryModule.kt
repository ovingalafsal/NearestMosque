package com.example.nearestmosque.di

import com.example.nearestmosque.data.remote.GoogleMapsApi
import com.example.nearestmosque.data.repository.MosqueRepositoryImpl
import com.example.nearestmosque.domain.repository.MosqueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMosqueRepository(
        api: GoogleMapsApi,
        @Named("google_maps_api_key") apiKey: String
    ): MosqueRepository {
        return MosqueRepositoryImpl(api, apiKey)
    }
}
