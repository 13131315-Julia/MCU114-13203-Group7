package com.example.music

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkViewModel : ViewModel() {
    private val handlerThread = HandlerThread("VM-Work").apply { start() }
    private val worker = Handler(handlerThread.looper)

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _status = MutableLiveData("Idle")
    val status: LiveData<String> = _status

    @Volatile private var running = false
    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {
        if (running) return
        running = true
        _status.postValue("Preparing…")
        _progress.postValue(0)

        // 開始播放背景音樂
        startBackgroundMusic(context)

        worker.post {
            try {
                // 準備階段 - 3秒
                Thread.sleep(3000)
                if (!running) return@post

                _status.postValue("Working…")

                // 工作階段 - 100步，每步100ms
                for (i in 1..100) {
                    if (!running) break
                    Thread.sleep(100)
                    _progress.postValue(i)
                }

                if (running) {
                    _status.postValue("背景工作結束！")
                    stopBackgroundMusic() // 完成時停止音樂
                } else {
                    _status.postValue("Canceled")
                }
                running = false
            } catch (_: InterruptedException) {
                _status.postValue("Canceled")
                stopBackgroundMusic() // 中斷時停止音樂
                running = false
            }
        }
    }

    fun cancel() {
        if (running) {
            running = false
            _status.postValue("Canceled")
            stopBackgroundMusic() // 取消時停止音樂
        }
    }

    private fun startBackgroundMusic(context: Context) {
        try {
            // 確保有 background_music.mp3 在 res/raw 資料夾中
            val resourceId = context.resources.getIdentifier("background_music", "raw", context.packageName)
            if (resourceId != 0) {
                mediaPlayer = MediaPlayer.create(context, resourceId).apply {
                    isLooping = true // 循環播放
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopBackgroundMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        running = false
        stopBackgroundMusic() // 清理時停止音樂
        handlerThread.quitSafely()
        super.onCleared()
    }
}