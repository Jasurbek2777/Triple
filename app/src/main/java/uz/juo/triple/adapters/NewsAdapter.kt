package uz.juo.triple.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.juo.triple.R
import uz.juo.triple.databinding.NewsRvItemBinding
import uz.juo.triple.models.news.Data

class NewsAdapter(var context: Context, var itemClick: listen) :
    PagingDataAdapter<Data, NewsAdapter.ViewHolder>(MyDiffUtil()) {
    inner class ViewHolder(var item: NewsRvItemBinding) : RecyclerView.ViewHolder(item.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(data: Data, position: Int) {
            var date = data.published_at.subSequence(0, data.published_at.indexOf("T"))
            var hours = data.published_at.subSequence(
                data.published_at.indexOf("T") + 1,
                data.published_at.indexOf("+")
            )
            var anim = AnimationUtils.loadAnimation(context, R.anim.rv)
            item.root.startAnimation(anim)
            item.dateTv.text = "$hours/$date"
            item.desc.text = data.description
            Picasso.get().load(data.image).placeholder(R.drawable.holder).into(item.itemImage)
            item.root.setOnClickListener {
                itemClick.setOnClick(data)
            }
        }
    }

    class MyDiffUtil() : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NewsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

    interface listen {
        fun setOnClick(data: Data)
    }
}