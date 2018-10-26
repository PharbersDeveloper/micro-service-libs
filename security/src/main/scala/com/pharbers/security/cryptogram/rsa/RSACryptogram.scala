package com.pharbers.security.cryptogram.rsa

import org.apache.commons.codec.binary.Base64
import com.pharbers.security.cryptogram.PhCryptogram
import java.security.{KeyPairGenerator, SecureRandom}

trait RSACryptogram extends PhCryptogram {
    val puk: String
    val prk: String
    val ALGORITHM_RSA: String
    val TRANSFORMS_RSA: String
    val CHARSET_NAME_UTF_8: String
    val KEY_SIZE: Int

    def createKey(): (String, String) = {
        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA)
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom())
        val keyPair = keyPairGenerator.generateKeyPair()

        val publicKey = Base64.encodeBase64String(keyPair.getPublic.getEncoded)
        val privateKey = Base64.encodeBase64String(keyPair.getPrivate.getEncoded)

        (publicKey, privateKey)
    }
}
