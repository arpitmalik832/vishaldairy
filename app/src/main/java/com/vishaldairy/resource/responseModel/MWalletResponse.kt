package com.vishaldairy.resource.responseModel

data class MWalletResponse (
    val errorCode: Int?=null,
    val errorMessage: String?=null,
    val amount:String?=null
)