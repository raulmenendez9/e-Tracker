package com.unicomer.e_tracker_test.models

data class Record(
    var recordName: String,
    var recordDate: String,
    var recordMount: String,
    var recordCategory:String,
    var recordPhoto:String,
    var recordDescription: String,
    var recordDateRegister: String,
    var recordUpdateRegister: String){
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