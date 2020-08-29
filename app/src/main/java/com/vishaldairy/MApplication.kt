package com.vishaldairy

import android.app.Application
import android.content.Context

class MApplication : Application() {
    companion object {
        private var context: Context? = null

        fun getContext(): Context? {
            return context
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = base
    }
}