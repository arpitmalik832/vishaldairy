package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.adapter.OrdersAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentMyOrdersBinding
import com.vishaldairy.model.MOrder
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FragmentMyOrders: BaseFragment<FragmentMyOrdersBinding>(),OnClick<MOrder> {

    private var binding: FragmentMyOrdersBinding? = null
    private var viewModel: VMHome? = null
    private var adapterOrder: OrdersAdapter? = null

    private var ordersList = ArrayList<MOrder>()

    override val layoutId: Int
        get() = R.layout.fragment_my_orders

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterOrder = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    OrdersAdapter(
                        context,
                        viewModel,
                        binding,
                        this,
                        ArrayList()
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.ordersRecyclerView?.layoutManager = layoutManager
            binding?.ordersRecyclerView?.adapter = adapterOrder
        binding?.ordersRecyclerView?.setHasFixedSize(true)
    }

    override fun initListeners() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            viewModel?.ordersList?.let {
                adapterOrder?.setDataList(it)
            }

            hideLoading()
            binding?.parentLayout?.visibility = View.VISIBLE
        }

        binding?.allBtn?.setOnClickListener {
            context?.let {context ->
                binding?.allBtn?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_30dp
                )
                binding?.processingBtn?.background = null
                binding?.completedBtn?.background = null
                binding?.cancelledBtn?.background = null

                ordersList.clear()

                viewModel?.ordersList?.let {
                    for(i in it.indices) {
                        ordersList.add(it[i])
                    }
                    adapterOrder?.setDataList(ordersList)
                }
            }
        }

        binding?.processingBtn?.setOnClickListener {
            context?.let {context ->
                binding?.processingBtn?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_30dp
                )
                binding?.allBtn?.background = null
                binding?.completedBtn?.background = null
                binding?.cancelledBtn?.background = null

                ordersList.clear()

                viewModel?.ordersList?.let {
                    for(i in it.indices) {
                        if (it[i].status?.toLowerCase(Locale.ROOT) == "processing") {
                            ordersList.add(it[i])
                        }
                    }
                    adapterOrder?.setDataList(ordersList)
                }
            }
        }

        binding?.completedBtn?.setOnClickListener {
            context?.let {context ->
                binding?.completedBtn?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_30dp
                )
                binding?.processingBtn?.background = null
                binding?.allBtn?.background = null
                binding?.cancelledBtn?.background = null

                ordersList.clear()

                viewModel?.ordersList?.let {
                    for(i in it.indices) {
                        if(it[i].status?.toLowerCase(Locale.ROOT) == "delivered")
                            ordersList.add(it[i])
                    }
                    adapterOrder?.setDataList(ordersList)
                }
            }
        }

        binding?.cancelledBtn?.setOnClickListener {
            context?.let {context ->
                binding?.cancelledBtn?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_30dp
                )
                binding?.processingBtn?.background = null
                binding?.completedBtn?.background = null
                binding?.allBtn?.background = null

                ordersList.clear()

                viewModel?.ordersList?.let {
                    for(i in it.indices) {
                        if(it[i].status?.toLowerCase(Locale.ROOT) == "cancelled")
                            ordersList.add(it[i])
                    }
                    adapterOrder?.setDataList(ordersList)
                }
            }
        }

    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        viewModel?.getOrders()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it,"My Orders")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MOrder, position: Int, view: View) {
        if(view.id == R.id.details_btn) {
            val fragmentOrderDetail = FragmentOrderDetail()

            val args = Bundle()
            val beanJson = Gson().toJson(bean)
            args.putString("order",beanJson)

            fragmentOrderDetail.arguments = args
            replaceFragment(
                fragmentOrderDetail,
                R.id.frame_layout,
                "5",
                true
            )
        }
    }

}