package com.unicomer.e_tracker_test.models

data class TotalRecord(val totalFood: String,
                  val totalCar: String,
                  val totalHotel: String,
                  val totalOther:String){
    constructor():this("","","","")
}