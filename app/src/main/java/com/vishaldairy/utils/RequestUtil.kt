package com.vishaldairy.utils

object RequestUtil {

    fun getPerPage(): Int {
        return 100
    }

    fun getCategoryId(
        categoryId: Int
    ):Int {
        return categoryId
    }

    fun getPage(
        pageNo: Int
    ): Int {
        return pageNo
    }

    fun getAddToCartBody(
        productId: Int
    ): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["id"] = productId
        request["quantity"] = 1
        return request
    }

    fun updateQuantityOfItemInStock(
        quantity: Int
    ): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["quantity"] = quantity
        return request
    }

    fun fetchBalanceReqBody(): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["user_id"] = Action.getUserId()
        return request
    }

    fun addBalanceReqBody(
        money: String
    ): HashMap<String, Any> {
        val request = HashMap<String, Any>()
        request["user_id"] = Action.getUserId()
        request["amount"] = money
        return request
    }

}