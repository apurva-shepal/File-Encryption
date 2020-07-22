package com.example.crypto.aes

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.*
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AesEncrypter(var key: SecretKey) {

    var paramSpec: AlgorithmParameterSpec

    fun encryptFile(path: String?, encrypted: Uri): String? {
        val inFile = File(path!!)

        val outFile = File(encrypted.path!!)
        try {
            val keyData = key.encoded
            val key2 =
                SecretKeySpec(keyData, 0, keyData.size, ALGO_SECRET_KEY_GENERATOR) as SecretKey
            val sKey = Base64.encodeToString(key2.encoded, Base64.DEFAULT)
            encrypt(
                key,
                paramSpec,
                FileInputStream(inFile),
                FileOutputStream(outFile)
            )
            return sKey
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun decryptFile(
        uri: Uri,
        uriOut: String?,
        secretKey: String?
    ): Boolean {
        val inFile = File(uri.path!!)
        val outFile = File(uriOut!!)

        val decodedKey = Base64.decode(secretKey!!, Base64.DEFAULT)
        val key2: SecretKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
        return try {
            decrypt(
                key2,
                paramSpec,
                FileInputStream(inFile),
                FileOutputStream(outFile)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val IV_LENGTH = 16
        private const val ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG"
        const val ALGO_SECRET_KEY_GENERATOR = "AES"
        private const val DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024
        private const val ALGO_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding"
        lateinit var context: Context


        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class,
            IOException::class
        )
        fun encrypt(
            key: SecretKey?,
            paramSpec: AlgorithmParameterSpec?,
            inputStream: InputStream,
            out: OutputStream
        ) {
            var outputStream = out
            try {
                val c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR)
                c.init(Cipher.ENCRYPT_MODE, key, paramSpec)
                outputStream = CipherOutputStream(outputStream, c)
                var count = 0
                val buffer = ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)
                while (inputStream.read(buffer).also { count = it } >= 0) {
                    outputStream.write(buffer, 0, count)
                }
            } finally {
                outputStream.close()
            }
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class,
            IOException::class
        )
        fun decrypt(
            key: SecretKey?,
            paramSpec: AlgorithmParameterSpec?,
            inputStream: InputStream,
            out: OutputStream
        ) {
            var outputStream = out
            try {
                val c =
                    Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR)
                c.init(Cipher.DECRYPT_MODE, key, paramSpec)
                outputStream = CipherOutputStream(outputStream, c)
                var count = 0
                val buffer =
                    ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)
                while (inputStream.read(buffer).also { count = it } >= 0) {
                    outputStream.write(buffer, 0, count)
                }
            } finally {
                outputStream.close()
            }
        }
    }

    init {
        val iv = ByteArray(IV_LENGTH)
        SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR)
            .nextBytes(iv)
        paramSpec = IvParameterSpec(iv)
        key =  KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey()
    }

}