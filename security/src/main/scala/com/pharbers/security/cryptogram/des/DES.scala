package com.pharbers.security.cryptogram.des

case class DES(override val password: String = "") extends DESCryptogram with DESEncryptTrait with DESDecryptTrait {
    val ALGORITHM_DES = "DES"
    val ALGORITHM_SHA1PRNG = "SHA1PRNG"
    val ALGORITHM_AES = "AES"
    val CHARSET_NAME_UTF_8 = "UTF-8"
}
