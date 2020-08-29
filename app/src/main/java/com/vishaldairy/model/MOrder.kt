package com.vishaldairy.model

data class MOrder (
    var no:Int?=null,
    var date:String?=null,
    var noOfProducts:Int?=null,
    var totalAmount:Int?=null,
    var status:String?=null,
    var productList:ArrayList<MOrderedProduct>?=null,
    var shippingAddress:MAddress?=null,
    var paymentMethod:String?=null,
    var discount:String?=null
)
