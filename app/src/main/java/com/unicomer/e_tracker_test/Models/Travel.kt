package com.unicomer.e_tracker_test.Models

data class Travel(val originCountry: String?,
                  val destinyCountry: String?,
                  val centerCost: String?,
                  val cash: String?,
                  val refund: String?,
                  val initialDate: String,
                 // val finishDate: String?,
                  val aproved: String?,
                  val description: String?,
                  val balance: String?){
    constructor():this("","", "","","","","",
        "","")
}