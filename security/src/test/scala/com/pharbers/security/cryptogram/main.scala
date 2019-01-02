package com.pharbers.security.cryptogram

import com.pharbers.security.cryptogram.des.DES
import com.pharbers.security.cryptogram.rsa.RSA

object main extends App {
    val (puk, prk) = RSA().createKey()
    println(puk)
    println(prk)

//    val pu = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK9L5z4lVKzUdffDqndr78I+ovAZ9W/AOTf9AYqOWgw1ZzJuHSHZL8iCtkfSR9KMsLC/wxsNHigUFyKsaTWFIi8CAwEAAQ=="
//    val pr = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAr0vnPiVUrNR198Oqd2vvwj6i8Bn1b8A5N/0Bio5aDDVnMm4dIdkvyIK2R9JH0oywsL/DGw0eKBQXIqxpNYUiLwIDAQABAkAex1ID3GQgsHFCHo3ox//h+EN9quEoTPT++qJxpIr1B4T6DAUOzuI68/eZy5MGpUvi4vhtmYn9mrbVeqZnrcDBAiEA2hUHPCoDjSibsp1o5IMhgIXlnKsz7+9UgC+FW6vDgOECIQDNxnWaq53Q27LmFP6mM/sHDN2uMx0nvphKhqQ9vaI1DwIhANIHgIFMAUGYg2LhQJ0bQU+zJLDfHVUNzPbrTWc9JDthAiAHlSuaQn6zRpVGEzn7B+lVLi0xESMe5tAX1vRQbh9/EwIgf2TdQUtdoCDGHQA6NrEZ8jqMX4opbwa8WqqNewuBXPw="
//    val rsa = RSA(pu, pr)

    val source = "adscd\n中文乱码,我爱你中国1\n中文乱码,我爱你中国2\n中文乱码,我爱你中国3\n中文乱码,我爱你中国4\n中文乱码,我爱你中国5\n中文乱码,我爱你中国6\n中文乱码,我爱你中国7\n中文乱码,我爱你中国8\n中文乱码,我爱你中国\n中文乱码,我爱你中国9\n中文乱码,我爱你中国10\n中文乱码,我爱你中国11\n2222-ok-ok-ok"
    val rsa = RSA(puk, prk)
    val encrypted = rsa.encrypt(source)
    println(encrypted)

//        val encrypting = "aJeuEKZfXcWefE2OubMpBKAXjNYaoR9/lS6WGcMe2241eBxyF5PNyeaxR2cAe2v7GqJPkFoTVJjZ6yK1vkeJayIWD+5DnLyz8ElglGqr/gTr+syGaS/VW54vCzDYdOqGr5dPtZICbleb/WDIH5eJfDtCj9gKgENJ8sZUQ3L6j1Am1ioBplbgCGJY77R6Q3wdU/BZ+R5rSULcFA6XgmkmjrU7MGMjYp5SbpBFSxBJmjlo22WSWRAxoOPUJnZqJPfUmU5lh9WUwZ+9v0+fOM6Fi9WtAUdyYi5kIKYjxAmElCmbF886a1EoTGC3wMvITQNcE5pPfrUd2RbjtbuQIbT8NFPN+3CByKUO65pQrOhAypbWXlIlIDYUK+geWx2a8IFRbFJJbinWSjKWwDZnoO8EtMMJ9/c3YiHbSWrZybuI4Cx8E9B/5QdOH1NIeZpbdXuWVvOpoGkahy1BNqAjEECpDcE1zkqSoDiBAgO7LloBAk1yranOY/5DmvD/eVf4zrXnGoexck9ekABLLBj8C5X6WAEg+hslt2n7n9v5lipK6bVfV4qwkFHTggTvexFZInrbGXGvBcg6t/WsUoV6aT8Bzg=="
//        val decrypted = rsa.decrypt(encrypting)
    val decrypted = rsa.decrypt(encrypted)
    println(decrypted)
}
