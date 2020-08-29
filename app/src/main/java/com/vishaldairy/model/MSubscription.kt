package com.vishaldairy.model

data class MSubscription (
    var product:MOrderedProduct?=null,
    var startDate:String?=null,
    var endDate:String?=null,
    var deliveryTime:String?=null,
    var status:String?=null,
    var optionsList: ArrayList<MSubscriptionOptions>?=null
)

