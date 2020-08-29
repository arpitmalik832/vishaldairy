package com.vishaldairy.model

data class MCartItem (
    val id:Int?=null,
    var name:String?=null,
    val key:String?=null,
    val quantity:Int?=null,
    val imageId:Int?=null,
    val imageSrc:String?=null,
    val regular_price:String?=null,
    val sale_price:String?=null,
    val line_price:String?=null,
    val on_sale:Boolean?=null,
    val purchasable:Boolean?=null,
    val manage_stock:Boolean?=null,
    val stock_quantity:Int?=null,
    val stock_status:String?=null
)
