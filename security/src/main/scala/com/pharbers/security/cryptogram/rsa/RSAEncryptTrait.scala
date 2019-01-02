package com.pharbers.security.cryptogram.rsa

import java.net.URLEncoder

import javax.crypto.Cipher
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

import org.apache.commons.codec.binary.Base64

trait RSAEncryptTrait { this: RSACryptogram =>
    def encrypt(cleartext: String): String = {

        if(puk.isEmpty) throw new Exception("public key is empty")

        val originKey = Base64.decodeBase64(puk)
        val keySpec = new X509EncodedKeySpec(originKey)
        val publicKey = KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(keySpec)

        val cipher = Cipher.getInstance(TRANSFORMS_RSA)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val inputBytes = URLEncoder.encode(cleartext, CHARSET_NAME_UTF_8).getBytes(CHARSET_NAME_UTF_8)
        val inputLength = inputBytes.length

        val MAX_ENCRYPT_BLOCK = (KEY_SIZE >> 3) - 11
        var offset = 0
        var cache: Array[Byte] = Array()

        while (inputLength - offset > 0) {
            val tmp = if (inputLength - offset > MAX_ENCRYPT_BLOCK)
                cipher.doFinal(inputBytes, offset, MAX_ENCRYPT_BLOCK)
            else
                cipher.doFinal(inputBytes, offset, inputLength - offset)

            cache ++= tmp
            offset += MAX_ENCRYPT_BLOCK
        }

        Base64.encodeBase64String(cache)
    }
}
