package com.unicomer.e_tracker_test.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.unicomer.e_tracker_test.models.Record

@Dao
interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setData(records: MutableList<Record>)

    @Query("SELECT * FROM records")
    fun allRecords(): List<Record>



}