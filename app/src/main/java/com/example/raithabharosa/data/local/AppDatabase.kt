package com.example.raitha_bharosa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import com.example.raitha_bharosa.data.local.entity.SeasonLogEntity

@Database(
    entities = [FarmerEntity::class, SoilReadingEntity::class, SeasonLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
    abstract fun soilReadingDao(): SoilReadingDao
    abstract fun seasonLogDao(): SeasonLogDao
}
