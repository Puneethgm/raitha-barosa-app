package com.raithabharosa.hub.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raithabharosa.hub.data.dao.*
import com.raithabharosa.hub.data.model.*

@Database(entities = [FarmerProfile::class, SeasonRecord::class, SoilRecord::class, DailyAction::class, com.raithabharosa.hub.data.model.User::class, com.raithabharosa.hub.data.model.ScheduledAction::class], version = 3, exportSchema = false)
abstract class RaithaBharosaDatabase : RoomDatabase() {
    abstract fun farmerDao(): FarmerDao
    abstract fun seasonDao(): SeasonDao
    abstract fun soilDao(): SoilDao
    abstract fun dailyActionDao(): DailyActionDao
    abstract fun userDao(): com.raithabharosa.hub.data.dao.UserDao
    abstract fun scheduledActionDao(): com.raithabharosa.hub.data.dao.ScheduledActionDao

    companion object {
        @Volatile
        private var instance: RaithaBharosaDatabase? = null

        fun getInstance(context: Context): RaithaBharosaDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(context.applicationContext, RaithaBharosaDatabase::class.java, "raitha_bharosa_hub.db").fallbackToDestructiveMigration().build()
                instance = db
                db
            }
        }
    }
}
