package com.pharbers.security.cryptogram.md5

import com.pharbers.security.cryptogram.PhCryptogram

case class md5() extends PhCryptogram {
    /**
      * 加密
      *
      * @param cleartext 明文
      */
    override def encrypt(cleartext: String): String = {
        java.security.MessageDigest.getInstance("MD5")
                .digest(cleartext.getBytes())
                .map(0xFF & _)
                .map("%02x".format(_))
                .foldLeft(""){_ + _}
    }

    /**
      * 解密
      *
      * @param cipher 密文
      */
    override def decrypt(cipher: String): String = ???
}
