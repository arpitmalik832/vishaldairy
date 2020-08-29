package com.vishaldairy.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.vishaldairy.dotprogressbar.R
import com.vishaldairy.dotprogressbar.DotProgressBar
import com.vishaldairy.navigator.NavigatorBase

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity(), NavigatorBase {

    private var mViewDataBinding: T? = null
    private var dotProgressBar: DotProgressBar? = null
    private var mToolbar: Toolbar? = null
    @get:LayoutRes
    protected abstract val layoutId: Int

    open fun getViewDataBinding(): T? {
        return mViewDataBinding
    }

    override fun showLoading() {
        dotProgressBar = DotProgressBar.Builder()
            .setMargin(1)
            .setAnimationDuration(1000)
            .setDotBackground(R.drawable.icon_dot)
            .setMaxScale(1f)
            .setMaxScale(0.3f)
            .setNumberOfDots(3)
            .build(this)
        dotProgressBar?.startAnimation()
    }

    override fun hideLoading() {
        dotProgressBar?.visibility = View.GONE
    }

    override fun showMessage(msg: String?,action:String?, parentLayout:View) {
        msg?.let {
            Snackbar.make(
                parentLayout,
                it,
                Snackbar.LENGTH_SHORT
            )
        }?.setAction(action) {

        }?.show()
    }

    override fun onCreate(
        mSavedInstanceState: Bundle?
    ) {
        super.onCreate(mSavedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this,layoutId)
        init()
    }

    private fun init() {
        initViews()
        initListeners()
        bindDataWithViews()
    }

    abstract fun initViews()
    abstract fun initListeners()
    abstract fun bindDataWithViews()

    fun addFragment(
        fragment: Fragment,
        containerId: Int,
        fragmentTag: String
    ) {
        supportFragmentManager.beginTransaction()
            .replace(
                containerId,
                fragment,
                fragmentTag
            ).commitAllowingStateLoss()
    }

    fun replaceFragment(
        fragment: Fragment,
        containerId: Int,
        fragmentTag: String,
        addToBackTrace: Boolean
    ) {
        val replace = supportFragmentManager.beginTransaction()
            .replace(
                containerId,
                fragment,
                fragmentTag
            )
        if(addToBackTrace) {
            replace.addToBackStack(fragment.javaClass.name)
        }
        replace.commitAllowingStateLoss()
    }

    fun hideSoftInputKeyboard() {
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null) {
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }

    //val isNetworkAvailable: Boolean
      //  get() {
        //    val connectivityManager = this@BaseActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
          //  val activeNetworkInfo = connectivityManager.activeNetworkInfo
            //return activeNetworkInfo !== null && activeNetworkInfo.isConnected
       // }

    private fun hasPermissions(
        permission: String
    ): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            applicationContext,
            permission
        )
    }

    private fun hasPermissions(
        permissions: Array<String>
    ): Boolean {
        for(s in permissions) {
            if(ContextCompat.checkSelfPermission(
                    applicationContext,
                    s
                ) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun askForPermissions(
        permission: String,
        permissionCode: Int
    ) {
        if(!hasPermissions(permission)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                permissionCode
            )
        }
    }

    fun askForPermissions(
        permissions: Array<String>,
        permissionCode: Int
    ) {
        if(!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                permissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

}