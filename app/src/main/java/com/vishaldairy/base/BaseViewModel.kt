package com.vishaldairy.base

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

open class BaseViewModel<T> : ViewModel() {
    private var mNavigator: WeakReference<T>? = null

    val navigator: T?
        get() = mNavigator!!.get()

    fun setNavigator(navigator: T) {
        mNavigator = WeakReference(navigator)
    }
}
