package com.pharbers.security.cryptogram.rsa

import java.net.URLDecoder
import javax.crypto.Cipher
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import org.apache.commons.codec.binary.Base64

trait RSADecryptTrait { this: RSACryptogram =>
    def decrypt(ciphertext: String): String = {

        if(prk.isEmpty) throw new Exception("private key is empty")

        val originKey = Base64.decodeBase64(prk)
        val keySpec = new PKCS8EncodedKeySpec(originKey)
        val privateKey = KeyFactory.getInstance(ALGORITHM_RSA).generatePrivate(keySpec)

        val cipher = Cipher.getInstance(TRANSFORMS_RSA)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val inputBytes = Base64.decodeBase64(ciphertext)
        val inputLength = inputBytes.length

        val MAX_DECRYPT_BLOCK = KEY_SIZE >> 3
        var offset = 0
        var cache: Array[Byte] = Array()

        while (inputLength - offset > 0) {
            val tmp = if (inputLength - offset > MAX_DECRYPT_BLOCK)
                cipher.doFinal(inputBytes, offset, MAX_DECRYPT_BLOCK)
            else
                cipher.doFinal(inputBytes, offset, inputLength - offset)

            cache ++= tmp
            offset += MAX_DECRYPT_BLOCK
        }

        URLDecoder.decode(new String(cache, CHARSET_NAME_UTF_8), CHARSET_NAME_UTF_8)
    }
}
