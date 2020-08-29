package com.vishaldairy.utils

import com.google.gson.Gson
import com.vishaldairy.model.MAddress
import com.vishaldairy.model.MUser
import java.util.*

object Action {
    fun getUserId(): Int {
        return 27
    }

    fun getPhoneNumber(): String {
        return "7042040468"
    }

    fun getUserName(): String {
        return "Chandan"
    }

    fun getEmailAddress(): String {
        return "Chandan.sharma7539@gmail.com"
    }

    fun generateTxn(): String? {
        return "TXN_ID_${Calendar.getInstance().timeInMillis}"
    }

    fun saveUser(user: MUser) {
        PreferencesUtils.putString(AppConstants.USER_DATA,Gson().toJson(user))
    }

    fun saveUserAddress(userAddress: MAddress) {
        PreferencesUtils.putString(AppConstants.USER_ADDRESS_DATA,Gson().toJson(userAddress))
    }

    fun getUser():MUser{
        return Gson().fromJson<MUser>(PreferencesUtils.getString(AppConstants.USER_DATA),MUser::class.java)?: MUser()
    }

    fun getUserAddress():MAddress{
        return Gson().fromJson<MAddress>(PreferencesUtils.getString(AppConstants.USER_ADDRESS_DATA),MAddress::class.java)?:MAddress()
    }
}