package com.vishaldairy.navigator

import android.view.View

interface NavigatorBase {
    fun showLoading()
    fun hideLoading()
    fun showMessage(msg: String?,action:String?,parentLayout:View)
    fun showMessage(msg: String?,action:Int?){}
    fun onAction(data: Any?,action:Int?){}
}