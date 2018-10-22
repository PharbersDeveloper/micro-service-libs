package com.pharbers.security.cryptogram

import org.scalatest.FunSuite
import com.pharbers.security.cryptogram.rsa.RSA

class RSASpec extends FunSuite {
    test("RSA test") {
        val (puk, prk) = RSA ().createKey (2048)
        println (puk)
        println (prk)
//    val a = "AL6jaxW5nh5MEYX1FzUJzar9HK7NqkcQJpkIKFz3Qq392M3hZyv7PiQcDaKZc2QaHXDwJUIbWeSCEivMKBrAxNs="
//    val b = "AQAB"
//    val c = "AJzINSdW0m1ETh0HJiDegSCgrVgtmF59L+EnTlq2l0YTUuByGXm2orvkxoWoVa+iiUGmXqH+WLaGnFLsA3cjFiE="

        val rsa = RSA (puk = puk, prk = prk)

        val eed = rsa.encrypt ("alsdkjfaslkdjflakj2o325754356lkjdaslkfjflakj20938000asdfadg42345e54ysrfsasdghdf40000")
        println (eed)

        val red = rsa.decrypt (eed)
        println (red)
    }
}