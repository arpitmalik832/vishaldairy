package com.vishaldairy.model

data class MCart (
    val currency:String?=null,
    val item_count:Int?=null,
    val items:ArrayList<MCartItem>?=null,
    val needs_shipping:Boolean?=null,
    val total_price:Double?=null,
    val total_weight:Int?=null
)