package com.pharbers.security.cryptogram.rsa

case class RSA(override val puk: String = "", override val prk: String = "")
        extends RSACryptogram with RSAEncryptTrait with RSADecryptTrait {
    val ALGORITHM_RSA = "RSA"
    val TRANSFORMS_RSA = "RSA/ECB/PKCS1PADDING"
    val CHARSET_NAME_UTF_8 = "UTF-8"
    val KEY_SIZE = 512
}