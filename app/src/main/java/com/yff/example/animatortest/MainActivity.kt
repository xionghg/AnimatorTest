package com.yff.example.animatortest

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.yff.example.animatortest.utils.Kaleidoscope

class MainActivity : Activity() {

    private var started: Boolean = false
    private lateinit var button: Button
    private lateinit var duration: SeekBar
    private lateinit var total: SeekBar
    private lateinit var durationText: TextView
    private lateinit var totalText: TextView
    private var kaleidoscope: Kaleidoscope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        duration = findViewById(R.id.duration)
        duration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                durationText.text = getText("duration", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        total = findViewById(R.id.total)
        total.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                totalText.text = getText("total", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        durationText = findViewById(R.id.duration_text)
        totalText = findViewById(R.id.total_text)
        durationText.text = getText("duration", duration.progress)
        totalText.text = getText("total", total.progress)
    }

    fun getText(string: String, int: Int): String {
        return "$string: $int"
    }

    fun onButtonClick(view: View) {
        if (started) {
            button.text = "Start"
            stop()
            started = false
        } else {
            button.text = "Stop"
            start()
            started = true
        }
    }

    private fun start() {
        kaleidoscope?.stop()
        kaleidoscope = Kaleidoscope.with(this)
                .total(total.progress)
                .duration(duration.progress)
                .colorRule(Kaleidoscope.RandomColorRule())
                .build()
        kaleidoscope?.start()
    }

    private fun stop() {
        kaleidoscope?.stopSmoothly()
    }
}
