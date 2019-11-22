package com.unicomer.e_tracker_test.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "record_name") val recordName: String = "",
    @ColumnInfo(name = "record_date") val recordDate: String = "",
    @ColumnInfo(name = "record_amount") val recordMount: String = "",
    @ColumnInfo(name = "record_category") val recordCategory:String = "",
    @ColumnInfo(name = "record_photo") val recordPhoto:String = "",
    @ColumnInfo(name = "record_desc") val recordDescription: String = "",
    @ColumnInfo(name = "record_create_register") val recordDateRegister: String = "",
    @ColumnInfo(name = "record_update_register") val recordUpdateRegister: String = ""): Parcelable