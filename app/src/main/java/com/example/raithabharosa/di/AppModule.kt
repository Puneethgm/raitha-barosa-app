package com.example.raitha_bharosa.di

import android.content.Context
import androidx.room.Room
import com.example.raitha_bharosa.data.local.AppDatabase
import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.remote.OpenMeteoForecastApi
import com.example.raitha_bharosa.data.remote.OpenMeteoGeocodingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "raitha_bharosa_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFarmerDao(database: AppDatabase): FarmerDao {
        return database.farmerDao()
    }

    @Provides
    @Singleton
    fun provideSoilReadingDao(database: AppDatabase): SoilReadingDao {
        return database.soilReadingDao()
    }

    @Provides
    @Singleton
    fun provideSeasonLogDao(database: AppDatabase): SeasonLogDao {
        return database.seasonLogDao()
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(): OpenMeteoGeocodingApi {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoGeocodingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideForecastApi(): OpenMeteoForecastApi {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoForecastApi::class.java)
    }
}
