package com.example.digitaltasbih

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvCount: TextView
    private lateinit var btnTasbih: Button
    private lateinit var btnReset: Button
    private lateinit var swFullScreen: Switch
    private lateinit var swSound: Switch
    private lateinit var swVibrate: Switch
    private lateinit var root: View

    private var count = 0
    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var tone: ToneGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCount = findViewById(R.id.tv_count)
        btnTasbih = findViewById(R.id.btn_tasbih)
        btnReset = findViewById(R.id.btn_reset)
        swFullScreen = findViewById(R.id.sw_full_screen)
        swSound = findViewById(R.id.sw_sound)
        swVibrate = findViewById(R.id.sw_vibrate)
        root = findViewById(R.id.root)

        prefs = getSharedPreferences("tasbih_prefs", MODE_PRIVATE)
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        count = prefs.getInt("count", 0)
        tvCount.text = count.toString()

        swFullScreen.isChecked = prefs.getBoolean("full_screen", true)
        swSound.isChecked = prefs.getBoolean("sound", true)
        swVibrate.isChecked = prefs.getBoolean("vibrate", true)

        val increment: () -> Unit = {
            count++
            tvCount.text = count.toString()
            prefs.edit().putInt("count", count).apply()
            if (swSound.isChecked) tone.startTone(ToneGenerator.TONE_CDMA_NETWORK_USA_CODEB, 50)
            if (swVibrate.isChecked) {
                val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
                v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }

        btnTasbih.setOnClickListener { increment() }

        root.setOnClickListener { v ->
            if (swFullScreen.isChecked && v.id == R.id.root) increment()
        }

        btnReset.setOnClickListener {
            count = 0
            tvCount.text = "0"
            prefs.edit().putInt("count", 0).apply()
        }

        swFullScreen.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("full_screen", checked).apply()
        }
        swSound.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("sound", checked).apply()
        }
        swVibrate.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("vibrate", checked).apply()
        }
    }

    override fun onDestroy() {
        tone.release()
        super.onDestroy()
    }
}