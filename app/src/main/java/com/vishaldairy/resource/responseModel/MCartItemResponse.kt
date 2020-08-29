package com.vishaldairy.resource.responseModel

data class MCartItemResponse(
    val id:Int?=null,
    val name:String?=null,
    val key:String?=null,
    val quantity:Int?=null,
    val permalink:String?=null,
    val images:ArrayList<MImageResponse>?=null,
    val price:String?=null,
    val line_price:String?=null
)