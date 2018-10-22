package com.pharbers.security.cryptogram.rsa

case class RSA(override val puk: RSAPublicKey = RSAPublicKey(),
               override val prk: RSAPrivateKey = RSAPrivateKey())
        extends RSACryptogram with RSAEncryptTrait with RSADecryptTrait