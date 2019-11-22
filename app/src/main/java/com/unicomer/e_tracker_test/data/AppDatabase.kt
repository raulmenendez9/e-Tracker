package com.unicomer.e_tracker_test.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unicomer.e_tracker_test.models.Record


@Database(
    entities = [Record::class],
    version = 1,
    exportSchema = false)

abstract class AppDatabase: RoomDatabase() {

    abstract fun travelDataDao(): TravelDao

    companion object {

        @Volatile private var instance : AppDatabase?= null

        fun getInstance(mContext: Context) : AppDatabase? {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(mContext).also { instance = it}
            }
        }

        private fun buildDatabase(context : Context) : AppDatabase? {
            return Room.databaseBuilder(context,
                AppDatabase::class.java,
                "travels").build()
        }

    }


}