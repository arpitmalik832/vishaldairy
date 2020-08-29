package com.vishaldairy.model

data class MSubCategory(
    val id:Int?=null,
    val name:String?=null,
    val parentId:Int?=null,
    val count:Int?=null,
    val description:String?=null,
    val imageId:Int?=null,
    val imageSrc:String?=null,
    var productList:ArrayList<MProduct>?=null
)