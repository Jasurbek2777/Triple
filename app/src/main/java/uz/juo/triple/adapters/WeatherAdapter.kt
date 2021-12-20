package uz.juo.triple.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.juo.triple.R
import uz.juo.triple.databinding.WeatherItemBinding
import uz.juo.triple.models.weather.Daily
import uz.juo.triple.models.weather.WeatherData
import uz.juo.triple.utils.Functions
import kotlin.math.roundToInt

class WeatherAdapter(var data: WeatherData) : RecyclerView.Adapter<WeatherAdapter.Vh>() {
    inner class Vh(var item: WeatherItemBinding) : RecyclerView.ViewHolder(item.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(info: Daily) {
            item.itemTemp.text = "${info.temp.day.roundToInt()} / ${info.temp.night.roundToInt()}"
            var date = Functions().weatherDataConvertor(info.dt.toLong())
            item.dayTv.text = "${date[0]}/${date[1]}/${date[2]}"
            var desc=info.weather[0].main
            item.weatherDesc.text =desc
            when(desc.lowercase()){
                "clouds"->{
                    item.icon.setImageResource(R.drawable.cloud)
                }
                "rain"->{
                    item.icon.setImageResource(R.drawable.rain)
                }
                "snow"->{
                    item.icon.setImageResource(R.drawable.snow)
                }
                else->{
                    item.icon.setImageResource(R.drawable.sunny)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(data.daily[position])
    }

    override fun getItemCount(): Int {
        return data.daily.size
    }
}