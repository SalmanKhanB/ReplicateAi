package com.example.replicateai.util



import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    requireContext().toast(message)
}

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingUrl: String? = null

    fun playAudio(context: Context, url: String, onCompletion: () -> Unit = {}) {
        if (currentPlayingUrl == url) {
            // Toggle play/pause if same URL
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
            return
        }

        // Stop previous playback
        stopAudio()

        currentPlayingUrl = url
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { mp ->
                mp.start()
            }
            setOnCompletionListener {
                currentPlayingUrl = null
                onCompletion()
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentPlayingUrl = null
    }

    fun isPlaying(url: String): Boolean {
        return currentPlayingUrl == url && mediaPlayer?.isPlaying == true
    }
}
