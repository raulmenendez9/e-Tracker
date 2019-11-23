package com.unicomer.e_tracker_test.models

data class Travel(val originCountry: String?,
                  val destinyCountry: String?,
                  val centerCost: String?,
                  val cash: String?,
                  val emailUser: String?,
                  val refund: String?,
                  val initialDate: String?,
                  val finishDate: String?,
                  val DateRegister: String?,
                  val UpdateRegister: String?,
                  val aproved: String?,
                  val description: String?,
                  val balance: String?,
                  val active: Boolean,
                  val settled: Boolean){
    constructor():this("","", "",
        "","","","","","","","",
        "","", false, false)
}