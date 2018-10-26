package com.pharbers.security.cryptogram.des

import javax.crypto.KeyGenerator
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64
import com.pharbers.security.cryptogram.PhCryptogram

trait DESCryptogram extends PhCryptogram {
    val password: String
    val ALGORITHM_DES: String
    val ALGORITHM_SHA1PRNG: String
    val ALGORITHM_AES: String
    val CHARSET_NAME_UTF_8: String

    def createKey(keySeed: String = "abcd1234!@#$"): String = {
        val secureRandom = SecureRandom.getInstance(ALGORITHM_SHA1PRNG)
        secureRandom.setSeed(keySeed.getBytes())

        val generator = KeyGenerator.getInstance(ALGORITHM_AES)
        generator.init(secureRandom)

        Base64.encodeBase64String(generator.generateKey().getEncoded)
    }
}
