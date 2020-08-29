package com.vishaldairy.resource.responseModel

data class MCategoryResponse (
    val id:Int?=null,
    val name:String?=null,
    val slug:String?=null,
    val parent:Int?=null,
    val count:Int?=null,
    val description:String?=null,
    val image:MImageResponse?=null
)