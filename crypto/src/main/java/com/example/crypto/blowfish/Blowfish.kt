package com.example.crypto.blowfish

import android.util.Log
import java.io.*
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*

class Blowfish {
    var keyGenerator: KeyGenerator? = null
    var secretKey: SecretKey? = null
    var cipher: Cipher? = null

     fun encrypt(srcPath: String, destPath: String) {
        val rawFile = File(srcPath)
        val encryptedFile = File(destPath)
        var inStream: InputStream? = null
        var outStream: OutputStream? = null
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, secretKey)

            inStream = FileInputStream(rawFile)
            outStream = FileOutputStream(encryptedFile)
            val buffer = ByteArray(1024)
            var len: Int
            while (inStream.read(buffer).also { len = it } > 0) {
                outStream.write(cipher!!.update(buffer, 0, len))
                outStream.flush()
            }
            outStream.write(cipher!!.doFinal())
            inStream.close()
            outStream.close()

        } catch (ex: IllegalBlockSizeException) {
            Log.e("Exception", ex.message!!)
        } catch (ex: BadPaddingException) {
            Log.e("Exception", ex.message!!)
        } catch (ex: InvalidKeyException) {
            Log.e("Exception", ex.message!!)
        } catch (ex: FileNotFoundException) {
            Log.e("Exception", ex.message!!)
        } catch (ex: IOException) {
            Log.e("Exception", ex.message!!)
        }
    }

     fun decrypt(srcPath: String, destPath: String): Boolean {
        val encryptedFile = File(srcPath)
        val decryptedFile = File(destPath)
        var inStream: InputStream? = null
        var outStream: OutputStream? = null
        try {
            cipher!!.init(Cipher.DECRYPT_MODE, secretKey)

            inStream = FileInputStream(encryptedFile)
            outStream = FileOutputStream(decryptedFile)
            val buffer = ByteArray(1024)
            var len: Int
            while (inStream.read(buffer).also { len = it } > 0) {
                outStream.write(cipher!!.update(buffer, 0, len))
                outStream.flush()
            }
            outStream.write(cipher!!.doFinal())
            inStream.close()
            outStream.close()
            return true
        } catch (ex: IllegalBlockSizeException) {
            Log.e("Exception", ex.message!!)
            return false
        } catch (ex: BadPaddingException) {
            Log.e("Exception", ex.message!!)
            return false
        } catch (ex: InvalidKeyException) {
            Log.e("Exception", ex.message!!)
            return false
        } catch (ex: FileNotFoundException) {
            Log.e("Exception", ex.message!!)
            return false
        } catch (ex: IOException) {
            Log.e("Exception", ex.message!!)
            return false
        }
    }


    init {
        try {
            keyGenerator = KeyGenerator.getInstance("Blowfish")
            secretKey = keyGenerator!!.generateKey()
            cipher = Cipher.getInstance("Blowfish")
        } catch (ex: NoSuchPaddingException) {
            Log.e("Exception", ex.message!!)
        } catch (ex: NoSuchAlgorithmException) {
            Log.e("Exception", ex.message!!)
        }
    }
}