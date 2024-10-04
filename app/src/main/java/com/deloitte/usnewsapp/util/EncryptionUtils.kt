package com.deloitte.usnewsapp.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import android.util.Log

object EncryptionUtils {

    private const val KEY_ALIAS = "my_key_alias"

    init {
        generateAndStoreAESKey(KEY_ALIAS)
    }

    private fun generateAndStoreAESKey(alias: String) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun encryptPassword(password: String): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryption = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
        val encryptedPassword = iv + encryption
        val encodedPassword = Base64.encodeToString(encryptedPassword, Base64.DEFAULT)
        return encodedPassword
    }

    fun decryptPassword(encryptedPassword: String): String {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
            val iv = encryptedBytes.copyOfRange(0, 12)
            val encryption = encryptedBytes.copyOfRange(12, encryptedBytes.size)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedPassword = cipher.doFinal(encryption)
            val decodedPassword = String(decryptedPassword, Charsets.UTF_8)
            return decodedPassword
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
}
