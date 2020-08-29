package com.vishaldairy.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object PayU {
    val merchantId = "123456"
    val merchantKey = "SXb8d0Fx"
    val salt = "sBBmboPMqg"
    val successUrl = "https://www.payumoney.com/mobileapp/payumoney/success.php"
    val failedUrl = "https://www.payumoney.com/mobileapp/payumoney/failure.php"

//    fun hashCal(type: String?, hashString: String): String? {
//        val hash = StringBuilder()
//        var messageDigest: MessageDigest? = null
//        try {
//            messageDigest = MessageDigest.getInstance(type)
//            messageDigest.update(hashString.toByteArray())
//            val mdbytes: ByteArray = messageDigest.digest()
//            for (hashByte in mdbytes) {
//                hash.append(
//                    ((hashByte and 0xff.toByte()) + 0x100).toString(16).substring(1)
//                )
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        }
//        return hash.toString()
//    }
//

    fun hashCal(type: String?, str: String): String? {
        val hashseq = str.toByteArray()
        val hexString = StringBuffer()
        try {
            val algorithm =
                MessageDigest.getInstance(type)
            algorithm.reset()
            algorithm.update(hashseq)
            val messageDigest = algorithm.digest()
            for (i in messageDigest.indices) {
                val hex =
                    Integer.toHexString(0xFF and messageDigest[i].toInt())
                if (hex.length == 1) {
                    hexString.append("0")
                }
                hexString.append(hex)
            }
        } catch (nsae: NoSuchAlgorithmException) {
        }
        return hexString.toString()
    }

}