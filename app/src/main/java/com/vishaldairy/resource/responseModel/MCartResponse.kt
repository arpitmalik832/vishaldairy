package com.vishaldairy.resource.responseModel

data class MCartResponse (
    val currency:String?=null,
    val item_count:Int?=null,
    val items:ArrayList<MCartItemResponse>?=null,
    val needs_shipping:Boolean?=null,
    val total_price:Double?=null,
    val total_weight:Int?=null
)