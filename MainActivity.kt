package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val API: String = "47a097065d990060948a85c7857a168e" // Your API Key
    private var CITY: String = "dhaka,bd" // Default city

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blaa) // Correct layout file name

        val cityInput = findViewById<EditText>(R.id.cityInput)
        val fetchWeatherButton = findViewById<Button>(R.id.fetchWeatherButton)

        fetchWeatherButton.setOnClickListener {
            val inputCity = cityInput.text.toString().trim()
            if (inputCity.isNotEmpty()) {
                CITY = inputCity
                WeatherTask().execute()
            } else {
                cityInput.error = "Please enter a city!"
            }
        }
    }

    inner class WeatherTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            return try {
                // Fetch weather data from the OpenWeather API
                URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                if (result == null) throw Exception("No response from server.")

                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " +
                        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                // Set data to UI elements
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar { it.uppercase() }
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.wind).text = "Wind Speed: $windSpeed km/h"
                findViewById<TextView>(R.id.pressure).text = "Pressure: $pressure hPa"
                findViewById<TextView>(R.id.humidity).text = "Humidity: $humidity%"

                // Set sunrise and sunset times
                val sunriseText = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                val sunsetText = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))

                findViewById<TextView>(R.id.sunrise).text = "Sunrise: $sunriseText"
                findViewById<TextView>(R.id.sunset).text = "Sunset: $sunsetText"

                // Set sunrise and sunset icons
                val sunriseIcon = findViewById<ImageView>(R.id.sunrise_icon)
                val sunsetIcon = findViewById<ImageView>(R.id.sunset_icon)
                sunriseIcon.setImageResource(R.drawable.sunrise) // Make sure this icon is in your drawable folder
                sunsetIcon.setImageResource(R.drawable.sunset)   // Make sure this icon is in your drawable folder

                // Set visibility of views
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }
}
