package uz.juo.triple.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import uz.juo.triple.R
import uz.juo.triple.databinding.AzonRvItemBinding
import uz.juo.triple.databinding.WeatherItemBinding
import uz.juo.triple.models.weather.Daily
import uz.juo.triple.models.weather.WeatherData
import uz.juo.triple.room.entity.AzonEntity
import uz.juo.triple.utils.Functions
import kotlin.math.roundToInt

class AzonAdapter(var context: Context, var data: ArrayList<AzonEntity>) :
    RecyclerView.Adapter<AzonAdapter.Vh>() {
    inner class Vh(var item: AzonRvItemBinding) : RecyclerView.ViewHolder(item.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(info: AzonEntity) {
            item.dayTv.text = info.date
            item.typeTv.text = info.type
            item.timeTv.text = "${info.hour}:${info.minut}"
            var anim = AnimationUtils.loadAnimation(context, R.anim.rv)
            item.root.startAnimation(anim)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(AzonRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}