package com.pharbers.security.cryptogram.des

import java.net.URLDecoder
import java.security.SecureRandom
import javax.crypto.spec.DESKeySpec
import org.apache.commons.codec.binary.Base64
import javax.crypto.{Cipher, SecretKeyFactory}

trait DESDecryptTrait { this: DESCryptogram =>
    def decrypt(ciphertext: String): String = {
        val originKey = Base64.decodeBase64(password)
        if(password.isEmpty) throw new Exception("password is empty")
        if(originKey.length % 8 != 0) throw new Exception("password length is wrong")

        val keySpec = new DESKeySpec(originKey)
        val securekey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(keySpec)

        val inputBytes = Base64.decodeBase64(ciphertext)

        val cipher = Cipher.getInstance(ALGORITHM_DES)
        cipher.init(Cipher.DECRYPT_MODE, securekey, new SecureRandom())

        val cache = cipher.doFinal(inputBytes)
        URLDecoder.decode(new String(cache, CHARSET_NAME_UTF_8), CHARSET_NAME_UTF_8)
    }
}
