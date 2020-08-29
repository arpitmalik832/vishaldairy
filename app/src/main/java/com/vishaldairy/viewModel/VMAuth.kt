package com.vishaldairy.viewModel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.vishaldairy.base.BaseViewModel
import com.vishaldairy.model.MAddress
import com.vishaldairy.model.MUser
import com.vishaldairy.navigator.NavigatorBase
import com.vishaldairy.resource.responseModel.MLoginResponse
import com.vishaldairy.resource.responseModel.MUserResponse
import com.vishaldairy.resource.service.ServiceLogin
import com.vishaldairy.resource.service.ServiceOTP
import com.vishaldairy.resource.service.ServiceRegister
import com.vishaldairy.utils.Action
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VMAuth : BaseViewModel<NavigatorBase>() {
    var loginResponse = MutableLiveData<Boolean>()
    var regResponse = MutableLiveData<Boolean>()
    var otpResponse = MutableLiveData<Boolean>()
    var otp = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    var buttonText = MutableLiveData<String>()


    fun hitGenerateOtp(phone: String) {
        phoneNumber.value = phone
        val request = HashMap<String, Any>()
        request["mobile"] = phone
        ServiceLogin(request).call.enqueue(object : Callback<MLoginResponse> {
            override fun onFailure(call: Call<MLoginResponse>, t: Throwable) {
                loginResponse.value = false
            }

            override fun onResponse(
                call: Call<MLoginResponse>,
                response: Response<MLoginResponse>
            ) {
                if (response.isSuccessful && response.body()?.errorCode == 200) {
                    loginResponse.value = true
                    otp.value = response.body()?.otp?:"0"
                } else {
                    loginResponse.value = false
                }
                navigator?.showMessage(response.body()?.errorMessage,0)
            }
        })
    }

    fun hitVerifyOtp(otp:String) {
        val request = HashMap<String, Any>()
        request["mobile"] = phoneNumber.value?:""
        request["otp"] = otp
        ServiceOTP(request).call.enqueue(object : Callback<MUserResponse> {
            override fun onFailure(call: Call<MUserResponse>, t: Throwable) {
                otpResponse.value = false
            }

            override fun onResponse(
                call: Call<MUserResponse>,
                response: Response<MUserResponse>
            ) {
                if (response.isSuccessful) {
                    if(response.body()?.errorCode == 200){
                        val mData = response.body()?: MUserResponse()
                        Action.saveUser(mData.userData?:MUser())
                        Action.saveUserAddress(mData.addressData?: MAddress())
                        navigator?.onAction(mData,response.body()?.errorCode)
                    }else if(response.body()?.errorCode == 201){
                        navigator?.onAction(null,response.body()?.errorCode)
                    }
                    otpResponse.value = true
                } else {
                    otpResponse.value = false
                }
            }
        })
    }

    var rName = MutableLiveData<String>()
    var rMobileNumber = MutableLiveData<String>()
    var rDetails = MutableLiveData<String>()
    var rEmail = MutableLiveData<String>()
    var rFlatNumber = MutableLiveData<String>()
    var rLandmark = MutableLiveData<String>()
    var rSociety = MutableLiveData<String>()
    var rLocality = MutableLiveData<String>()
    var rCity = MutableLiveData<String>()
    var rState = MutableLiveData<String>()
    var rPinCode = MutableLiveData<String>()

    fun hitRegisterUser(){
        if(!validateUser()) return
        val request = HashMap<String, Any>()
        request["name"] = rName.value?:""
        request["mobile"] = rMobileNumber.value?:""
        request["details"] = rDetails.value?:""
        request["email"] = rEmail.value?:""
        request["flatNo"] = rFlatNumber.value?:""
        request["landmark"] = rLandmark.value?:""
        request["locality"] = rLocality.value?:""
        request["society"] = rSociety.value?:""
        request["city"] = rCity.value?:""
        request["state"] = rState.value?:""
        request["pincode"] = rPinCode.value?:""

        ServiceRegister(request).call.enqueue(object : Callback<MUserResponse> {
            override fun onFailure(call: Call<MUserResponse>, t: Throwable) {
                regResponse.value = false
            }
            override fun onResponse(
                call: Call<MUserResponse>,
                response: Response<MUserResponse>
            ) {
                if (response.isSuccessful && response.body()?.errorCode == 200) {
                    regResponse.value = true
                    val mData = response.body()?: MUserResponse()
                    Action.saveUser(mData.userData?:MUser())
                    Action.saveUserAddress(mData.addressData?: MAddress())
                    navigator?.onAction(mData,200)
                } else {
                    regResponse.value = false
                }
                navigator?.showMessage(response.body()?.errorMessage,response.body()?.errorCode)
            }
        })
    }


    private fun validateUser(): Boolean {

        if (TextUtils.isEmpty(rName.value)) {
            navigator?.showMessage("Enter an name",0)
            return false
        }
        if (TextUtils.isEmpty(rDetails.value)) {
            navigator?.showMessage("Enter Details", 0)
            return false
        }
        if (TextUtils.isEmpty(rEmail.value)) {
            navigator?.showMessage("Enter an Email", 0)
            return false
        }
        if (TextUtils.isEmpty(rFlatNumber.value)) {
            navigator?.showMessage("Enter Flat/House No", 0)
            return false
        }
        if (TextUtils.isEmpty(rSociety.value)) {
            navigator?.showMessage("Enter Society", 0)
            return false
        }
        if (TextUtils.isEmpty(rLocality.value)) {
            navigator?.showMessage("Enter Locality", 0)
            return false
        }
        if (TextUtils.isEmpty(rCity.value)) {
            navigator?.showMessage("Enter an city", 0)
            return false
        }
        if (TextUtils.isEmpty(rState.value)) {
            navigator?.showMessage("Enter an State", 0)
            return false
        }
        if (TextUtils.isEmpty(rPinCode.value)) {
            navigator?.showMessage("Enter an pincode", 0)
            return false
        }
        return true
    }

}