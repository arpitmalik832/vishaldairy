package com.vishaldairy.resource.responseModel

data class MImageResponse(
    val id:Int?=null,
    val date_created:String?=null,
    val date_created_gmt:String?=null,
    val date_modified:String?=null,
    val date_modified_gmt:String?=null,
    val src:String?=null,
    val thumbnail:String?=null,
    val name:String?=null,
    val alt:String?=null
)