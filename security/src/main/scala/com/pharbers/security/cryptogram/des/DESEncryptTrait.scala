package com.pharbers.security.cryptogram.des

import java.net.URLEncoder
import java.security.SecureRandom

import javax.crypto.spec.DESKeySpec
import javax.crypto.{Cipher, SecretKeyFactory}
import org.apache.commons.codec.binary.Base64

trait DESEncryptTrait { this: DESCryptogram =>
    def encrypt(cleartext: String): String = {
        val originKey = Base64.decodeBase64(password)
        if(password.isEmpty) throw new Exception("password is empty")
        if(originKey.length % 8 != 0) throw new Exception("password length is wrong")

        val keySpec = new DESKeySpec(originKey)
        val securekey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(keySpec)

        val inputBytes = URLEncoder.encode(cleartext, CHARSET_NAME_UTF_8).getBytes(CHARSET_NAME_UTF_8)

        val cipher = Cipher.getInstance(ALGORITHM_DES)
        cipher.init(Cipher.ENCRYPT_MODE, securekey, new SecureRandom())

        val result = cipher.doFinal(inputBytes)

        Base64.encodeBase64String(result)
    }
}
