package com.vishaldairy.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.vishaldairy.dotprogressbar.DotProgressBar
import com.vishaldairy.dotprogressbar.R
import com.vishaldairy.navigator.NavigatorBase

abstract class BaseFragment<T: ViewDataBinding>: Fragment(), NavigatorBase {

    private var mViewDataBinding: T? = null
    private var mRootView: View? = null
    private var mToolbar: Toolbar? = null
    var dotProgressBar: DotProgressBar? = null

    @get:LayoutRes
    protected abstract val layoutId: Int

    open fun getViewDataBinding(): T? {
        return mViewDataBinding
    }

    override fun showLoading() {
        context?.let {
            dotProgressBar = DotProgressBar.Builder()
                .setMargin(1)
                .setAnimationDuration(1000)
                .setDotBackground(R.drawable.icon_dot)
                .setMaxScale(1f)
                .setMaxScale(0.3f)
                .setNumberOfDots(3)
                .build(it)
        }
        dotProgressBar?.startAnimation()
    }

    override fun hideLoading() {
        dotProgressBar?.visibility = View.GONE
    }

    override fun showMessage(msg: String?, action: String?, parentLayout: View) {
        msg?.let {
            Snackbar.make(
                parentLayout,
                it,
                Snackbar.LENGTH_SHORT
            )
        }?.setAction(action) {

        }?.show()
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(
            inflater,
            layoutId,
            container,
            false
        )
        mRootView = mViewDataBinding?.root
        init()
        return mRootView
    }

    private fun init() {
        initViews()
        bindDataWithViews()
        initListeners()
    }

    abstract fun initViews()
    abstract fun bindDataWithViews()
    abstract fun initListeners()

    fun setToolbar(toolbar: Toolbar) {
        mToolbar = toolbar
        (activity as BaseActivity<*>?)?.setSupportActionBar(toolbar)
    }

    fun setToolbarBackgroundColor(color: Int) {
        mToolbar?.setBackgroundColor(color)
    }

    fun alignTitleCenter(titleView:AppCompatTextView) {
        titleView.gravity = Gravity.CENTER
    }

    fun setToolbarTitle(titleView: AppCompatTextView, title: String) {
        titleView.text = title
    }

    fun setToolbarTitleTextColor(titleView: AppCompatTextView, color: Int) {
        titleView.setTextColor(color)
    }

    fun enableToolbarHomeButton() {
        if((activity as BaseActivity<*>?)?.supportActionBar != null) {
            (activity as BaseActivity<*>?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as BaseActivity<*>?)?.supportActionBar?.title = "Dashboard Fragment"
        }
    }

    fun setToolbarHomeButtonIcon(drawable: Int) {
        (activity as BaseActivity<*>?)?.supportActionBar?.setHomeAsUpIndicator(drawable)
    }

    fun hideSoftInputKeyboard() {
        if(activity == null)
            return
        val view = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null) {
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }

    fun addFragment(
        fragment: Fragment,
        containerId: Int,
        fragmentTag: String
    ) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(
                containerId,
                fragment,
                fragmentTag
            )
            ?.commitAllowingStateLoss()
    }

    fun replaceFragment(
        fragment: Fragment,
        containerId: Int,
        fragmentTag: String,
        addToBackStack: Boolean
    ) {
        if(activity != null) {
            val replace = activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(
                    containerId,
                    fragment,
                    fragmentTag
                )
            if(addToBackStack) {
                replace?.addToBackStack(fragment.javaClass.name)
            }
            replace?.commitAllowingStateLoss()
        }
    }

    private fun hasPermissions(
        permission: String
    ): Boolean? {
        return context?.let {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                it,
                permission
            )
        }
    }

    private fun hasPermissions(
        permissions: Array<String>
    ): Boolean {
        for(s in permissions) {
            if(context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        s
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun askForPermissions(
        permission: String,
        permissionCode: Int
    ) {
        hasPermissions(permission)?.let { bool ->
            if (!bool) {
                activity?.let {activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        permissionCode
                    )
                }
            }
        }
    }

    fun askForPermissions(
        permissions: Array<String>,
        permissionCode: Int
    ) {
        if(!hasPermissions(permissions)) {
            activity?.let {activity ->
                ActivityCompat.requestPermissions(
                    activity,
                    permissions,
                    permissionCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

}