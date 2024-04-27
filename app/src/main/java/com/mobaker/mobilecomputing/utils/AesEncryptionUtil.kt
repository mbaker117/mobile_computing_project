package com.mobaker.mobilecomputing.utils

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object AesEncryptionUtil {

    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray
    private const val secret = "jackie"

    private fun AesEncryption() {}

    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(StandardCharsets.UTF_8)
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = key.copyOf(16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val bytes = cipher.doFinal(strToEncrypt.toByteArray())
            Log.d("encryption", Base64.encodeToString(bytes, Base64.DEFAULT))
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

}