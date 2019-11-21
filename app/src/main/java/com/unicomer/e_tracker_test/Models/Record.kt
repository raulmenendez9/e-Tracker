package com.unicomer.e_tracker_test.models

data class Record(val recordName: String,
                  val recordDate: String,
                  val recordMount: String,
                  val recordCategory:String,
                  val recordPhoto:String,
                  val recordDescription: String,
                  val recordDateRegister: String,
                  val recordUpdateRegister: String){
    constructor():this("","","","", "","",
        "","")
}
/*
orden para las categorias:
food = 0
car = 1
hotel = 2
other = 3
*/