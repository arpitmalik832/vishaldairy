package com.vishaldairy.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.vishaldairy.R
import com.vishaldairy.model.MProduct
import com.vishaldairy.model.MSubCategory
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.fragment.FragmentProducts
import com.vishaldairy.utils.AppMethods
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.viewModel.VMHome
import java.util.ArrayList

class ProductsAdapter(
    private val context: Context,
    var viewModel: VMHome,
    var fragment: FragmentProducts,
    private var listener: OnClick<MProduct>,
    private var data: ArrayList<MSubCategory>
) : BaseExpandableListAdapter() {

    fun setDataList(data: ArrayList<MSubCategory>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): MProduct? {
        val productList = data[groupPosition].productList
        return productList?.get(childPosition)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val productList = data[groupPosition].productList
        return productList?.size?:0
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              contentView: View?, parent: ViewGroup
    ): View? {
        var view = contentView
        val product = getChild(groupPosition, childPosition) as MProduct

        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_product, parent,false)
        }

        val productName: AppCompatTextView? = view?.findViewById(R.id.product_name)
        val productImage: AppCompatImageView? = view?.findViewById(R.id.product_image)
        val productPrice: AppCompatTextView? = view?.findViewById(R.id.product_price)
        val productSalePrice: AppCompatTextView? = view?.findViewById(R.id.product_sale_price)
        val productStatus: AppCompatTextView? = view?.findViewById(R.id.product_status)
        val addBtnText: AppCompatTextView? = view?.findViewById(R.id.add_button_text)

        val addBtn: ConstraintLayout? = view?.findViewById(R.id.product_add_button_layout)
        val addBtnFirstImage: AppCompatImageView? = view?.findViewById(R.id.add_button_first_image)
        val addBtnSecondImage: AppCompatImageView? = view?.findViewById(R.id.add_button_second_image)
        val subscribeBtn: AppCompatTextView?  = view?.findViewById(R.id.subscribe_button_text)

        productName?.text = product.name?.trim()
        AppMethods.loadImage(
            context,
            product.imageSrc,
            productImage,
            R.drawable.image_placeholder
        )

        product.on_sale?.let {
            if(it) {
                productSalePrice?.text = product.sale_price?.let { it1 ->
                    AppMethods.formatPriceText(
                        it1
                    )
                }
            } else {
                productSalePrice?.text = ""
            }
        }
        if(productSalePrice?.text != "" && productSalePrice?.text != null) {
            val productPriceText = SpannableString(product.regular_price?.let {
                AppMethods.formatPriceText(
                    it
                )
            })
            productPriceText.setSpan(
                StrikethroughSpan(),
                0,
                productPriceText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            productPrice?.text = productPriceText
        } else {
            val productPriceText = product.price?.let { AppMethods.formatPriceText(it) }
            productPrice?.text = productPriceText
        }

        productStatus?.text = product.stock_status
        when(product.stock_status) {
            OtherConstants.PRODUCT_OUT_OF_STOCK -> {
                productStatus?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red3
                    )
                )

                addBtn?.isEnabled = false
                addBtn?.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.background_white_fill_with_d4d4d4_stroke_2dp_with_corners_20dp
                )
                addBtnText?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.grey
                    )
                )
                addBtnFirstImage?.setImageResource(R.drawable.icon_add_inactive)

                subscribeBtn?.isEnabled = false
                subscribeBtn?.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.background_d4d4d4_color_fill_with_corners_20dp
                )
            }
            OtherConstants.PRODUCT_IN_STOCK -> {
                productStatus?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )

                addBtn?.isEnabled = true
                addBtn?.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.background_white_fill_with_color_primary_stroke_2dp_with_corners_20dp
                )
                addBtnText?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                addBtnFirstImage?.setImageResource(R.drawable.icon_add_color_primary)

                subscribeBtn?.isEnabled = true
                subscribeBtn?.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.background_color_primary_fill_with_corners_20dp
                )
            }
            else -> {

            }
        }

        addBtn?.setOnClickListener {
            if(addBtnText?.text == OtherConstants.ADD_BTN_TEXT) {
                addBtn.isEnabled = false
                addBtnText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.grey
                    )
                )
                addBtnFirstImage?.let { addBtnFirstImage ->
                    addBtnSecondImage?.let { addBtnSecondImage ->
                        viewModel.addItemToCart(
                            product,
                            fragment,
                            addBtn,
                            addBtnText,
                            addBtnFirstImage,
                            addBtnSecondImage
                        )
                    }
                }
            }
        }

        addBtnFirstImage?.setOnClickListener {
            if(addBtnText?.text.toString() == "1") {
                addBtnFirstImage.isEnabled = false
                addBtnFirstImage.setImageResource(R.drawable.icon_remove_inactive)
                addBtnText?.let { addBtnText ->
                    addBtnSecondImage?.let { addBtnSecondImage ->
                        viewModel.deleteItemFromCart(
                            fragment,
                            OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE,
                            addBtnFirstImage = addBtnFirstImage,
                            addBtnSecondImage = addBtnSecondImage,
                            addBtnText = addBtnText,
                            product = product
                        )
                    }
                }
            } else if(addBtnText?.text.toString() != OtherConstants.ADD_BTN_TEXT) {
                addBtnFirstImage.isEnabled = false
                addBtnFirstImage.setImageResource(R.drawable.icon_remove_inactive)
                var quantity = addBtnText?.text.toString().toInt()
                if(quantity > 1) {
                    quantity--
                    addBtnText?.let {addBtnText ->
                        addBtnSecondImage?.let {addBtnSecondImage ->
                            viewModel.updateQuantityOfItemInCart(
                                quantity,
                                fragment,
                                OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE,
                                addBtnFirstImage = addBtnFirstImage,
                                addBtnSecondImage = addBtnSecondImage,
                                addBtnText = addBtnText,
                                product = product
                            )
                        }
                    }
                } else {
                    addBtnText?.let { addBtnText ->
                        addBtnSecondImage?.let { addBtnSecondImage ->
                            viewModel.deleteItemFromCart(
                                fragment,
                                OtherConstants.PRODUCT_ADD_BTN_FIRST_IMAGE,
                                addBtnFirstImage,
                                addBtnSecondImage,
                                addBtnText,
                                product
                            )
                        }
                    }
                }
            }
        }

        addBtnSecondImage?.setOnClickListener {
            if(addBtnSecondImage.visibility == View.VISIBLE) {
                addBtnSecondImage.isEnabled  = false
                addBtnSecondImage.setImageResource(R.drawable.icon_add_inactive)
                var quantity = addBtnText?.text.toString().toInt()
                quantity++
                addBtnText?.let { addBtnText ->
                    addBtnFirstImage?.let { addBtnFirstImage ->
                        viewModel.updateQuantityOfItemInCart(
                            quantity,
                            fragment,
                            OtherConstants.PRODUCT_ADD_BTN_SECOND_IMAGE,
                            addBtnFirstImage = addBtnFirstImage,
                            addBtnSecondImage = addBtnSecondImage,
                            addBtnText = addBtnText,
                            product = product
                        )
                    }
                }
            }
        }

        subscribeBtn?.setOnClickListener {
            listener.onClick(
                product,
                childPosition,
                it
            )
        }

        return view
    }

    override fun getGroup(groupPosition: Int): Any {
        return data[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return data.size
    }

    override fun getGroupView(groupPosition: Int, isLastChild: Boolean, contentView: View?,
                              parent: ViewGroup
    ): View? {
        var view = contentView
        val subCategory = getGroup(groupPosition) as MSubCategory
        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_products_category, parent,false)
        }

        val name: AppCompatTextView? = view?.findViewById(R.id.sub_category_name)
        val noOfProducts: AppCompatTextView? = view?.findViewById(R.id.no_of_products)

        name?.text = subCategory.name?.trim()
        val noOfProductsText = subCategory.count?.let { AppMethods.formatNoOfProducts(it) }
        noOfProducts?.text = noOfProductsText

        return view
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}