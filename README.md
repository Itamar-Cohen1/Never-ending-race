# Never Ending Race 🏎️

An Android racing game app developed as a final project. The game features a car that must dodge obstacles and collect coins along an endless track.

## Key Features

* **Game Structure (No Canvas):** The track is built with 5 separate lanes. Adhering to the critical restriction of not using Canvas, all rendering, obstacles, and movement are implemented exclusively using standard UI components (Views and Layouts).
* **Advanced Control Modes:** * Button-based gameplay (regular or fast speed).
  * **Sensor Controls:** Utilizes the device's tilt sensors. The X-axis is used for moving left and right between lanes, and the Y-axis is used for dynamic game speed control (tilt forward to accelerate, backward to decelerate).
* **High Scores and Location Tracking (GPS & Maps):**
  * Upon crashing (game over), the game triggers a vibration, fetches the current device location (GPS coordinates), and saves the score in local storage (`SharedPreferences`).
  * **Split High Scores Screen:** Displays the top 10 highest scores in a list, integrated with Google Maps. 
  * **Map Interactivity:** Clicking on a specific score in the list smoothly animates the map camera to the exact location where the player crashed (marked with a pin).

## Technologies and Libraries
* Language: Kotlin
* Location Services: Fused Location Provider
* Maps: Google Maps SDK for Android
