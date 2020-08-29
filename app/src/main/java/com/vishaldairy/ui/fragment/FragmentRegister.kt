package com.vishaldairy.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentRegisterBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMAuth

class FragmentRegister: BaseFragment<FragmentRegisterBinding>() {

    private var binding: FragmentRegisterBinding? = null
    private var viewModel: VMAuth? = null

    override val layoutId: Int
        get() = R.layout.fragment_register

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMAuth::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {

    }

    override fun showMessage(msg: String?, action: Int?) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }

    private fun checkEditTextIsNotEmpty(editText: AppCompatEditText, errorString: String): Boolean {
        editText.text?.isEmpty()?.let {isEmpty ->
            editText.text?.equals("")?.let {equals ->
                if (isEmpty || editText.text?.length == 0 || equals || editText.text == null) {
                    editText.error = errorString
                    return false
                }
            }
        }
        return true
    }

    override fun onAction(data: Any?, action: Int?) {
        startActivity(Intent(context,ActivityMain::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun bindDataWithViews() {
    }


}