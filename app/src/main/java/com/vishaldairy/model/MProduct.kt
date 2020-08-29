package com.vishaldairy.model

data class MProduct (
    val id:Int?=null,
    val name:String?=null,
    val imageId:Int?=null,
    val imageSrc:String?=null,
    val price:String?=null,
    val regular_price:String?=null,
    val sale_price:String?=null,
    val description:String?=null,
    val type:String?=null,
    val status:String?=null,
    val featured:Boolean?=null,
    val catalog_visibility:String?=null,
    val on_sale:Boolean?=null,
    val purchasable:Boolean?=null,
    val total_sales:String?=null,
    val tax_status:String?=null,
    val manage_stock:Boolean?=null,
    val stock_quantity:Int?=null,
    val stock_status:String?=null
)