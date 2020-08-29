package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.adapter.OrderedProductsAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentOrderDetailBinding
import com.vishaldairy.model.MOrder
import com.vishaldairy.model.MOrderedProduct
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentOrderDetail: BaseFragment<FragmentOrderDetailBinding>(),OnClick<MOrderedProduct> {

    private var binding: FragmentOrderDetailBinding? = null
    private var viewModel: VMHome? = null
    private var adapterOrderedProduct: OrderedProductsAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_order_detail

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterOrderedProduct = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    OrderedProductsAdapter(
                        context,
                        viewModel,
                        binding,
                        ArrayList()
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.orderProductsRecyclerView?.layoutManager = layoutManager
        binding?.orderProductsRecyclerView?.adapter = adapterOrderedProduct
        binding?.orderProductsRecyclerView?.setHasFixedSize(true)
    }

    override fun initListeners() {
        val orderJson = arguments?.getString("order")
        val order: MOrder? = Gson().fromJson(
            orderJson,
            MOrder::class.java
        )

        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            viewModel?.orderProductListMap?.get(order?.no)?.let {
                adapterOrderedProduct?.setDataList(it)
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

        val orderJson = arguments?.getString("order")
        val order: MOrder? = Gson().fromJson(
            orderJson,
            MOrder::class.java
        )

        binding?.orderNo?.text = order?.no.toString()
        binding?.orderDate?.text = order?.date
        binding?.products?.text = order?.noOfProducts.toString()

        binding?.status?.text = order?.status
        context?.let {context ->
            when(order?.status) {
                "PROCESSING" -> {
                    binding?.status?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                }
                "DELIVERED" -> {
                    binding?.status?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green
                        )
                    )
                }
                "CANCELLED" -> {
                    binding?.status?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red3
                        )
                    )
                }
                else -> {

                }
            }
        }

        var address = "${order?.shippingAddress?.residencyType}\n" +
                "${order?.shippingAddress?.flatOrHouseNo}, " +
                "${order?.shippingAddress?.apartmentOrSociety}, " +
                if(order?.shippingAddress?.landmark != "" && order?.shippingAddress?.landmark != null) {
                    "${order.shippingAddress?.landmark}, "
                } else {
                    ""
                }
        address += "${order?.shippingAddress?.locality}, " +
                "${order?.shippingAddress?.city}, " +
                "${order?.shippingAddress?.stateOrRegion} - " +
                "${order?.shippingAddress?.pinCode}"
        binding?.shippingAddress?.text = address

        binding?.paymentMethod?.text = order?.paymentMethod
        binding?.discount?.text = order?.discount

        val totalAmount = "\u20B9 ${order?.totalAmount}"
        binding?.totalAmount?.text = totalAmount
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it,"Order Details")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MOrderedProduct, position: Int, view: View) {
        Toast.makeText(context, "Text", Toast.LENGTH_SHORT).show()
    }

}