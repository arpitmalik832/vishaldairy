package com.vishaldairy.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.adapter.ProductsAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentProductsBinding
import com.vishaldairy.model.MCategory
import com.vishaldairy.model.MProduct
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome

class FragmentProducts: BaseFragment<FragmentProductsBinding>(), OnClick<MProduct> {

    var binding: FragmentProductsBinding? = null
    private var viewModel: VMHome? = null
    private var adapterProducts: ProductsAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_products

    override fun initViews() {

        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterProducts = context?.let {context ->
            viewModel?.let { viewModel ->
                ProductsAdapter(
                    context,
                    viewModel,
                    this,
                    this,
                    ArrayList()
                )
            }
        }
        binding?.expandableListView?.setAdapter(adapterProducts)

    }

    override fun initListeners() {

        viewModel?.subCategoriesList?.observe(this, Observer {
            if(it.isNotEmpty()) {
                adapterProducts?.setDataList(it)
            } else {
                binding?.expandableListView?.visibility = View.GONE
                binding?.noProductsLayout?.visibility = View.VISIBLE
            }
        })

        binding?.expandableListView?.setOnGroupClickListener { _: ExpandableListView, v: View, _: Int, _: Long ->

            val groupNavigationImage = v.findViewById<AppCompatImageView>(R.id.navigation_image)

            if(groupNavigationImage.tag == "0") {
                groupNavigationImage.setImageResource(R.drawable.icon_arrow_up)
                groupNavigationImage.tag = "1"
            } else {
                groupNavigationImage.setImageResource(R.drawable.icon_arrow_down)
                groupNavigationImage.tag = "0"
            }

            false

        }

        binding?.cartIcon?.setOnClickListener {
            replaceFragment(
                FragmentShoppingCart(),
                R.id.frame_layout,
                "10",
                true
            )
        }

    }

    override fun bindDataWithViews() {
        val categoryString = arguments?.getString("category")
        val category: MCategory? = Gson().fromJson(
            categoryString,
            MCategory::class.java
        )

        initActionBar()

        category?.id?.let {
            viewModel?.getProducts(
                it,
                this,
                1
            )
        }

        val noOfProductsText = "${category?.count} Products"
        binding?.noOfProducts?.text = noOfProductsText
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        context?.let {
            setToolbarBackgroundColor(ContextCompat.getColor(it,R.color.blue4))
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MProduct, position: Int, view: View) {
        if(view.id == R.id.subscribe_button_text) {
            val fragmentAddSubscription = FragmentAddSubscription()
            val args = Bundle()
            val beanString = Gson().toJson(bean)
            args.putString("data",beanString)
            fragmentAddSubscription.arguments = args
            replaceFragment(
                fragmentAddSubscription,
                R.id.frame_layout,
                "2",
                true
            )
        }
    }

}