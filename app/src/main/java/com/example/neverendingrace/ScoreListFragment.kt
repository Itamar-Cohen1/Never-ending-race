package com.example.neverendingrace
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class ScoreListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score_list, container, false)
        val listView = view.findViewById<ListView>(R.id.scores_list_view)

        val sharedPreferences = requireActivity().getSharedPreferences("HighScores", android.content.Context.MODE_PRIVATE)
        val scoresString = sharedPreferences.getString("SCORES_LOC", "") ?: ""

        val rawScoresList = if (scoresString.isNotEmpty()) scoresString.split(",") else listOf()

        val displayList = if (rawScoresList.isNotEmpty()) {
            rawScoresList.map { item ->
                val parts = item.split("|")
                "Distance: ${parts[0]}"
            }
        } else {
            listOf("No scores yet")
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, displayList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            if (rawScoresList.isNotEmpty() && rawScoresList.size > position) {
                val parts = rawScoresList[position].split("|")
                if (parts.size >= 3) {
                    val lat = parts[1].toDoubleOrNull() ?: 0.0
                    val lon = parts[2].toDoubleOrNull() ?: 0.0

                    (activity as? HighScoresActivity)?.updateMapLocation(lat, lon)
                }
            }
        }

        return view
    }
}