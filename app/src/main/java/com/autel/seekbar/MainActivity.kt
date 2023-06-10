package com.autel.seekbar

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var mSeekBarView: BatteryBackupRatio? = null
    private val tickMarks = mutableListOf<TickMarkBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSeekBarView = findViewById<BatteryBackupRatio>(R.id.seekbar)

        findViewById<Button>(R.id.btn_mock).setOnClickListener {
            mockTickMarksData()
        }
    }


    private fun mockTickMarksData() {
        for (i in 0..20) {
            val bean = TickMarkBean(
                i, (5 * i).toString() + "%", i % 4 == 0
            )
            tickMarks.add(i, bean)
        }
        mSeekBarView?.initTickMarkData(tickMarks)
        mSeekBarView?.setInitialProgressValue("40%")

        mSeekBarView?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                mSeekBarView?.updateProgressValue(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}