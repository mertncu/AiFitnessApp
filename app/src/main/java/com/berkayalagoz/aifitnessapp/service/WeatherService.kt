package com.berkayalagoz.aifitnessapp.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class WeatherResponse(
    val current: CurrentWeather,
    val current_units: CurrentUnits
)

@Serializable
data class CurrentWeather(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val weather_code: Int,
    val wind_speed_10m: Double
)

@Serializable
data class CurrentUnits(
    val time: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val weather_code: String,
    val wind_speed_10m: String
)

data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double,
    val weatherDescription: String,
    val weatherEmoji: String,
    val cityName: String = "İstanbul"
)

class WeatherService {
    private val json = Json { ignoreUnknownKeys = true }
    
    // İstanbul koordinatları
    private val latitude = 41.0082
    private val longitude = 28.9784
    
    suspend fun getCurrentWeather(): WeatherData? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FIstanbul"
                
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val weatherResponse = json.decodeFromString<WeatherResponse>(response)
                    
                    WeatherData(
                        temperature = weatherResponse.current.temperature_2m,
                        humidity = weatherResponse.current.relative_humidity_2m,
                        weatherCode = weatherResponse.current.weather_code,
                        windSpeed = weatherResponse.current.wind_speed_10m,
                        weatherDescription = getWeatherDescription(weatherResponse.current.weather_code),
                        weatherEmoji = getWeatherEmoji(weatherResponse.current.weather_code),
                        cityName = "İstanbul"
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    private fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Açık"
            1, 2, 3 -> "Parçalı Bulutlu"
            45, 48 -> "Sisli"
            51, 53, 55 -> "Çiseleyen"
            56, 57 -> "Dondurucu Çiseleme"
            61, 63, 65 -> "Yağmurlu"
            66, 67 -> "Dondurucu Yağmur"
            71, 73, 75 -> "Karlı"
            77 -> "Kar Tanesi"
            80, 81, 82 -> "Sağanak"
            85, 86 -> "Kar Sağanağı"
            95 -> "Fırtınalı"
            96, 99 -> "Dolu ile Fırtına"
            else -> "Bilinmeyen"
        }
    }
    
    private fun getWeatherEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2, 3 -> "⛅"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌦️"
            56, 57 -> "🌨️"
            61, 63, 65 -> "🌧️"
            66, 67 -> "🌨️"
            71, 73, 75 -> "❄️"
            77 -> "🌨️"
            80, 81, 82 -> "🌦️"
            85, 86 -> "🌨️"
            95 -> "⛈️"
            96, 99 -> "⛈️"
            else -> "🌤️"
        }
    }
} 