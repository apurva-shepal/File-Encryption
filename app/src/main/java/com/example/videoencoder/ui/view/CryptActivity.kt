package com.example.videoencoder.ui.view

import android.Manifest
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crypto.aes.AesEncrypter
import com.example.crypto.blowfish.Blowfish
import com.example.videoencoder.R
import com.example.videoencoder.constants.ApplicationConstants
import com.example.videoencoder.ui.`interface`.IOnFileSelected
import com.example.videoencoder.ui.adapter.FileNameAdapter
import com.example.videoencoder.utils.FileUtils
import com.example.videoencoder.utils.Preferences
import kotlinx.android.synthetic.main.activity_crypt_file.*
import java.io.File
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class CryptActivity : AppCompatActivity(), View.OnClickListener, IOnFileSelected {


    private var encryptedFilePath: Uri? = null
    private var outputPath: String? = null

    private lateinit var videoUri: Uri
    private val encrypterAES: AesEncrypter
    private val encrypterBlowfish: Blowfish
    private var secretKey: String? = null


    init {
        encrypterAES = AesEncrypter(getKeyForEncryption())
        encrypterBlowfish = Blowfish()
        AesEncrypter.context = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypt_file)

        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1001
        )

        encrypt.setOnClickListener(this)

        createDirectoryfolder()
        displayEncryptedFiles()
    }

    private fun displayEncryptedFiles() {
        val path = Environment.getExternalStorageDirectory()
            .toString() + File.separator + "Encrypted" + File.separator + "AES"
        val directory = File(path)
        val filesArray = directory.listFiles()
        val fileList = ArrayList<File>()
        if(!filesArray.isNullOrEmpty()) {
            for (file in filesArray) {
                fileList.add(file)
            }
        }

        val adapter = FileNameAdapter(fileList, this)
        recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleview.adapter = adapter
        (recycleview.adapter as FileNameAdapter).notifyDataSetChanged()
    }

    private fun createDirectoryfolder() {
        val encrypted = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Encrypted"
        )
        if (!encrypted.exists())
            encrypted.mkdirs()

        val aes1 = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Encrypted" + File.separator + "AES"
        )
        if (!aes1.exists())
            aes1.mkdirs()

        val blowfish1 = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Encrypted" + File.separator + "Blowfish"
        )
        if (!blowfish1.exists())
            blowfish1.mkdirs()

        val decrypted = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Decrypted"
        )
        if (!decrypted.exists())
            decrypted.mkdirs()

        val aes = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Decrypted" + File.separator + "AES"
        )
        if (!aes.exists())
            aes.mkdirs()

        val blowfish = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Decrypted" + File.separator + "Blowfish"
        )
        if (!blowfish.exists())
            blowfish.mkdirs()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun performFileSearch() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Document"),
            REQUEST_OPEN_DOCUMENT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_OPEN_DOCUMENT -> {
                    videoUri = data!!.data!!
                    outputPath = FileUtils.getPath(this, videoUri)


                    encryptedFileName.text = videoUri.path

                    val checked = findViewById<RadioButton>(radioalgo.checkedRadioButtonId)

                    if (checked.text == getString(R.string.aes)) {
                        performAESEncryption()
                    } else if (checked.text == getString(R.string.blowfish)) {
                        performBlowfishEncryption()
                    }
                }
            }
        }
    }

    private fun performBlowfishEncryption() {
        encryptedFilePath = Uri.parse(
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "Encrypted" + File.separator + "Blowfish" + File.separator + outputPath!!.substring(
                outputPath!!.lastIndexOf("/") + 1
            )
        )
        encrypterBlowfish.encrypt(encryptedFilePath!!.path!!, outputPath!!)
    }

    private fun performAESEncryption() {
        encryptedFilePath = Uri.parse(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Encrypted" + File.separator + "AES" + File.separator + File(outputPath!!).name)

        if(Preferences.getString(ApplicationConstants.AES_SECRET_KEY).isNullOrEmpty()) {
            secretKey = encrypterAES.encryptFile(outputPath, encryptedFilePath!!)
            secretKey?.let { Preferences.putString(ApplicationConstants.AES_SECRET_KEY, it) }
        } else {
            secretKey = Preferences.getString(ApplicationConstants.AES_SECRET_KEY)
            secretKey = encrypterAES.encryptFile(outputPath, encryptedFilePath!!)
        }

        Log.e("path", encryptedFilePath!!.path!!)
        if (secretKey != null) {
            Toast.makeText(this, "File encrypted", Toast.LENGTH_SHORT).show()
            displayEncryptedFiles()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.encrypt -> {
                performFileSearch()
            }

        }
    }

    private fun performBlowfishDencryption(file: File) {
        val decrypted = encrypterBlowfish.decrypt(encryptedFilePath!!.path!!, file.absolutePath)
        if (decrypted) {
            Toast.makeText(this, "File decrypted", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun performAESDecryption(encryptedFile: File, file: File) {
        if(secretKey.isNullOrEmpty()) {
            secretKey = Preferences.getString(ApplicationConstants.AES_SECRET_KEY)
        }
        val decrypted = encrypterAES.decryptFile(Uri.parse(encryptedFile.absolutePath), file.absolutePath, secretKey)
        if (decrypted) {
            Toast.makeText(this, "File decrypted", Toast.LENGTH_SHORT).show()
            openDecryptedFile(file)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openDecryptedFile(file: File) {
        when(file.extension) {
            "jpg" -> {
                    val intent = Intent(this, ActivityViewImage::class.java)
                    intent.putExtra(ApplicationConstants.IMAGE_PATH, file.absolutePath)
                    startActivity(intent)
            }

            "mp4" -> {
                val intent = Intent(this, VideoViewActivity::class.java)
                intent.putExtra(ApplicationConstants.VIDEO_PATH, file.absolutePath)
                startActivity(intent)
            }

            "png" -> {
                val intent = Intent(this, ActivityViewImage::class.java)
                intent.putExtra(ApplicationConstants.IMAGE_PATH, file.absolutePath)
                startActivity(intent)
            }

            "pdf" -> {
                val intent = Intent(this, PdfViewActivity::class.java)
                intent.putExtra(ApplicationConstants.PDF_PATH, file.absolutePath)
                startActivity(intent)
            }

            "doc" -> {
                val intent = Intent(this, PdfViewActivity::class.java)
                intent.putExtra(ApplicationConstants.PDF_PATH, file.absolutePath)
                startActivity(intent)
            }
        }
    }

    private fun openPDFDocument(filename: String) {
        val pdfFile = File(filename)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        val fileUri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)
        pdfIntent.setDataAndType(fileUri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        val viewerIntent = Intent.createChooser(pdfIntent, "Open PDF")
        startActivity(viewerIntent)
    }

    companion object {
        private const val REQUEST_OPEN_DOCUMENT = 11
    }

    private fun getKeyForEncryption(): SecretKey {
        val encryptionKey: SecretKey
        if (Preferences.getString(ApplicationConstants.AES_ENCRYPTION_KEY).isNullOrEmpty()) {
            encryptionKey = KeyGenerator.getInstance(AesEncrypter.ALGO_SECRET_KEY_GENERATOR).generateKey()
            Preferences.putString(ApplicationConstants.AES_ENCRYPTION_KEY, encryptionKey.toString())
        } else {
            val keyString = Preferences.getString(ApplicationConstants.AES_ENCRYPTION_KEY)!!
            encryptionKey = SecretKeySpec(
                keyString.toByteArray(),
                0,
                keyString.length,
                AesEncrypter.ALGO_SECRET_KEY_GENERATOR
            )
        }
        return encryptionKey
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onFileSelected(encryptedFile: File) {
        val decryptedFile = File(
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "Decrypted" + File.separator + "AES" + File.separator + encryptedFile.name)
        if(!decryptedFile.exists()) {
        performAESDecryption(encryptedFile, decryptedFile)
        } else {
            openDecryptedFile(decryptedFile)
        }
    }
}
