package com.pharbers.security.cryptogram.rsa

import javax.crypto.Cipher
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import org.apache.commons.codec.binary.Base64

trait RSAEncryptTrait { this: RSACryptogram =>
    def encrypt(cleartext: String): String = {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")

        val publicKeySpec = new RSAPublicKeySpec(
            BigInt(1, Base64.decodeBase64(puk.base64Modulus)).bigInteger,
            BigInt(1, Base64.decodeBase64(puk.base64Exp)).bigInteger
        )

        val publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val MAX_ENCRYPT_BLOCK = (BigInt(1, Base64.decodeBase64(prk.base64Modulus)).bigInteger.bitLength() >> 3) - 11
        val inputBytes = cleartext.getBytes("UTF-8")
        val inputLength = cleartext.length

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
