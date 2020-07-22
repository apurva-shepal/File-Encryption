package com.example.videoencoder.ui.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.crypto.aes.AesEncrypter
import com.example.videoencoder.R
import com.example.videoencoder.constants.ApplicationConstants
import com.example.videoencoder.utils.FileUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_view_video_file.*
import java.io.File

class VideoViewActivity : AppCompatActivity() {

    private val currentWindow: Int = 0
    private var player: SimpleExoPlayer? = null
    private val playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_video_file)
        val videoPath = intent.getStringExtra(ApplicationConstants.VIDEO_PATH)!!
        initializePlayer(videoPath)
    }

    private fun initializePlayer(outputPath: String?) {
        player = ExoPlayerFactory.newSimpleInstance(
            this,
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        video_view.player = player
        setDataSource(outputPath)
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
                this@VideoViewActivity,
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
}