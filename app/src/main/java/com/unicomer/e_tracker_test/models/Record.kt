package com.unicomer.e_tracker_test.models

data class Record(val recordName: String,
                  val recordDate: String,
                  val recordMount: Double,
                  val recordCategory:Int,
                  val recordPhoto:String,
                  val recordDescription: String,
                  val recordDateRegister: String,
                  val recordUpdateRegister: String){
    constructor():this("","",0.0,0, "","",
        "","")
}
/*
orden para las categorias:
food = 0
car = 1
hotel = 2
other = 3
*/