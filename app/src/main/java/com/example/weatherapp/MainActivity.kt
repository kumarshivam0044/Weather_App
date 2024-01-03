package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

//4f7b7f1fd812f5e4c1e4f3994f700f4f api key
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(1000)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        getSupportActionBar()?.hide();
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity() {
       val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }


    private fun fetchWeatherData(cityName:String) {
     val retrofit = Retrofit.Builder()
         .addConverterFactory(GsonConverterFactory.create())
         .baseUrl("https://api.openweathermap.org/data/2.5/")
         .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"4f7b7f1fd812f5e4c1e4f3994f700f4f","metric")
        response.enqueue(object:Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
                if (response.isSuccessful&&responseBody!=null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val Weather = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                  binding.Temp.text = "$temperature°C"

                    binding.Weather.text = Weather
                    binding.maxtemp.text= "Max Temp :$maxTemp °C"
                    binding.minTemp.text= "Min Temp :$minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text="$windSpeed m/s "
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.sunny.text = Weather
                    binding.Today.text =dayName(System.currentTimeMillis())
                    binding.Date.text =  date()
                    binding.cityname.text = "$cityName"
                    // Log.d("TAG","onResponse:$temperature")

                    changethemeaccordingtoweather(Weather)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changethemeaccordingtoweather(conditionS:String) {
      when(conditionS){
          "Haze"->{
              binding.root.setBackgroundResource(R.drawable.colud_background)
              binding.lottieAnimationView.setAnimation(R.raw.cloud)
          }
          "Clear Sky", "Sunny","Clear"->{
              binding.root.setBackgroundResource(R.drawable.sunny_background)
              binding.lottieAnimationView.setAnimation(R.raw.sun)
          }
          "Partly Clouds ","Clouds","OverCast","Mist","Foggy","Fog"->{
              binding.root.setBackgroundResource(R.drawable.colud_background)
              binding.lottieAnimationView.setAnimation(R.raw.cloud)
          }
          "Light Rain", "Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
              binding.root.setBackgroundResource(R.drawable.rain_background)
              binding.lottieAnimationView.setAnimation(R.raw.rain)
          }
          "Light Snow", "Moderate Snow","Heavy Snow", "Blizzard","Snow"->{
              binding.root.setBackgroundResource(R.drawable.snow_background)
              binding.lottieAnimationView.setAnimation(R.raw.snow)
          }
          else->{
              binding.root.setBackgroundResource(R.drawable.sunny_background)
              binding.lottieAnimationView.setAnimation(R.raw.sun)
          }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }

    fun dayName(timeStamp:Long):String
    {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}