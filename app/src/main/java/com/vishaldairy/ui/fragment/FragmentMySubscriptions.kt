package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.adapter.SubscriptionsAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentMySubscriptionsBinding
import com.vishaldairy.model.MSubscription
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentMySubscriptions: BaseFragment<FragmentMySubscriptionsBinding>(), OnClick<MSubscription> {

    private var binding: FragmentMySubscriptionsBinding? = null
    private var viewModel: VMHome? = null
    private var adapterSubscriptions: SubscriptionsAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_my_subscriptions

    override fun initViews() {

        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterSubscriptions = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    SubscriptionsAdapter(
                        context,
                        viewModel,
                        binding,
                        this,
                        ArrayList()
                    )
                }
            }
        }
        binding?.expandableListView?.setAdapter(adapterSubscriptions)

    }

    override fun initListeners() {

        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            viewModel?.subscriptionsList?.let {
                adapterSubscriptions?.setDataList(it)
            }

            hideLoading()
            binding?.parentLayout?.visibility = View.VISIBLE

        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        viewModel?.getSubscriptions()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        context?.let {
            setToolbarBackgroundColor(Color.WHITE)
        }
        binding?.title?.let {
            setToolbarTitle(it,"My Subscriptions")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MSubscription, position: Int, view: View) {
        if(view.id == R.id.edit_btn) {
            (activity as ActivityMain?)?.binding?.snackBarLayout?.let {snackBarLayout ->
                showMessage(
                    "Under Construction",
                    "",
                    snackBarLayout
                )
            }
        } else if(view.id == R.id.vacation_btn) {
            replaceFragment(
                FragmentSetVacation(),
                R.id.frame_layout,
                "14",
                true
            )
        }
    }

}