package com.example.raitha_bharosa.di

import android.content.Context
import androidx.room.Room
import com.example.raitha_bharosa.data.local.AppDatabase
import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.local.dao.UserDao
import com.example.raitha_bharosa.data.remote.TwilioApi
import com.example.raitha_bharosa.data.repository.AuthRepository
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.data.repository.SoilRepository
import com.example.raitha_bharosa.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "raitha_bharosa_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFarmerDao(database: AppDatabase): FarmerDao {
        return database.farmerDao()
    }

    @Provides
    fun provideSeasonLogDao(database: AppDatabase): SeasonLogDao {
        return database.seasonLogDao()
    }

    @Provides
    fun provideSoilReadingDao(database: AppDatabase): SoilReadingDao {
        return database.soilReadingDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTwilioApi(): TwilioApi {
        return TwilioApi()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        twilioApi: TwilioApi
    ): AuthRepository {
        return AuthRepository(userDao, twilioApi)
    }

    @Provides
    @Singleton
    fun provideFarmerRepository(
        farmerDao: FarmerDao,
        seasonLogDao: SeasonLogDao
    ): FarmerRepository {
        return FarmerRepository(farmerDao, seasonLogDao)
    }

    @Provides
    @Singleton
    fun provideSoilRepository(
        soilReadingDao: SoilReadingDao
    ): SoilRepository {
        return SoilRepository(soilReadingDao)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(): WeatherRepository {
        return WeatherRepository()
    }
}
