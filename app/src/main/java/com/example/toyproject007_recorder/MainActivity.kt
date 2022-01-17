package com.example.toyproject007_recorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizerView)
    }
    private val recordTimetextView: CountUpView by lazy{
        findViewById(R.id.recordTimeTextView)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recoding.3gp"
    }
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECODING
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING ||
                    value == State.ON_PLAYING)//false면 버튼이 클릭이 안됨
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()
    }


    //    요청된 권한이 이미 허가가 되었는지 아닌지에 대해서 오버라이드 메시지를 통해서 확인 가능
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }

    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() {
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        resetButton.setOnClickListener {
            stopPlying()
            state = State.BEFORE_RECODING
        }
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECODING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlating()
                }
                State.ON_PLAYING -> {
                    stopPlying()
                }
            }
        }
    }

    private fun initVariables() {
        state = State.BEFORE_RECODING
    }


    private fun startRecording() {
        recorder = MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(recordingFilePath)
                prepare()
            }
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimetextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimetextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlating() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }
        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimetextView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimetextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}