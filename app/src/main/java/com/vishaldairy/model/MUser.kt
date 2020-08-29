package com.vishaldairy.model

data class MUser(
    var name:String?=null,
    var image:Int?=null,
    var mobile:String?=null,
    var email:String?=null,
    var address:MAddress?=null,
    var detail:String?=null,
    var noOfSubscriptions:Int?=null,
    var walletBalance:Int?=null
)