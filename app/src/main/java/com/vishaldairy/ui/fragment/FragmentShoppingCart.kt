package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishaldairy.R
import com.vishaldairy.adapter.ShoppingCartAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentShoppingCartBinding
import com.vishaldairy.model.MCartItem
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.utils.AppMethods
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.viewModel.VMHome

class FragmentShoppingCart: BaseFragment<FragmentShoppingCartBinding>(),OnClick<MCartItem> {

    var binding: FragmentShoppingCartBinding? = null
    private var viewModel: VMHome? = null
    var adapterCartItem: ShoppingCartAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_shopping_cart

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterCartItem = context?.let {context ->
            viewModel?.let {viewModel ->
                ShoppingCartAdapter(
                    context,
                    viewModel,
                    this,
                    this,
                    ArrayList()
                )
            }
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.itemsRecyclerView?.layoutManager = layoutManager
        binding?.itemsRecyclerView?.adapter = adapterCartItem
        binding?.itemsRecyclerView?.setHasFixedSize(true)
    }

    override fun initListeners() {

        viewModel?.cart?.observe(this, Observer {
            it.items?.let {it1 ->
                if(it1.isNotEmpty()) {
                    adapterCartItem?.setDataList(it1)
                    binding?.emptyCartLayout?.visibility = View.GONE
                    binding?.nestedScrollView?.visibility = View.VISIBLE
                    binding?.bottomDetailsLayout?.visibility = View.VISIBLE
                    binding?.cartAmount?.text = AppMethods.formatPriceText(it.total_price.toString())
                    val totalPrice = it.total_price?.plus(binding?.serviceFee?.text.toString().substring(2).toInt())
                    binding?.totalPrice?.text = AppMethods.formatPriceText(totalPrice.toString())
                } else {
                    binding?.nestedScrollView?.visibility = View.GONE
                    binding?.bottomDetailsLayout?.visibility = View.GONE
                    binding?.emptyCartLayout?.visibility = View.VISIBLE
                }
            }
        })

        binding?.refreshBtn?.setOnClickListener {
            viewModel?.getCart(this)
        }

        binding?.selectShippingAddressBtn?.setOnClickListener {
            var isActive = true
            viewModel?.cart?.value?.items?.let { list ->
                for (i in list.indices) {
                    if(list[i].stock_status == OtherConstants.PRODUCT_OUT_OF_STOCK) {
                        isActive = false
                        break
                    }
                }
            }
            if(isActive) {
                val fragmentShippingAddress = FragmentShippingAddress()

                val args = Bundle()
                args.putString("requestFrom", "cart")

                fragmentShippingAddress.arguments = args
                fragmentShippingAddress.setTargetFragment(
                    this,
                    OtherConstants.SELECT_SHIPPING_ADDRESS_CODE
                )
                replaceFragment(
                    fragmentShippingAddress,
                    R.id.frame_layout,
                    "11",
                    true
                )
            } else {
                (activity as ActivityMain?)?.binding?.snackBarLayout?.let {snackBarLayout ->
                    showMessage(
                        "Please remove Out of Stock Items",
                        "",
                        snackBarLayout
                    )
                }
            }
        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        viewModel?.getCart(this)

    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "Shopping Cart")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MCartItem, position: Int, view: View) {
    }
}

