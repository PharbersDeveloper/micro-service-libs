package com.pharbers.security.cryptogram.rsa

import java.security.spec.{RSAPrivateKeySpec, RSAPublicKeySpec}
import java.security.{KeyFactory, KeyPairGenerator}
import com.pharbers.security.cryptogram.Cryptogram
import org.apache.commons.codec.binary.Base64

case class RSAPublicKey(base64Modulus: String = "", base64Exp: String = "")

case class RSAPrivateKey(base64Modulus: String = "", base64Exp: String = "")

trait RSACryptogram extends Cryptogram {
    val puk: RSAPublicKey
    val prk: RSAPrivateKey

    def createKey(KEY_Len: Int): (RSAPublicKey, RSAPrivateKey) = {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(KEY_Len)

        val factory = KeyFactory.getInstance("RSA")
        val keyPair = keyPairGenerator.generateKeyPair()

        val publicKey = keyPair.getPublic
        val privateKey = keyPair.getPrivate

        val publicKeySpec = factory.getKeySpec(publicKey, classOf[RSAPublicKeySpec])
        val privateKeySpec = factory.getKeySpec(privateKey, classOf[RSAPrivateKeySpec])

        (RSAPublicKey(
            Base64.encodeBase64String(publicKeySpec.getModulus.toByteArray),
            Base64.encodeBase64String(publicKeySpec.getPublicExponent.toByteArray)
        ), RSAPrivateKey(
            Base64.encodeBase64String(privateKeySpec.getModulus.toByteArray),
            Base64.encodeBase64String(privateKeySpec.getPrivateExponent.toByteArray)
        ))
    }
}
