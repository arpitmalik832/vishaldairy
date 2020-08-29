package com.vishaldairy.resource.responseModel

import com.vishaldairy.model.MAddress
import com.vishaldairy.model.MUser

class MUserResponse(
    val errorCode: Int? = null,
    val errorMessage: String? = null,
    val userData: MUser? = null,
    val addressData: MAddress? = null
)