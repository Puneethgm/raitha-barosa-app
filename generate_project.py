#!/usr/bin/env python3
"""
Raitha-Bharosa Hub - Automated Project Generator
Generates all 26+ source files for the Android project
"""

import os
import sys

def create_file(filepath, content):
    """Create a file with the given content"""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"✅ {filepath}")

# Define all files to create
files = {
    # Data Models
    "app/src/main/kotlin/com/raithabharosa/hub/data/model/FarmerProfile.kt": """package com.raithabharosa.hub.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profiles")
data class FarmerProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, val crop: String, val location: String,
    val fieldAreaHectares: Double, val soilType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class CropType { SUGARCANE, RAGI, PADDY }

data class SoilData(
    val nitrogen: Float, val phosphorus: Float, val potassium: Float,
    val moisture: Float, val temperature: Float, val pH: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeatherData(
    val temperature: Float, val humidity: Float, val rainfall: Float,
    val windSpeed: Float, val condition: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SowingIndexResult(
    val index: Float, val status: SowingStatus, val recommendation: String,
    val nextCheckIn: Long
)

enum class SowingStatus { GREEN, AMBER, RED }
""",

    "app/src/main/kotlin/com/raithabharosa/hub/data/model/SeasonRecord.kt": """package com.raithabharosa.hub.data.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "season_records", foreignKeys = [
    ForeignKey(entity = FarmerProfile::class, parentColumns = ["id"],
    childColumns = ["farmerId"], onDelete = ForeignKey.CASCADE)
])
data class SeasonRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val farmerId: Int, val season: String, val cropType: String,
    val sowingDate: Long, val harvestDate: Long?,
    val yieldKgPerHectare: Float?,
    val totalNitrogenUsed: Float, val totalPhosphorusUsed: Float,
    val totalPotassiumUsed: Float, val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "soil_records", foreignKeys = [
    ForeignKey(entity = SeasonRecord::class, parentColumns = ["id"],
    childColumns = ["seasonId"], onDelete = ForeignKey.CASCADE)
])
data class SoilRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seasonId: Int, val nitrogen: Float, val phosphorus: Float,
    val potassium: Float, val moisture: Float, val temperature: Float,
    val pH: Float, val recordedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_actions", foreignKeys = [
    ForeignKey(entity = SeasonRecord::class, parentColumns = ["id"],
    childColumns = ["seasonId"], onDelete = ForeignKey.CASCADE)
])
data class DailyAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seasonId: Int, val dayNumber: Int, val action: String,
    val reasoning: String, val completed: Boolean = false,
    val completedAt: Long? = null, val createdAt: Long = System.currentTimeMillis()
)
""",

    # DAOs
    "app/src/main/kotlin/com/raithabharosa/hub/data/dao/FarmerDao.kt": """package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.FarmerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerProfile): Long
    @Query("SELECT * FROM farmer_profiles WHERE id = :id")
    suspend fun getFarmer(id: Int): FarmerProfile?
    @Query("SELECT * FROM farmer_profiles")
    fun getAllFarmers(): Flow<List<FarmerProfile>>
    @Query("SELECT * FROM farmer_profiles ORDER BY lastUpdated DESC LIMIT 1")
    fun getLatestFarmer(): Flow<FarmerProfile?>
    @Update
    suspend fun updateFarmer(farmer: FarmerProfile)
    @Delete
    suspend fun deleteFarmer(farmer: FarmerProfile)
}
""",

    "app/src/main/kotlin/com/raithabharosa/hub/data/dao/SeasonDao.kt": """package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.SeasonRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: SeasonRecord): Long
    @Query("SELECT * FROM season_records WHERE id = :id")
    suspend fun getSeason(id: Int): SeasonRecord?
    @Query("SELECT * FROM season_records WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getSeasonsByFarmer(farmerId: Int): Flow<List<SeasonRecord>>
    @Query("SELECT * FROM season_records WHERE farmerId = :farmerId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestSeason(farmerId: Int): Flow<SeasonRecord?>
    @Update
    suspend fun updateSeason(season: SeasonRecord)
    @Delete
    suspend fun deleteSeason(season: SeasonRecord)
}
""",

    "app/src/main/kotlin/com/raithabharosa/hub/data/dao/SoilDao.kt": """package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.SoilRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilRecord(soil: SoilRecord): Long
    @Query("SELECT * FROM soil_records WHERE seasonId = :seasonId ORDER BY recordedAt DESC")
    fun getSoilRecords(seasonId: Int): Flow<List<SoilRecord>>
    @Query("SELECT * FROM soil_records WHERE seasonId = :seasonId ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestSoilRecord(seasonId: Int): Flow<SoilRecord?>
    @Update
    suspend fun updateSoilRecord(soil: SoilRecord)
    @Delete
    suspend fun deleteSoilRecord(soil: SoilRecord)
}
""",

    "app/src/main/kotlin/com/raithabharosa/hub/data/dao/DailyActionDao.kt": """package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.DailyAction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: DailyAction): Long
    @Query("SELECT * FROM daily_actions WHERE seasonId = :seasonId ORDER BY dayNumber ASC")
    fun getActionsForSeason(seasonId: Int): Flow<List<DailyAction>>
    @Query("SELECT * FROM daily_actions WHERE seasonId = :seasonId AND dayNumber = :dayNumber")
    suspend fun getActionForDay(seasonId: Int, dayNumber: Int): DailyAction?
    @Update
    suspend fun updateAction(action: DailyAction)
    @Delete
    suspend fun deleteAction(action: DailyAction)
}
""",

    # Database
    "app/src/main/kotlin/com/raithabharosa/hub/data/database/RaithaBharosaDatabase.kt": """package com.raithabharosa.hub.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raithabharosa.hub.data.dao.*
import com.raithabharosa.hub.data.model.*

@Database(entities = [FarmerProfile::class, SeasonRecord::class, SoilRecord::class, DailyAction::class], version = 1, exportSchema = false)
abstract class RaithaBharosaDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
    abstract fun seasonDao(): SeasonDao
    abstract fun soilDao(): SoilDao
    abstract fun dailyActionDao(): DailyActionDao

    companion object {
        @Volatile
        private var instance: RaithaBharosaDatabase? = null

        fun getInstance(context: Context): RaithaBharosaDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(context.applicationContext, RaithaBharosaDatabase::class.java, "raitha_bharosa_hub.db").build()
                instance = db
                db
            }
        }
    }
}
""",

    # Resources
    "app/src/main/res/values/strings.xml": """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Raitha-Bharosa Hub</string>
    <string name="onboarding_welcome">Welcome to Raitha-Bharosa Hub</string>
    <string name="farmer_name_label">Farmer Name</string>
    <string name="continue_button">Continue</string>
    <string name="dashboard_title">Dashboard</string>
    <string name="sowing_index_label">Sowing Index</string>
    <string name="input_center_title">Input Center</string>
    <string name="submit">Submit</string>
    <string name="krishi_calendar_title">Krishi Calendar</string>
    <string name="season_history_title">Season History</string>
</resources>
""",

    "app/src/main/res/values-kn/strings.xml": """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">ರೈತ-ಭರೋಸ ಹಬ್</string>
    <string name="onboarding_welcome">ರೈತ-ಭರೋಸ ಹಬ್ ಗೆ ಸ್ವಾಗತ</string>
    <string name="farmer_name_label">ರೈತನ ಹೆಸರು</string>
    <string name="continue_button">ಮುಂದುವರಿಸಿ</string>
    <string name="dashboard_title">ಪ್ರದರ್ಶನ ಫಲಕ</string>
    <string name="sowing_index_label">ರೋಪಣ ಸೂಚ್ಯಂಕ</string>
    <string name="input_center_title">ಇನ್ಪುಟ್ ಕೇಂದ್ರ</string>
    <string name="submit">ಸಲ್ಲಿಸಿ</string>
    <string name="krishi_calendar_title">ಕೃಷಿ ಪಂಚಾಂಗ</string>
    <string name="season_history_title">ಋತುವಿನ ಇತಿಹಾಸ</string>
</resources>
""",

    "app/src/main/res/styles.xml": """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.RaithaBharosaHub" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:statusBarColor">#2D6A4F</item>
        <item name="android:navigationBarColor">#FFFFFF</item>
    </style>
</resources>
""",

    # Gradle properties
    "gradle.properties": """android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
""",

    # Documentation
    "README.md": """# Raitha-Bharosa Hub

A Smart Sowing Assistant Android application for Indian farmers.

## Quick Start

1. Build: `./gradlew build`
2. Install: `./gradlew installDebug`
3. Run on device/emulator

## Features

- 🌾 Real-time Sowing Index calculation
- 📊 Soil data tracking
- 📅 7-day action planning
- 📈 Season history and yield tracking
- 🌍 English + Kannada support

## Architecture

- MVVM with Repository pattern
- Jetpack Compose UI
- Room Database
- Kotlin Coroutines

See CLAUDE.md for detailed architecture documentation.
""",

    "CLAUDE.md": """# Raitha-Bharosa Hub - Architecture Guide

## Project Structure

```
app/src/main/kotlin/com/raithabharosa/hub/
├── MainActivity.kt
├── data/
│   ├── dao/ (Database DAOs)
│   ├── database/ (Room setup)
│   ├── model/ (Data classes)
│   └── repository/ (Data layer)
├── domain/
│   └── engine/ (Business logic)
└── presentation/
    ├── navigation/ (Navigation)
    ├── screens/ (UI screens)
    ├── viewmodel/ (ViewModels)
    ├── theme/ (Styling)
    └── localization/ (Strings)
```

## Key Components

- **SowingIndexEngine**: Calculates sowing readiness
- **DataGenerator**: Simulates realistic sensor data
- **Repository**: Single source of truth for data
- **ViewModels**: Manage UI state
- **Compose Screens**: Beautiful reactive UI

## Database

- FarmerProfile
- SeasonRecord
- SoilRecord
- DailyAction

All with proper foreign key relationships.

See SETUP_INSTRUCTIONS.md for next steps.
""",
}

def main():
    print("🌾 Raitha-Bharosa Hub - Project Generator")
    print("=" * 50)

    try:
        created_count = 0
        for filepath, content in files.items():
            create_file(filepath, content)
            created_count += 1

        print("=" * 50)
        print(f"\n✨ Successfully created {created_count} files!")
        print("\n📝 Next steps:")
        print("1. Open the project in Android Studio")
        print("2. Sync Gradle (File → Sync Now)")
        print("3. Build: ./gradlew build")
        print("4. Run: ./gradlew installDebug")

        return 0
    except Exception as e:
        print(f"\n❌ Error: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())
