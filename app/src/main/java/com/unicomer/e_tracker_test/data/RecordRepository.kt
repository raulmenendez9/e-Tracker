package com.unicomer.e_tracker_test.data

import com.unicomer.e_tracker_test.models.Record
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class RecordRepository(private val dataDao: TravelDao) {

    suspend fun createRecord(records: MutableList<Record>){
        withContext(IO) {
            dataDao.setData(records)
        }
    }

    suspend fun records() = dataDao.allRecords()

    companion object {

        // Singleton initialization
        @Volatile private var instance: RecordRepository? = null

        fun recordInstance(dataDao: TravelDao) =
            instance ?: synchronized(this){
                instance?: RecordRepository(dataDao).also { instance = it }
            }
    }


}