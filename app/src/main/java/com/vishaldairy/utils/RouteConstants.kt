package com.vishaldairy.utils

object RouteConstants {
    const val URL_V3_CATEGORIES = "wp-json/wc/v3/products/categories"
    const val URL_BLOCKS_CATEGORIES = "wp-json/wc/blocks/products/categories"

    const val URL_V3_PRODUCTS = "wp-json/wc/v3/products"
    const val URL_BLOCKS_PRODUCTS = "wp-json/wc/blocks/products"

    const val URL_STORE_CART = "wp-json/wc/store/cart"
    const val URL_SEARCH = "wp-json/search/{key}"

    const val URL_ADD_ITEM_TO_CART = "wp-json/wc/store/cart/items"
    const val URL_UPDATE_QUANTITY_OF_ITEM_IN_CART = "wp-json/wc/store/cart/items/{key}"
    const val URL_DELETE_ITEM_FROM_CART = "wp-json/wc/store/cart/items/{key}"

    const val URL_FETCH_WALLET_BALANCE = "api/check_wallet.php"
    const val URL_ADD_MONEY_TO_WALLET = "api/wallet.php"
    const val URL_REMOVE_MONEY_FROM_WALLET = "api/daily_subscription.php"
    const val URL_FETCH_PAYMENT_METHODS = "wp-json/wc/v3/payment_gateways"

    const val URL_LOGIN = "api/login.php"
    const val URL_REGISTER = "api/register.php"
    const val URL_OTP = "api/verify_otp.php"
    const val URL_SUBSCRIBE_PRODUCT = "api/subscribe_product.php"
}