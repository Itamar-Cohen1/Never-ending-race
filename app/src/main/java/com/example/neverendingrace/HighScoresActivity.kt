package com.example.neverendingrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class HighScoresActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        supportFragmentManager.beginTransaction()
            .replace(R.id.list_fragment_container, ScoreListFragment())
            .commit()

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sharedPreferences = getSharedPreferences("HighScores", android.content.Context.MODE_PRIVATE)
        val scoresString = sharedPreferences.getString("SCORES_LOC", "") ?: ""

        if (scoresString.isNotEmpty()) {
            val scoresList = scoresString.split(",")

            for (item in scoresList) {
                val parts = item.split("|")
                if (parts.size >= 3) {
                    val score = parts[0]
                    val lat = parts[1].toDoubleOrNull() ?: 0.0
                    val lon = parts[2].toDoubleOrNull() ?: 0.0

                    if (lat != 0.0 && lon != 0.0) {
                        val position = com.google.android.gms.maps.model.LatLng(lat, lon)
                        mMap?.addMarker(
                            com.google.android.gms.maps.model.MarkerOptions()
                                .position(position)
                                .title("Score: $score")
                        )
                    }
                }
            }


            val firstParts = scoresList[0].split("|")
            if (firstParts.size >= 3) {
                val lat = firstParts[1].toDoubleOrNull() ?: 0.0
                val lon = firstParts[2].toDoubleOrNull() ?: 0.0
                if (lat != 0.0 && lon != 0.0) {
                    val firstPos = com.google.android.gms.maps.model.LatLng(lat, lon)
                    mMap?.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(firstPos, 12f))                }
            }

        }
    }
    fun updateMapLocation(lat: Double, lon: Double) {
        if (lat != 0.0 && lon != 0.0) {
            val position = com.google.android.gms.maps.model.LatLng(lat, lon)
            mMap?.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }
}