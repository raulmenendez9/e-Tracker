package com.unicomer.e_tracker_test.models

data class Travel(val originCountry: String?,
                  val destinyCountry: String?,
                  val centerCost: String?,
                  val cash: Double?,
                  val refund: String?,
                  val initialDate: String,
                  val finishDate: String?,
                  val aproved: String?,
                  val description: String?,
                  val balance: Double?){
    constructor():this("","", "",0.00,"","",
        "", "","",0.00)
}