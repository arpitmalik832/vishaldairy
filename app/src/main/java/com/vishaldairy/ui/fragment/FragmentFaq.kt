package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.adapter.FaqAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentFaqBinding
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentFaq: BaseFragment<FragmentFaqBinding>() {

    private var binding: FragmentFaqBinding? = null
    private var viewModel: VMHome? = null
    private var adapterFaq: FaqAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_faq

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterFaq = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    FaqAdapter(
                        context,
                        viewModel,
                        binding,
                        ArrayList()
                    )
                }
            }
        }
        binding?.expandableListView?.setAdapter(adapterFaq)
    }

    override fun initListeners() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            viewModel?.faqList?.let {
                adapterFaq?.setDataList(it)
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

        viewModel?.getFaqs()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "Frequently Asked Questions")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}

