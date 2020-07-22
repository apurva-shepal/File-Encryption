package com.example.videoencoder.ui.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.crypto.aes.AesEncrypter
import com.example.videoencoder.R
import com.example.videoencoder.utils.FileUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_crypt_file.*
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private var storeUri: Uri? = null
    private var outputPath: String? = null
    private val playbackPosition: Long = 0
    private val currentWindow: Int = 0
    private lateinit var videoUri: Uri
    private var player: SimpleExoPlayer? = null
    private val encrypter: AesEncrypter
        get() {
            TODO()
        }
    private var key: String? = null

    init {

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
//        decrypt.setOnClickListener(this)
        createDirectoryfolder()
    }

    private fun createDirectoryfolder() {
        val encrypted = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Encrypted"
        )
        if(!encrypted.exists())
            encrypted.mkdirs()

        val decrypted = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Decrypted"
        )
        if(!decrypted.exists())
            decrypted.mkdirs()
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
                    storeUri = Uri.parse(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Encrypted" +  File.separator + outputPath!!.substring(outputPath!!.lastIndexOf("/")+1) )
                    key = encrypter.encryptFile(outputPath, storeUri!!)
                    Log.e("path", storeUri!!.path!!)
//                    if(!key.isNullOrEmpty())
                        Toast.makeText(this, "File encrypted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializePlayer(outputPath: String?) {
        player = ExoPlayerFactory.newSimpleInstance(
            this,
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
//        video_view.player = player
//        setDataSource(outputPath)
    }

    private fun setDataSource(outputPath: String?) {
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "VideoEncoder"))
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = ExtractorMediaSource(
            Uri.fromFile(File(outputPath)),
            dataSourceFactory, extractorsFactory, null, null
        )
        player!!.prepare(videoSource)
        player!!.playWhenReady = true
        player?.addListener(playerListener)
        player!!.seekTo(0)
        player?.seekTo(currentWindow, playbackPosition)
    }


    private val playerListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onSeekProcessed() {
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Toast.makeText(
                this@MainActivity,
                error!!.message,
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onLoadingChanged(isLoading: Boolean) {

        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.encrypt -> {
                performFileSearch()

            }

        }
    }


    companion object {
        private val REQUEST_OPEN_DOCUMENT = 11
    }
}