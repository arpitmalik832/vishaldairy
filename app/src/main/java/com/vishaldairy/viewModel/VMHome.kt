package com.vishaldairy.viewModel

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.vishaldairy.R
import com.vishaldairy.adapter.ShoppingCartAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.base.BaseViewModel
import com.vishaldairy.model.*
import com.vishaldairy.navigator.NavigatorBase
import com.vishaldairy.resource.client.ResponseValidation
import com.vishaldairy.resource.responseModel.*
import com.vishaldairy.resource.service.*
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.ui.fragment.*
import com.vishaldairy.utils.Action
import com.vishaldairy.utils.AppMethods
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.utils.RequestUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VMHome : BaseViewModel<NavigatorBase>() {

    var categoriesList = MutableLiveData<ArrayList<MCategory>>()

    private val products = LinkedHashMap<Int, MProduct>()
    private val productsInCart = LinkedHashMap<Int, MCartItem>()

    private var subCategoriesListWithoutProducts = MutableLiveData<ArrayList<MCategory>>()
    var subCategoriesList = MutableLiveData<ArrayList<MSubCategory>>()

    var cart = MutableLiveData<MCart>()

    var paymentMethodsList = MutableLiveData<ArrayList<MPaymentMethod>>()

    var ordersList = ArrayList<MOrder>()
    var orderProductListMap = LinkedHashMap<Int, ArrayList<MOrderedProduct>>()
    var addressesList = ArrayList<MAddress>()
    var subscriptionsList = ArrayList<MSubscription>()
    private var subscriptions = LinkedHashMap<String, MSubscription>()
    var faqList = ArrayList<MFaq>()
    private var faqs = LinkedHashMap<String, MFaq>()

    fun getCategories(
        fragment: FragmentDashboard,
        pageNo: Int
    ) {
        var categoryList: ArrayList<MCategory>? = null
        var subCategoryList: ArrayList<MCategory>? = null
        if (pageNo == 1) {
            categoryList = ArrayList()
            subCategoryList = ArrayList()
            fragment.showLoading()
            (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
            fragment.binding?.parentLayout?.visibility = View.INVISIBLE
        }

        ServiceCategories(
            RequestUtil.getPerPage(),
            RequestUtil.getPage(pageNo)
        ).call.enqueue(object : Callback<ArrayList<MCategoryResponse>> {
            override fun onResponse(
                call: Call<ArrayList<MCategoryResponse>>,
                response: Response<ArrayList<MCategoryResponse>>
            ) {
                if (ResponseValidation<ArrayList<MCategoryResponse>>().ifResponseIsNotSuccessful(
                        response
                    )
                ) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    fragment.binding?.categoriesRecyclerView?.visibility = View.GONE
                    fragment.binding?.errorLayout?.visibility = View.VISIBLE
                    return
                }
                val responseBody = response.body()
                responseBody?.let {
                    for (i in it.indices) {
                        if (it[i].parent == OtherConstants.PARENT_CATEGORY_ID) {
                            val category = MCategory(
                                it[i].id,
                                it[i].name,
                                it[i].parent,
                                it[i].count,
                                it[i].description,
                                it[i].image?.id,
                                it[i].image?.src
                            )
                            categoryList?.add(category)
                        } else {
                            val category = MCategory(
                                it[i].id,
                                it[i].name,
                                it[i].parent,
                                it[i].count,
                                it[i].description,
                                it[i].image?.id,
                                it[i].image?.src
                            )
                            subCategoryList?.add(category)
                        }
                    }
                }
                if (responseBody?.size != OtherConstants.POSTS_PER_PAGE) {
                    categoriesList.value = categoryList
                    subCategoriesListWithoutProducts.value = subCategoryList

                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                }
                if (responseBody?.size == OtherConstants.POSTS_PER_PAGE) {
                    val reqPageNo = pageNo + 1
                    getCategories(fragment, reqPageNo)
                }
            }

            override fun onFailure(call: Call<ArrayList<MCategoryResponse>>, t: Throwable) {
                Log.e("ServiceCategories", "onFailure: $t")

                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
                fragment.binding?.categoriesRecyclerView?.visibility = View.GONE
                fragment.binding?.errorLayout?.visibility = View.VISIBLE
            }
        })
    }

    fun getData(
        pageNo: Int
    ) {
        ServiceAllProducts(
            RequestUtil.getPerPage(),
            RequestUtil.getPage(pageNo)
        ).call.enqueue(object : Callback<ArrayList<MProductResponse>> {
            override fun onResponse(
                call: Call<ArrayList<MProductResponse>>,
                response: Response<ArrayList<MProductResponse>>
            ) {
                val responseBody = response.body()
                responseBody?.let {
                    for (i in it.indices) {
                        var status = ""
                        if (it[i].stock_status == OtherConstants.RESPONSE_IN_STOCK) {
                            status = OtherConstants.PRODUCT_IN_STOCK
                        }
                        if (it[i].stock_status == OtherConstants.RESPONSE_OUT_OF_STOCK) {
                            status = OtherConstants.PRODUCT_OUT_OF_STOCK
                        }
                        val product = MProduct(
                            it[i].id,
                            it[i].name,
                            it[i].images?.get(0)?.id,
                            it[i].images?.get(0)?.src,
                            it[i].price,
                            it[i].regular_price,
                            it[i].sale_price,
                            it[i].description,
                            it[i].type,
                            it[i].status,
                            it[i].featured,
                            it[i].catalog_visibility,
                            it[i].on_sale,
                            it[i].purchasable,
                            it[i].total_sales,
                            it[i].tax_status,
                            it[i].manage_stock,
                            it[i].stock_quantity,
                            status
                        )
                        product.id?.let { id ->
                            products[id] = product
                        }
                    }
                    if (responseBody.size == OtherConstants.POSTS_PER_PAGE) {
                        val reqPageNo = pageNo + 1
                        getData(reqPageNo)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<MProductResponse>>, t: Throwable) {
                Log.e("ServiceAllProducts", "onFailure: $t")
            }
        })
    }

    fun getProducts(
        categoryId: Int,
        fragment: FragmentProducts,
        pageNo: Int
    ) {
        fragment.showLoading()
        (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
        fragment.binding?.parentLayout?.visibility = View.INVISIBLE

        val subCategories = LinkedHashMap<Int, MSubCategory>()
        val subCategoriesTempList = ArrayList<MSubCategory>()
        val temp = ArrayList<MCategory>()
        subCategoriesListWithoutProducts.value?.let {
            for (i in it.indices) {
                if (it[i].parentId == categoryId) {
                    temp.add(it[i])
                }
            }
        }

        if (temp.isEmpty()) {
            categoriesList.value?.let {
                for (i in it.indices) {
                    if (it[i].id == categoryId) {
                        temp.add(it[i])
                    }
                }
            }
        }

        for (s in temp.indices) {
            temp[s].id?.let { subCategoryId ->
                addSubCategory(
                    fragment,
                    subCategoryId,
                    subCategories,
                    temp,
                    s,
                    subCategoriesTempList,
                    pageNo
                )
            }
        }
    }

    private fun addSubCategory(
        fragment: FragmentProducts,
        subCategoryId: Int,
        subCategories: LinkedHashMap<Int, MSubCategory>,
        temp: ArrayList<MCategory>,
        s: Int,
        subCategoriesTempList: ArrayList<MSubCategory>,
        pageNo: Int
    ) {
        ServiceProducts(
            RequestUtil.getPerPage(),
            RequestUtil.getCategoryId(subCategoryId),
            RequestUtil.getPage(pageNo)
        ).call.enqueue(object : Callback<ArrayList<MProductResponse>> {
            override fun onResponse(
                call: Call<ArrayList<MProductResponse>>,
                response: Response<ArrayList<MProductResponse>>
            ) {
                if (ResponseValidation<ArrayList<MProductResponse>>().ifResponseIsNotSuccessful(
                        response
                    )
                ) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    fragment.binding?.expandableListView?.visibility = View.GONE
                    fragment.binding?.errorLayout?.visibility = View.VISIBLE
                    return
                }
                val responseBody = response.body()
                responseBody?.let {
                    for (i in it.indices) {
                        var tempSubCategory = subCategories[subCategoryId]
                        if (tempSubCategory == null) {
                            tempSubCategory = MSubCategory(
                                temp[s].id,
                                temp[s].name,
                                temp[s].parentId,
                                temp[s].count,
                                temp[s].description,
                                temp[s].imageId,
                                temp[s].imageSrc
                            )
                            subCategories[subCategoryId] = tempSubCategory
                            subCategoriesTempList.add(tempSubCategory)
                        }

                        var productList = tempSubCategory.productList
                        if (productList == null) {
                            productList = ArrayList()
                        }

                        var status = ""
                        if (it[i].stock_status == OtherConstants.RESPONSE_IN_STOCK) {
                            status = OtherConstants.PRODUCT_IN_STOCK
                        }
                        if (it[i].stock_status == OtherConstants.RESPONSE_OUT_OF_STOCK) {
                            status = OtherConstants.PRODUCT_OUT_OF_STOCK
                        }

                        val product = MProduct(
                            it[i].id,
                            it[i].name,
                            it[i].images?.get(0)?.id,
                            it[i].images?.get(0)?.src,
                            it[i].price,
                            it[i].regular_price,
                            it[i].sale_price,
                            it[i].description,
                            it[i].type,
                            it[i].status,
                            it[i].featured,
                            it[i].catalog_visibility,
                            it[i].on_sale,
                            it[i].purchasable,
                            it[i].total_sales,
                            it[i].tax_status,
                            it[i].manage_stock,
                            it[i].stock_quantity,
                            status
                        )

                        var alreadyExist = false

                        for (p in productList.indices) {
                            if (productList[p].id == product.id) {
                                alreadyExist = true
                                break
                            }
                        }

                        if (!alreadyExist) {
                            productList.add(product)
                            tempSubCategory.productList = productList
                        }
                    }

                }

                if (responseBody?.size != OtherConstants.POSTS_PER_PAGE) {
                    subCategoriesList.value = subCategoriesTempList

                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                }
                if (responseBody?.size == OtherConstants.POSTS_PER_PAGE) {
                    val reqPageNo = pageNo + 1
                    addSubCategory(
                        fragment,
                        subCategoryId,
                        subCategories,
                        temp,
                        s,
                        subCategoriesTempList,
                        reqPageNo
                    )
                }
            }

            override fun onFailure(call: Call<ArrayList<MProductResponse>>, t: Throwable) {
                Log.e("ServiceProducts", "onFailure: $t")

                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
                fragment.binding?.expandableListView?.visibility = View.GONE
                fragment.binding?.errorLayout?.visibility = View.VISIBLE
            }
        })
    }

    fun getCart(
        fragment: FragmentShoppingCart
    ) {
        fragment.showLoading()
        (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
        fragment.binding?.parentLayout?.visibility = View.INVISIBLE

        ServiceCart().call.enqueue(object : Callback<MCartResponse> {
            override fun onResponse(
                call: Call<MCartResponse>,
                response: Response<MCartResponse>
            ) {
                if (ResponseValidation<MCartResponse>().ifResponseIsNotSuccessful(response)) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    fragment.binding?.nestedScrollView?.visibility = View.GONE
                    fragment.binding?.bottomDetailsLayout?.visibility = View.GONE
                    fragment.binding?.errorLayout?.visibility = View.VISIBLE
                    return
                }
                val responseBody = response.body()
                val itemsList = ArrayList<MCartItem>()

                responseBody?.items?.let { items ->
                    for (i in items.indices) {
                        if (products[items[i].id] != null) {
                            val item = MCartItem(
                                items[i].id,
                                items[i].name,
                                items[i].key,
                                items[i].quantity,
                                items[i].images?.get(0)?.id,
                                items[i].images?.get(0)?.thumbnail,
                                products[items[i].id]?.regular_price,
                                items[i].price,
                                items[i].line_price,
                                products[items[i].id]?.on_sale,
                                products[items[i].id]?.purchasable,
                                products[items[i].id]?.manage_stock,
                                products[items[i].id]?.stock_quantity,
                                products[items[i].id]?.stock_status
                            )
                            itemsList.add(item)
                            item.id?.let { id ->
                                productsInCart[id] = item
                            }
                        } else {
                            val item = MCartItem(
                                items[i].id,
                                items[i].name,
                                items[i].key,
                                items[i].quantity,
                                items[i].images?.get(0)?.id,
                                items[i].images?.get(0)?.thumbnail,
                                items[i].price,
                                "",
                                items[i].line_price,
                                false,
                                purchasable = true,
                                manage_stock = true,
                                stock_quantity = 0,
                                stock_status = OtherConstants.PRODUCT_IN_STOCK
                            )
                            itemsList.add(item)
                            item.id?.let { id ->
                                productsInCart[id] = item
                            }
                        }
                    }
                }
                val myCart = MCart(
                    responseBody?.currency,
                    responseBody?.item_count,
                    itemsList,
                    responseBody?.needs_shipping,
                    responseBody?.total_price,
                    responseBody?.total_weight
                )
                cart.value = myCart

                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<MCartResponse>, t: Throwable) {
                Log.e("ServiceCart", "onFailure: $t")

                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
                fragment.binding?.nestedScrollView?.visibility = View.GONE
                fragment.binding?.bottomDetailsLayout?.visibility = View.GONE
                fragment.binding?.errorLayout?.visibility = View.VISIBLE
            }
        })
    }

    fun addItemToCart(
        product: MProduct,
        fragment: FragmentProducts,
        addBtn: ConstraintLayout,
        addBtnText: AppCompatTextView,
        addBtnFirstImage: AppCompatImageView,
        addBtnSecondImage: AppCompatImageView
    ) {
        product.id?.let { productId ->
            ServiceAddItemToCart(
                RequestUtil.getAddToCartBody(productId)
            ).call.enqueue(object : Callback<MCartItemResponse> {
                override fun onResponse(
                    call: Call<MCartItemResponse>,
                    response: Response<MCartItemResponse>
                ) {
                    if (ResponseValidation<MCartItemResponse>().ifResponseIsNotSuccessful(response)) {
                        (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                            fragment.showMessage(
                                "Some Error, Try again later",
                                "",
                                it
                            )
                        }
                        addBtn.isEnabled = true
                        fragment.context?.let { context ->
                            addBtnText.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        return
                    }

                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        fragment.showMessage(
                            "${product.name} added, you can check your cart",
                            "",
                            it
                        )
                    }
                    val responseBody = response.body()
                    val item = MCartItem(
                        responseBody?.id,
                        responseBody?.name,
                        responseBody?.key,
                        responseBody?.quantity,
                        responseBody?.images?.get(0)?.id,
                        responseBody?.images?.get(0)?.thumbnail,
                        product.regular_price,
                        responseBody?.price,
                        responseBody?.line_price,
                        product.on_sale,
                        product.purchasable,
                        product.manage_stock,
                        product.stock_quantity,
                        product.stock_status
                    )
                    responseBody?.id?.let { id ->
                        productsInCart[id] = item
                    }
                    addBtnFirstImage.setImageResource(R.drawable.icon_remove_color_primary)
                    addBtnText.text = "1"
                    fragment.context?.let { context ->
                        addBtnText.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    }
                    addBtnSecondImage.visibility = View.VISIBLE
                    addBtn.isEnabled = true
                }

                override fun onFailure(call: Call<MCartItemResponse>, t: Throwable) {
                    Log.e("ServiceAddItemToCart", "onFailure: $t")

                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        fragment.showMessage(
                            "Some Error, Try again later",
                            "",
                            it
                        )
                    }
                    fragment.context?.let { context ->
                        addBtnText.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    }
                    addBtn.isEnabled = true
                }
            })
        }
    }

    fun updateQuantityOfItemInCart(
        quantity: Int,
        fragment: Fragment,
        requestFrom: String,
        addBtnFirstImage: AppCompatImageView? = null,
        addBtnSecondImage: AppCompatImageView? = null,
        addBtnText: AppCompatTextView? = null,
        product: MProduct? = null,
        data: ArrayList<MCartItem>? = null,
        position: Int? = null,
        holder: ShoppingCartAdapter.ViewHolder? = null
    ) {
        var key = ""
        if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE || requestFrom == OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE) {
            productsInCart[product?.id]?.key?.let { key = it }
        }
        if (requestFrom == OtherConstants.CART_QUANTITY_BTN_FIRST_IMAGE || requestFrom == OtherConstants.CART_QUANTITY_BTN_SECOND_IMAGE) {
            key = position?.let { productsInCart[data?.get(it)?.id] }?.key.toString()
        }
        ServiceUpdateQuantityOfItemInCart(
            key,
            RequestUtil.updateQuantityOfItemInStock(quantity)
        ).call.enqueue(object : Callback<MCartItemResponse> {
            override fun onResponse(
                call: Call<MCartItemResponse>,
                response: Response<MCartItemResponse>
            ) {
                if (ResponseValidation<MCartItemResponse>().ifResponseIsNotSuccessful(response)) {
                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        (fragment as BaseFragment<*>?)?.showMessage(
                            "Some Error, Try again later",
                            "",
                            it
                        )
                    }
                    if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
                        addBtnFirstImage?.isEnabled = true
                        addBtnFirstImage?.setImageResource(R.drawable.icon_remove_color_primary)
                    }
                    if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE) {
                        addBtnSecondImage?.isEnabled = true
                        addBtnSecondImage?.setImageResource(R.drawable.icon_add_color_primary)
                    }
                    if (requestFrom == OtherConstants.CART_QUANTITY_BTN_FIRST_IMAGE) {
                        holder?.decreaseBtn?.isEnabled = true
                        holder?.decreaseBtn?.setImageResource(R.drawable.icon_remove_color_primary)
                    }
                    if (requestFrom == OtherConstants.CART_QUANTITY_BTN_SECOND_IMAGE) {
                        holder?.increaseBtn?.isEnabled = true
                        holder?.increaseBtn?.setImageResource(R.drawable.icon_add_color_primary)
                    }
                    return
                }

                val responseBody = response.body()
                val item = MCartItem(
                    responseBody?.id,
                    responseBody?.name,
                    responseBody?.key,
                    responseBody?.quantity,
                    responseBody?.images?.get(0)?.id,
                    responseBody?.images?.get(0)?.thumbnail,
                    product?.regular_price,
                    responseBody?.price,
                    responseBody?.line_price,
                    product?.on_sale,
                    product?.purchasable,
                    product?.manage_stock,
                    product?.stock_quantity,
                    product?.stock_status
                )
                responseBody?.id?.let { id ->
                    productsInCart[id] = item
                }

                if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE || requestFrom == OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE) {
                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        (fragment as BaseFragment<*>?)?.showMessage(
                            "${product?.name} updated, you can check your cart",
                            "",
                            it
                        )
                    }
                    if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
                        addBtnText?.text = quantity.toString()
                        addBtnFirstImage?.isEnabled = true
                        addBtnFirstImage?.setImageResource(R.drawable.icon_remove_color_primary)
                    }
                    if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE) {
                        addBtnText?.text = quantity.toString()
                        addBtnSecondImage?.isEnabled = true
                        addBtnSecondImage?.setImageResource(R.drawable.icon_add_color_primary)
                    }
                }

                if (requestFrom == OtherConstants.CART_QUANTITY_BTN_FIRST_IMAGE) {
                    getCart(fragment as FragmentShoppingCart)
                    holder?.decreaseBtn?.isEnabled = true
                    holder?.decreaseBtn?.setImageResource(R.drawable.icon_remove_color_primary)
                }
                if (requestFrom == OtherConstants.CART_QUANTITY_BTN_SECOND_IMAGE) {
                    getCart(fragment as FragmentShoppingCart)
                    holder?.increaseBtn?.isEnabled = true
                    holder?.increaseBtn?.setImageResource(R.drawable.icon_add_color_primary)
                }
            }

            override fun onFailure(call: Call<MCartItemResponse>, t: Throwable) {
                Log.e("ServiceUpdateQuantity", "onFailure: $t")

                (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                    (fragment as BaseFragment<*>?)?.showMessage(
                        "Some Error, Try again later",
                        "",
                        it
                    )
                }
                if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
                    addBtnFirstImage?.isEnabled = true
                    addBtnFirstImage?.setImageResource(R.drawable.icon_remove_color_primary)
                }
                if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE) {
                    addBtnSecondImage?.isEnabled = true
                    addBtnSecondImage?.setImageResource(R.drawable.icon_add_color_primary)
                }
                if (requestFrom == OtherConstants.CART_QUANTITY_BTN_FIRST_IMAGE) {
                    holder?.decreaseBtn?.isEnabled = true
                    holder?.decreaseBtn?.setImageResource(R.drawable.icon_remove_color_primary)
                }
                if (requestFrom == OtherConstants.CART_QUANTITY_BTN_SECOND_IMAGE) {
                    holder?.increaseBtn?.isEnabled = true
                    holder?.increaseBtn?.setImageResource(R.drawable.icon_add_color_primary)
                }
            }
        })
    }

    fun deleteItemFromCart(
        fragment: Fragment,
        requestFrom: String,
        addBtnFirstImage: AppCompatImageView? = null,
        addBtnSecondImage: AppCompatImageView? = null,
        addBtnText: AppCompatTextView? = null,
        product: MProduct? = null,
        data: ArrayList<MCartItem>? = null,
        position: Int? = null,
        holder: ShoppingCartAdapter.ViewHolder? = null
    ) {
        var key = ""
        if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
            key = productsInCart[product?.id]?.key.toString()
        }
        if (requestFrom == OtherConstants.CART_REMOVE_BTN) {
            key = position?.let { productsInCart[data?.get(it)?.id] }?.key.toString()
        }

        ServiceRemoveItemFromCart(
            key
        ).call.enqueue(object : Callback<MCartItemResponse> {
            override fun onResponse(
                call: Call<MCartItemResponse>,
                response: Response<MCartItemResponse>
            ) {
                if (!response.isSuccessful) {
                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        (fragment as BaseFragment<*>?)?.showMessage(
                            "Some Error, Try again later",
                            "",
                            it
                        )
                        if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE)
                            addBtnFirstImage?.isEnabled = true
                        addBtnFirstImage?.setImageResource(R.drawable.icon_remove_color_primary)
                    }
                    if (requestFrom == OtherConstants.CART_REMOVE_BTN) {
                        holder?.removeBtn?.isEnabled = true
                        holder?.removeBtn?.background =
                            (fragment as FragmentShoppingCart?)?.context?.let { context ->
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.background_d70d0d_fill_with_corners_15dp
                                )
                            }
                    }
                    return
                }

                if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
                    (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                        (fragment as BaseFragment<*>?)?.showMessage(
                            "${product?.name} removed, you can check your cart",
                            "",
                            it
                        )
                    }

                    addBtnFirstImage?.setImageResource(R.drawable.icon_add_color_primary)
                    addBtnText?.text = OtherConstants.ADD_BTN_TEXT
                    addBtnSecondImage?.visibility = View.GONE
                    addBtnFirstImage?.isEnabled = true

                    product?.id?.let { id ->
                        if (productsInCart[id] != null)
                            productsInCart.remove(id)
                    }
                }
                if (requestFrom == OtherConstants.CART_REMOVE_BTN) {
                    data?.let {
                        position?.let {
                            data[position].id?.let { id ->
                                if (productsInCart[id] != null)
                                    productsInCart.remove(id)
                            }
                        }
                        getCart(fragment as FragmentShoppingCart)
                        holder?.removeBtn?.isEnabled = true
                        holder?.removeBtn?.background =
                            (fragment as FragmentShoppingCart?)?.context?.let { context ->
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.background_d70d0d_fill_with_corners_15dp
                                )
                            }
                    }
                }
            }

            override fun onFailure(call: Call<MCartItemResponse>, t: Throwable) {
                Log.e("ServiceRemoveItem", "onFailure: $t")

                (fragment.activity as ActivityMain).binding?.snackBarLayout?.let {
                    (fragment as BaseFragment<*>?)?.showMessage(
                        "Some Error, Try again later",
                        "",
                        it
                    )
                }
                if (requestFrom == OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE) {
                    addBtnFirstImage?.isEnabled = true
                    addBtnFirstImage?.setImageResource(R.drawable.icon_remove_color_primary)
                }
                if (requestFrom == OtherConstants.CART_REMOVE_BTN) {
                    holder?.removeBtn?.isEnabled = true
                    holder?.removeBtn?.background =
                        (fragment as FragmentShoppingCart?)?.context?.let { context ->
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_d70d0d_fill_with_corners_15dp
                            )
                        }
                }
            }
        })

    }

    fun fetchPaymentMethods(
        fragment: FragmentAddMoney
    ) {
        fragment.showLoading()
        (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
        fragment.binding?.parentLayout?.visibility = View.INVISIBLE

        ServiceFetchPaymentMethods().call.enqueue(object :
            Callback<ArrayList<MPaymentMethodResponse>> {
            override fun onResponse(
                call: Call<ArrayList<MPaymentMethodResponse>>,
                response: Response<ArrayList<MPaymentMethodResponse>>
            ) {
                if (ResponseValidation<ArrayList<MPaymentMethodResponse>>().ifResponseIsNotSuccessful(
                        response
                    )
                ) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    return
                }
                val paymentMethods = ArrayList<MPaymentMethod>()
                val responseBody = response.body()
                responseBody?.let {
                    for (i in it.indices) {
                        val paymentMethod = MPaymentMethod(
                            it[i].id, it[i].title, R.drawable.icon_credit_card, it[i].description
                        )
                        paymentMethods.add(paymentMethod)
                    }
                }
                paymentMethodsList.value = paymentMethods
                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<ArrayList<MPaymentMethodResponse>>, t: Throwable) {
                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
            }
        })
    }

    fun fetchBalance(
        fragment: Fragment? = null,
        walletBalance: AppCompatTextView? = null
    ) {
        if (fragment is FragmentMyWallet) {
            fragment.showLoading()
            (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
            fragment.binding?.parentLayout?.visibility = View.INVISIBLE
        }

        ServiceFetchBalance(
            RequestUtil.fetchBalanceReqBody()
        ).call.enqueue(object : Callback<MWalletResponse> {
            override fun onResponse(
                call: Call<MWalletResponse>,
                response: Response<MWalletResponse>
            ) {
                if (ResponseValidation<MWalletResponse>().ifResponseIsNotSuccessful(response)) {
                    if (fragment is FragmentMyWallet) {
                        fragment.hideLoading()
                        fragment.binding?.parentLayout?.visibility = View.VISIBLE
                        (fragment.activity as ActivityMain?)?.binding?.snackBarLayout?.let { snackBarLayout ->
                            fragment.showMessage(
                                "Sorry, Please try again later",
                                "",
                                snackBarLayout
                            )
                        }
                    }
                    if (walletBalance != null) {
                        walletBalance.text = OtherConstants.ERROR
                    }
                    return
                }
                val responseBody = response.body()
                if (fragment is FragmentMyWallet) {
                    fragment.binding?.walletBalance?.text = responseBody?.amount?.let {
                        AppMethods.formatPriceText(it)
                    }
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                }
                if (walletBalance != null) {
                    walletBalance.text =
                        responseBody?.amount?.let { AppMethods.formatPriceText(it) }
                }
            }

            override fun onFailure(call: Call<MWalletResponse>, t: Throwable) {
                Log.e("ServiceFetchBalance", "onFailure: $t")

                if (fragment is FragmentMyWallet) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    (fragment.activity as ActivityMain?)?.binding?.snackBarLayout?.let { snackBarLayout ->
                        fragment.showMessage(
                            "Sorry, Please try again later",
                            "",
                            snackBarLayout
                        )
                    }
                }
                if (walletBalance != null) {
                    walletBalance.text = OtherConstants.ERROR
                }
            }
        })
    }

    fun addBalance(
        fragment: FragmentAddMoney
    ) {
        fragment.showLoading()
        (fragment.activity as ActivityMain?)?.binding?.frameLayout?.addView(fragment.dotProgressBar)
        fragment.binding?.parentLayout?.visibility = View.INVISIBLE

        ServiceAddBalance(
            RequestUtil.addBalanceReqBody(
                fragment.binding?.appBarText?.text.toString().substring(2)
            )
        ).call.enqueue(object : Callback<MWalletResponse> {
            override fun onResponse(
                call: Call<MWalletResponse>,
                response: Response<MWalletResponse>
            ) {
                if (ResponseValidation<MWalletResponse>().ifResponseIsNotSuccessful(response)) {
                    fragment.hideLoading()
                    fragment.binding?.parentLayout?.visibility = View.VISIBLE
                    return
                }
                fragment.activity?.onBackPressed()
            }

            override fun onFailure(call: Call<MWalletResponse>, t: Throwable) {
                Log.e("ServiceAddMoney", "onFailure: $t")

                fragment.hideLoading()
                fragment.binding?.parentLayout?.visibility = View.VISIBLE
            }
        })
    }

    /*fun getOtherFoodItems() {
        otherFoodItemsList.clear()
        otherFoodItemsList.add(
            MCategory(
                1,
                "Other 1"
            )
        )
        otherFoodItemsList.add(
            MCategory(
                1,
                "Other 2"
            )
        )
        otherFoodItemsList.add(
            MCategory(
                1,
                "Other 3"
            )
        )
        otherFoodItemsList.add(
            MCategory(
                1,
                "Other 4"
            )
        )
    }*/


    fun getOrders() {
        orderProductListMap.clear()
        ordersList.clear()
        addOrder(
            1,
            "2020-08-15",
            "PROCESSING",
            MAddress(
                "Jane Doe",
                "Apartment or Gated Society",
                "G - 118",
                "Emerald Garden",
                "",
                "Emerald Garden",
                "Kanpur",
                "UP",
                208001
            ),
            "Payment on Delivery",
            "\u20B9 0",
            "Amul Milk 500ml Pack",
            R.drawable.image_placeholder,
            2,
            49
        )
        addOrder(
            1,
            "2020-08-15",
            "PROCESSING",
            MAddress(
                "Jane Doe",
                "Apartment or Gated Society",
                "G - 118",
                "Emerald Garden",
                "",
                "Emerald Garden",
                "Kanpur",
                "UP",
                208001
            ),
            "Payment on Delivery",
            "\u20B9 0",
            "Fresh Paneer 100g Pack",
            R.drawable.image_placeholder,
            4,
            139
        )
        addOrder(
            2,
            "2020-08-25",
            "DELIVERED",
            MAddress(
                "Sara Doe",
                "Individual House",
                "2A/115",
                "Krishna Nagar",
                "Civil Lines",
                "Block - H, Civil Lines",
                "New Delhi",
                "Delhi",
                111001
            ),
            "Payment on Delivery",
            "\u20B9 100, Personal PromoCode",
            "Amul Milk 500ml Pack",
            R.drawable.image_placeholder,
            4,
            49
        )
        addOrder(
            2,
            "2020-08-25",
            "DELIVERED",
            MAddress(
                "Sara Doe",
                "Individual House",
                "2A/115",
                "Krishna Nagar",
                "Civil Lines",
                "Block - H, Civil Lines",
                "New Delhi",
                "Delhi",
                111001
            ),
            "Payment on Delivery",
            "\u20B9 100, Personal PromoCode",
            "Amul Milk 1000ml Pack",
            R.drawable.image_placeholder,
            2,
            99
        )
        addOrder(
            2,
            "2020-08-25",
            "DELIVERED",
            MAddress(
                "Sara Doe",
                "Individual House",
                "2A/115",
                "Krishna Nagar",
                "Civil Lines",
                "Block - H, Civil Lines",
                "New Delhi",
                "Delhi",
                111001
            ),
            "Payment on Delivery",
            "\u20B9 100, Personal PromoCode",
            "Fresh Paneer 100g Pack",
            R.drawable.image_placeholder,
            4,
            139
        )
        addOrder(
            3,
            "2020-08-25",
            "CANCELLED",
            MAddress(
                "Jane Doe",
                "Individual House",
                "B - 95",
                "Indira Nagar",
                "Bus Stand",
                "Sector - 35",
                "Chandigarh",
                "Chandigarh",
                145001
            ),
            "PayU Money",
            "100% off, Special Offer",
            "Amul Milk 500ml Pack",
            R.drawable.image_placeholder,
            3,
            49
        )
        addOrder(
            3,
            "2020-08-25",
            "CANCELLED",
            MAddress(
                "Jane Doe",
                "Individual House",
                "B - 95",
                "Indira Nagar",
                "Bus Stand",
                "Sector - 35",
                "Chandigarh",
                "Chandigarh",
                145001
            ),
            "PayU Money",
            "100% off, Special Offer",
            "Amul Milk 1000ml Pack",
            R.drawable.image_placeholder,
            2,
            99
        )
        addOrder(
            3,
            "2020-08-25",
            "CANCELLED",
            MAddress(
                "Jane Doe",
                "Individual House",
                "B - 95",
                "Indira Nagar",
                "Bus Stand",
                "Sector - 35",
                "Chandigarh",
                "Chandigarh",
                145001
            ),
            "PayU Money",
            "100% off, Special Offer",
            "Fresh Paneer 100g Pack",
            R.drawable.image_placeholder,
            3,
            139
        )
    }

    fun getAddresses() {
        addressesList.clear()
        addressesList.add(
            MAddress(
                "Jane Doe",
                "Apartment or Gated Society",
                "G - 118",
                "Emerald Garden",
                "",
                "Emerald Garden",
                "Kanpur",
                "UP",
                208001
            )
        )
        addressesList.add(
            MAddress(
                "Sara Doe",
                "Individual House",
                "2A/115",
                "Krishna Nagar",
                "Civil Lines",
                "Block - H, Civil Lines",
                "New Delhi",
                "Delhi",
                111001
            )
        )
        addressesList.add(
            MAddress(
                "Jane Doe",
                "Individual House",
                "B - 95",
                "Indira Nagar",
                "Bus Stand",
                "Sector - 35",
                "Chandigarh",
                "Chandigarh",
                145001
            )
        )
        addressesList.add(
            MAddress(
                "Sara Doe",
                "Apartment or Gated Society",
                "P - 90/3",
                "Apex Society",
                "",
                "Sector - 65",
                "Noida",
                "UP",
                220102
            )
        )
    }

    fun getSubscriptions() {
        subscriptions.clear()
        subscriptionsList.clear()
        addSubscription(
            "Amul Gold Milk 500ml Pack",
            R.drawable.image_placeholder,
            4,
            54,
            "05 Aug 2020",
            "15 Aug 2020",
            "8:00 am",
            "COMPLETED"
        )
        addSubscription(
            "Amul Gold Milk 1000ml Pack",
            R.drawable.image_placeholder,
            3,
            94,
            "10 Aug 2020",
            "30 Aug 2020",
            "9:00 am",
            "PROCESSING"
        )
        addSubscription(
            "Fresh Paneer 100g Pack",
            R.drawable.image_placeholder,
            3,
            139,
            "25 Aug 2020",
            "30 Aug 2020",
            "9:15 am",
            "YET TO START"
        )
        addSubscription(
            "Fresh Paneer 200g Pack",
            R.drawable.image_placeholder,
            5,
            189,
            "05 Jul 2020",
            "10 Jul 2020",
            "10:00 am",
            "CANCELLED"
        )
    }

    fun getFaqs() {
        faqs.clear()
        faqList.clear()
        addFaq(
            "Question 1",
            "Answer 1"
        )
        addFaq(
            "Question 2",
            "Answer 2"
        )
        addFaq(
            "Question 3",
            "Answer 3"
        )
        addFaq(
            "Question 4",
            "Answer 4"
        )
    }

    private fun addOrder(
        orderNo: Int,
        orderDate: String,
        orderStatus: String,
        shippingAddress: MAddress?,
        paymentMethod: String?,
        discount: String?,
        productName: String,
        productImage: Int,
        productQuantity: Int,
        productPrice: Int
    ) {

        var productList = orderProductListMap[orderNo]
        if (productList == null) {
            productList = ArrayList()
        }

        val productTotalAmount = productPrice * productQuantity

        productList.add(
            MOrderedProduct(
                productName,
                productImage,
                productQuantity,
                productPrice,
                productTotalAmount
            )
        )

        orderProductListMap[orderNo] = productList

        var orderTotalAmount = 0
        for (i in productList.indices) {
            productList[i].totalAmount?.let {
                orderTotalAmount += it
            }
        }

        var isExisting = false

        for (i in ordersList.indices) {
            if (ordersList[i].no == orderNo) {
                isExisting = true
                ordersList[i].noOfProducts = productList.size
                ordersList[i].totalAmount = orderTotalAmount
                ordersList[i].productList = productList
                break
            }
        }

        if (!isExisting) {
            ordersList.add(
                MOrder(
                    orderNo,
                    orderDate,
                    productList.size,
                    orderTotalAmount,
                    orderStatus,
                    productList,
                    shippingAddress,
                    paymentMethod,
                    discount
                )
            )
        }

    }

    private fun addFaq(
        questionString: String,
        answerString: String
    ): Int {
        val groupPosition: Int

        var faq = faqs[questionString]
        if (faq == null) {
            faq = MFaq(
                questionString
            )

            faqs[questionString] = faq
            faqList.add(faq)

        }

        var answersList = faq.answersList

        if (answersList == null) {
            answersList = ArrayList()
        }

        val answer = MFaqAnswer()
        answer.answer = answerString

        answersList.add(answer)
        faq.answersList = answersList

        groupPosition = faqList.indexOf(faq)

        return groupPosition
    }

    private fun addSubscription(
        productName: String,
        productImage: Int,
        productQuantity: Int,
        productPrice: Int,
        subscriptionStartDate: String,
        subscriptionEndDate: String,
        deliveryTime: String,
        subscriptionStatus: String
    ): Int {

        val groupPosition: Int

        var subscription = subscriptions[productName]

        val productAmount = productPrice * productQuantity


        if (subscription == null) {
            subscription = MSubscription(
                MOrderedProduct(
                    productName,
                    productImage,
                    productQuantity,
                    productPrice,
                    productAmount
                ),
                subscriptionStartDate,
                subscriptionEndDate,
                deliveryTime,
                subscriptionStatus
            )

            subscriptions[productName] = subscription
            subscriptionsList.add(subscription)

        }

        var optionsList = subscription.optionsList

        if (optionsList == null) {
            optionsList = ArrayList()
        }

        val options = MSubscriptionOptions()
        options.id = 1

        optionsList.add(options)
        subscription.optionsList = optionsList

        groupPosition = subscriptionsList.indexOf(subscription)

        return groupPosition

    }

    fun subscribeProduct(request: HashMap<String, Any>) {
        navigator?.showLoading()
        ServiceSubscribeProduct(request).call.enqueue(object : Callback<MLoginResponse> {
            override fun onFailure(call: Call<MLoginResponse>, t: Throwable) {
                navigator?.showMessage(t.localizedMessage, 0)
                navigator?.hideLoading()
            }

            override fun onResponse(
                call: Call<MLoginResponse>,
                response: Response<MLoginResponse>
            ) {
                if (response.isSuccessful && response.body()?.errorCode == 200) {
                    navigator?.onAction(null,200)
                }
                navigator?.hideLoading()
                navigator?.showMessage(response.body()?.errorMessage, 0)
            }
        })
    }


    var searchKey = MutableLiveData<String>()
    fun searchProduct(request: HashMap<String, Any>) {
        navigator?.showLoading()
        ServiceSubscribeProduct(request).call.enqueue(object : Callback<MLoginResponse> {
            override fun onFailure(call: Call<MLoginResponse>, t: Throwable) {
                navigator?.showMessage(t.localizedMessage, 0)
                navigator?.hideLoading()
            }

            override fun onResponse(
                call: Call<MLoginResponse>,
                response: Response<MLoginResponse>
            ) {
                if (response.isSuccessful && response.body()?.errorCode == 200) {
                    navigator?.onAction(null,200)
                }
                navigator?.hideLoading()
                navigator?.showMessage(response.body()?.errorMessage, 0)
            }
        })
    }

}