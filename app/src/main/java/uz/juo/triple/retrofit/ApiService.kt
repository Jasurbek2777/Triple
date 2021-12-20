package uz.juo.triple.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import uz.juo.triple.models.azon.AzonData
import uz.juo.triple.models.news.NewsData
import uz.juo.triple.models.weather.WeatherData

interface ApiService {

    companion object {
        private var WEATHER_KEY = "b51835a3ebd55518a657fa5878f37e24"
    }

    @GET("news?countries=ru&access_key=f811a4d6358f4321c731eeacca3e980e")
    suspend fun getNews(
        @Query("limit") limit: Int = 30,
        @Query("sort") sort: String = "published_desc",
        @Query("offset") offset: Int = 1,
    ): NewsData

    @GET
    suspend fun getWeather(
        @Url str: String =
            "https://api.openweathermap.org/data/2.5/onecall",
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String="metric",
        @Query("exclude") exclude: String="part",
        @Query("cnt") cnt: Int=6,
        @Query("appid") appid: String=WEATHER_KEY,
    ): WeatherData
    @GET
    suspend fun getData(
        @Url str: String =
            "http://api.aladhan.com/v1/calendarByAddress?",
        @Query("address") address: String="Tashkent,uzbekistan",
        @Query("method") method: Int=2,
        @Query("month") month: Int=12,
        @Query("year") year: Int=2021,
    ): AzonData
}