package com.vishaldairy.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.adapter.DashboardCategoriesAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentDashboardBinding
import com.vishaldairy.model.MCategory
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.activity.SearchActivity
import com.vishaldairy.utils.Action
import com.vishaldairy.viewModel.VMHome

class FragmentDashboard: BaseFragment<FragmentDashboardBinding>(), OnClick<MCategory> {

    var binding: FragmentDashboardBinding? = null
    private var viewModel: VMHome? = null
    private var adapterCategory: DashboardCategoriesAdapter? = null
    //private var adapterOtherFoodItem: DashboardOtherFoodItemsAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_dashboard

    override fun initViews() {

        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterCategory = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    DashboardCategoriesAdapter(
                        context,
                        viewModel,
                        binding,
                        this,
                        ArrayList()
                    )
                }
            }
        }
        binding?.categoriesRecyclerView?.layoutManager = GridLayoutManager(
            context,
            3
        )
        binding?.categoriesRecyclerView?.adapter = adapterCategory
        binding?.categoriesRecyclerView?.setHasFixedSize(true)

        /*adapterOtherFoodItem = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    DashboardOtherFoodItemsAdapter(
                        context,
                        viewModel,
                        binding,
                        this,
                        ArrayList()
                    )
                }
            }
        }
        binding?.otherFoodItemsRecyclerView?.layoutManager = GridLayoutManager(
            context,
            2
        )
        binding?.otherFoodItemsRecyclerView?.adapter = adapterOtherFoodItem
        binding?.otherFoodItemsRecyclerView?.setHasFixedSize(true)*/

    }

    override fun initListeners() {
        viewModel?.categoriesList?.observe(this, Observer {
            if(it.isNotEmpty()) {
                adapterCategory?.setDataList(it)
            } else {
                binding?.categoriesRecyclerView?.visibility = View.GONE
                binding?.noCategoriesLayout?.visibility = View.VISIBLE
            }

            /*viewModel?.otherFoodItemsList?.let {
                adapterOtherFoodItem?.setDataList(it)
            }*/
        })

        binding?.searchSectionEditText?.setOnClickListener {
            startActivity(Intent(context,SearchActivity::class.java))
        }

        binding?.drawerLayout?.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                drawerLayoutOptions()
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

    }

    override fun bindDataWithViews() {
        initActionBar()

        viewModel?.getCategories(
            this,
            1
        )
       // viewModel?.getOtherFoodItems()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        context?.let {
            setToolbarBackgroundColor(ContextCompat.getColor(it,R.color.colorPrimary))
        }
        binding?.title?.let {
            setToolbarTitle(it, "Vishal Dairy")
            alignTitleCenter(it)
            setToolbarTitleTextColor(it, Color.WHITE)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_menu_white)
    }

    override fun onClick(bean: MCategory, position: Int, view: View) {
        val fragmentProducts = FragmentProducts()

        val args = Bundle()
        val categoryString = Gson().toJson(bean)
        args.putString("category",categoryString)

        fragmentProducts.arguments = args
        replaceFragment(
            fragmentProducts,
            R.id.frame_layout,
            "1",
            true
        )
    }

    fun drawerLayoutOptions() {
        val closeBtn: AppCompatImageButton? = view?.findViewById(R.id.close_btn)

        val myProfileLayout: ConstraintLayout? = view?.findViewById(R.id.my_profile_layout)

        val myOrdersLayout: ConstraintLayout? = view?.findViewById(R.id.my_order_layout)
        val myOrdersIsSelected: AppCompatImageView? = view?.findViewById(R.id.my_order_is_selected)

        val myWalletLayout: ConstraintLayout? = view?.findViewById(R.id.my_wallet_layout)
        val myWalletIsSelected: AppCompatImageView? = view?.findViewById(R.id.my_wallet_is_selected)
        val walletBalance: AppCompatTextView? = view?.findViewById(R.id.wallet_balance)

        val myCartLayout: ConstraintLayout? = view?.findViewById(R.id.my_cart_layout)
        val myCartIsSelected: AppCompatImageView? = view?.findViewById(R.id.my_cart_is_selected)

        val mySubscriptionsLayout: ConstraintLayout? = view?.findViewById(R.id.my_subscriptions_layout)
        val mySubscriptionsIsSelected: AppCompatImageView? = view?.findViewById(R.id.my_subscriptions_is_selected)

        val setVacationLayout: ConstraintLayout? = view?.findViewById(R.id.vacation_layout)
        val setVacationIsSelected: AppCompatImageView? = view?.findViewById(R.id.vacation_is_selected)

        val helpSupportLayout: ConstraintLayout? = view?.findViewById(R.id.support_layout)
        val helpSupportIsSelected: AppCompatImageView? = view?.findViewById(R.id.support_is_selected)

        val profileNameView: AppCompatTextView? = view?.findViewById(R.id.my_profile_name)
        val profilePlace: AppCompatTextView? = view?.findViewById(R.id.my_profile_place)

        profileNameView?.text = if(!TextUtils.isEmpty(Action.getUser().name)) Action.getUser().name else {"User"}
        profilePlace?.text =if(!TextUtils.isEmpty(Action.getUserAddress().city)) Action.getUser().name else {"No Location"}

        viewModel?.fetchBalance(walletBalance = walletBalance)

        closeBtn?.setOnClickListener {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        myProfileLayout?.setOnClickListener {
            replaceFragment(
                FragmentMyProfile(),
                R.id.frame_layout,
                "3",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        myOrdersLayout?.setOnClickListener {
            myOrdersIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentMyOrders(),
                R.id.frame_layout,
                "4",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        myWalletLayout?.setOnClickListener {
            myWalletIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentMyWallet(),
                R.id.frame_layout,
                "6",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        myCartLayout?.setOnClickListener {
            myCartIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentShoppingCart(),
                R.id.frame_layout,
                "10",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        mySubscriptionsLayout?.setOnClickListener {
            mySubscriptionsIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentMySubscriptions(),
                R.id.frame_layout,
                "13",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        setVacationLayout?.setOnClickListener {
            setVacationIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentSetVacation(),
                R.id.frame_layout,
                "14",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

        helpSupportLayout?.setOnClickListener {
            helpSupportIsSelected?.visibility = View.VISIBLE
            replaceFragment(
                FragmentHelpSupport(),
                R.id.frame_layout,
                "15",
                true
            )
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }

    }

}