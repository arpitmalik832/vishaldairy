package com.vishaldairy.resource.responseModel

data class MProductResponse(
    val id:Int?=null,
    val name:String?=null,
    val slug:String?=null,
    val permalink:String?=null,
    val date_created:String?=null,
    val date_created_gmt:String?=null,
    val date_modified:String?=null,
    val date_modified_gmt:String?=null,
    val type:String?=null,
    val status:String?=null,
    val featured:Boolean?=null,
    val catalog_visibility:String?=null,
    val price: String?=null,
    val regular_price:String?=null,
    val sale_price:String?=null,
    val date_on_sale_from:String?=null,
    val date_on_sale_from_gmt:String?=null,
    val date_on_sale_to:String?=null,
    val date_on_sale_to_gmt:String?=null,
    val description:String?=null,
    val on_sale:Boolean?=null,
    val purchasable:Boolean?=null,
    val total_sales:String?=null,
    val tax_status:String?=null,
    val manage_stock:Boolean?=null,
    val stock_quantity:Int?=null,
    val stock_status:String?=null,
    val images:ArrayList<MImageResponse>?=null
)