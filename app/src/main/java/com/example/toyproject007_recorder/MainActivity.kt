package com.example.toyproject007_recorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton  by lazy{
        findViewById(R.id.recordButton)
    }

    private var state = State.BEFORE_RECODING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews(){
        recordButton.updateIconWithState(state)
    }
}