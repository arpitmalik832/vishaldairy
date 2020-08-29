package com.vishaldairy.navigator

import android.view.View

interface OnClick<T> {
    fun onClick(bean: T, position: Int, view: View)
}