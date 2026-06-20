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

class MainActivity : AppCompatActivity(),android.hardware.SensorEventListener {
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
    private lateinit var sensorManager: android.hardware.SensorManager
    private var accelerometer: android.hardware.Sensor? = null
    private var obstacleSpeed = 25f
    private var coinSpeed = 20f
    private var lastTiltTime: Long = 0
    private var currentLane = 2
    private val laneWidth = 180f
    private var lives = 3
    private var distance = 0
    private lateinit var tvOdometer: android.widget.TextView
    private lateinit var coin: ImageView
    private lateinit var playerCar: ImageView
    private lateinit var obstacle: ImageView
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)

        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        sensorManager = getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
        accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvOdometer = findViewById(R.id.tv_odometer)
        playerCar = findViewById(R.id.player_car)
        obstacle = findViewById(R.id.obstacle)
        coin = findViewById(R.id.coin)
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
            if (currentLane < 4) {
                currentLane++
                moveCarUI(playerCar)
            }
        }
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "BUTTONS"
        val gameSpeed = intent.getStringExtra("GAME_SPEED") ?: "NORMAL"

        if (gameMode == "SENSORS") {
            btnLeft.visibility = android.view.View.INVISIBLE
            btnRight.visibility = android.view.View.INVISIBLE
        }

        if (gameSpeed == "FAST") {
            obstacleSpeed = 40f
            coinSpeed = 35f
        } else if (gameSpeed == "SLOW") {
            obstacleSpeed = 15f
            coinSpeed = 10f
        }
        startObstacleMovement()
    }
    override fun onSensorChanged(event: android.hardware.SensorEvent?) {
        if (event?.sensor?.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val currentTime = System.currentTimeMillis()


            if (y < 4.0f) {
                obstacleSpeed = 40f
                coinSpeed = 35f
            } else if (y > 7.5f) {
                obstacleSpeed = 15f
                coinSpeed = 10f
            } else {
                obstacleSpeed = 25f
                coinSpeed = 20f
            }

            if (currentTime - lastTiltTime > 500) {
                if (x < -2.0f) {
                    if (currentLane < 4) {
                        currentLane++
                        moveCarUI(playerCar)
                        lastTiltTime = currentTime
                    }
                } else if (x > 2.0f) {
                    if (currentLane > 0) {
                        currentLane--
                        moveCarUI(playerCar)
                        lastTiltTime = currentTime
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, android.hardware.SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    private fun moveCarUI(car: ImageView) {
        when (currentLane) {
            0 -> car.translationX = -(laneWidth * 2)
            1 -> car.translationX = -laneWidth
            2 -> car.translationX = 0f
            3 -> car.translationX = laneWidth
            4 -> car.translationX = (laneWidth * 2)
        }
    }

    private fun startObstacleMovement() {
        val runnable = object : Runnable {
            override fun run() {
                val carRect = Rect()
                playerCar.getHitRect(carRect)

                coin.translationY += coinSpeed

                val coinRect = Rect()
                coin.getHitRect(coinRect)

                if (Rect.intersects(carRect, coinRect)) {
                    distance += 50
                    tvOdometer.text = "Distance: $distance"
                    resetCoinPosition()
                }

                if (coin.translationY > 2500f) {
                    resetCoinPosition()
                }

                distance += 1
                tvOdometer.text = "Distance: $distance"

                obstacle.translationY += obstacleSpeed

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
        val mediaPlayer = android.media.MediaPlayer.create(this, R.raw.crash_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }

        lives--

        when (lives) {
            2 -> heart3.visibility = View.INVISIBLE
            1 -> heart2.visibility = View.INVISIBLE
            0 -> {
                heart1.visibility = View.INVISIBLE
                Toast.makeText(this, "המשחק נגמר! מתחילים מחדש...", Toast.LENGTH_LONG).show()
                if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
                        val lat = location?.latitude ?: 0.0
                        val lon = location?.longitude ?: 0.0
                        saveScore(distance, lat, lon)
                        finish()
                    }
                } else {
                    saveScore(distance, 0.0, 0.0)

                    finish()
                }
            }
        }

    }

    private fun resetGame() {
        lives = 3
        heart1.visibility = View.VISIBLE
        heart2.visibility = View.VISIBLE
        heart3.visibility = View.VISIBLE
        distance = 0
        tvOdometer.text = "Distance: 0"
        currentLane = 1
        moveCarUI(playerCar)
    }

    private fun resetObstaclePosition() {
        obstacle.translationY = -200f
        val randomLane = Random.nextInt(5)
        when (randomLane) {
            0 -> obstacle.translationX = -(laneWidth * 2)
            1 -> obstacle.translationX = -laneWidth
            2 -> obstacle.translationX = 0f
            3 -> obstacle.translationX = laneWidth
            4 -> obstacle.translationX = (laneWidth * 2)
        }
    }
    private fun resetCoinPosition() {
        coin.translationY = -800f
        val randomLane = Random.nextInt(5)
        when (randomLane) {
            0 -> coin.translationX = -(laneWidth * 2)
            1 -> coin.translationX = -laneWidth
            2 -> coin.translationX = 0f
            3 -> coin.translationX = laneWidth
            4 -> coin.translationX = (laneWidth * 2)
        }
    }
    private fun saveScore(score: Int, lat: Double, lon: Double) {
        val sharedPreferences = getSharedPreferences("HighScores", android.content.Context.MODE_PRIVATE)
        val scoresString = sharedPreferences.getString("SCORES_LOC", "") ?: ""

        val scoresList = if (scoresString.isNotEmpty()) {
            scoresString.split(",").toMutableList()
        } else {
            mutableListOf()
        }

        scoresList.add("$score|$lat|$lon")

        scoresList.sortByDescending { it.split("|")[0].toInt() }
        val top10 = scoresList.take(10)

        sharedPreferences.edit().putString("SCORES_LOC", top10.joinToString(",")).apply()
    }
}
