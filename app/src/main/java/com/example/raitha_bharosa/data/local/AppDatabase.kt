package com.example.raitha_bharosa.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.local.dao.UserDao
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import com.example.raitha_bharosa.data.local.entity.SeasonLogEntity
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import com.example.raitha_bharosa.data.local.entity.UserEntity

@Database(
    entities = [
        FarmerEntity::class,
        SeasonLogEntity::class,
        SoilReadingEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
    abstract fun seasonLogDao(): SeasonLogDao
    abstract fun soilReadingDao(): SoilReadingDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raitha_bharosa_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
