package com.cniekirk.traintimes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cniekirk.traintimes.data.local.model.CRS

@Database(entities = [CRS::class],
    version = 1,
    exportSchema = false)
abstract class AppDb: RoomDatabase() {

    abstract fun crsDao(): CRSDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, "crs-codes")
                .build()
        }
    }

}