package com.example.neverendingrace

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var currentLane = 1
    private val laneWidth = 300f
    private var lives = 3

    private lateinit var playerCar: ImageView
    private lateinit var obstacle: ImageView
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerCar = findViewById(R.id.player_car)
        obstacle = findViewById(R.id.obstacle)
        val btnLeft = findViewById<Button>(R.id.btn_left)
        val btnRight = findViewById<Button>(R.id.btn_right)

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)

        btnLeft.setOnClickListener {
            if (currentLane > 0) {
                currentLane--
                moveCarUI(playerCar)
            }
        }

        btnRight.setOnClickListener {
            if (currentLane < 2) {
                currentLane++
                moveCarUI(playerCar)
            }
        }

        startObstacleMovement()
    }

    private fun moveCarUI(car: ImageView) {
        when (currentLane) {
            0 -> car.translationX = -laneWidth
            1 -> car.translationX = 0f
            2 -> car.translationX = laneWidth
        }
    }

    private fun startObstacleMovement() {
        val runnable = object : Runnable {
            override fun run() {
                obstacle.translationY += 25f

                val carRect = Rect()
                playerCar.getHitRect(carRect)

                val obstacleRect = Rect()
                obstacle.getHitRect(obstacleRect)

                if (Rect.intersects(carRect, obstacleRect)) {
                    handleCrash()
                    resetObstaclePosition()
                }

                if (obstacle.translationY > 2500f) {
                    resetObstaclePosition()
                }

                handler.postDelayed(this, 30)
            }
        }
        handler.post(runnable)
    }

    private fun handleCrash() {
        Toast.makeText(this, "בום! נפגעת!", Toast.LENGTH_SHORT).show()

        // --- קוד הרטט החדש ---
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
        // ----------------------

        lives--

        when (lives) {
            2 -> heart3.visibility = View.INVISIBLE
            1 -> heart2.visibility = View.INVISIBLE
            0 -> {
                heart1.visibility = View.INVISIBLE
                Toast.makeText(this, "המשחק נגמר! מתחילים מחדש...", Toast.LENGTH_LONG).show()
                resetGame()
            }
        }
    }

    private fun resetGame() {
        lives = 3
        heart1.visibility = View.VISIBLE
        heart2.visibility = View.VISIBLE
        heart3.visibility = View.VISIBLE

        currentLane = 1
        moveCarUI(playerCar)
    }

    private fun resetObstaclePosition() {
        obstacle.translationY = -200f
        val randomLane = Random.nextInt(3)
        when (randomLane) {
            0 -> obstacle.translationX = -laneWidth
            1 -> obstacle.translationX = 0f
            2 -> obstacle.translationX = laneWidth
        }
    }
}