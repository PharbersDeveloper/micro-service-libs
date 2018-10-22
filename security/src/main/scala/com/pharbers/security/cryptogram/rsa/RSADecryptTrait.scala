package com.pharbers.security.cryptogram.rsa

import javax.crypto.Cipher
import java.security.KeyFactory
import java.security.spec.RSAPrivateKeySpec
import org.apache.commons.codec.binary.Base64

trait RSADecryptTrait { this: RSACryptogram =>
    def decrypt(ciphertext: String): String = {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")

        val privateKeySpec = new RSAPrivateKeySpec(
            BigInt(1, Base64.decodeBase64(prk.base64Modulus)).bigInteger,
            BigInt(1, Base64.decodeBase64(prk.base64Exp)).bigInteger
        )

        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val MAX_DECRYPT_BLOCK = BigInt(1, Base64.decodeBase64(prk.base64Modulus)).bigInteger.bitLength() >> 3
        val inputBytes = Base64.decodeBase64(ciphertext)
        val inputLength = inputBytes.length

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

        new String(cache, "utf-8")
    }
}
