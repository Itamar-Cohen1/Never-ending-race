package com.example.neverendingrace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btnSlow = findViewById<Button>(R.id.btn_play_buttons_slow)
        val btnFast = findViewById<Button>(R.id.btn_play_buttons_fast)
        val btnSensors = findViewById<Button>(R.id.btn_play_sensors)
        val btnHighScores = findViewById<Button>(R.id.btn_high_scores)

        btnSlow.setOnClickListener {
            startGame("BUTTONS", "SLOW")
        }

        btnFast.setOnClickListener {
            startGame("BUTTONS", "FAST")
        }

        btnSensors.setOnClickListener {
            startGame("SENSORS", "NORMAL")
        }

        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(mode: String, speed: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GAME_MODE", mode)
        intent.putExtra("GAME_SPEED", speed)
        startActivity(intent)
    }
}