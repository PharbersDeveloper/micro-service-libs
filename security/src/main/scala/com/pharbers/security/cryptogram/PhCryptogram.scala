package com.pharbers.security.cryptogram

trait PhCryptogram {
    /**
      * 加密
      * @param cleartext 明文
      */
    def encrypt(cleartext: String): String

    /**
      * 解密
      * @param cipher 密文
      */
    def decrypt(cipher: String): String
}
