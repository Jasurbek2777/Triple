package uz.juo.triple.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.juo.triple.adapters.NewsAdapter
import uz.juo.triple.databinding.FragmentNewsBinding
import uz.juo.triple.models.news.Data
import uz.juo.triple.models.news.NewsData
import uz.juo.triple.paging.NewsSource
import uz.juo.triple.retrofit.ApiClient
import uz.juo.triple.utils.NetworkHelper
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NewsFragment : Fragment() {
    private val TAG = "NewsFragment"
    lateinit var binding: FragmentNewsBinding
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: NewsAdapter
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        adapter = NewsAdapter(requireContext(), object : NewsAdapter.listen {
            override fun setOnClick(data: Data) {
                try {
                    val url = data.url
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.newsRv.adapter = adapter
        showLoading()
        loadData()
        binding.swiper.setOnRefreshListener {
            loadData()
        }

        return binding.root
    }

    fun showLoading() {
        binding.spinKit.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.spinKit.visibility = View.INVISIBLE
    }

    private fun loadData() {
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            viewModel.branches().observe(viewLifecycleOwner, {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            })
            hideLoading()
        } else {
            showLoading()
        }

        binding.swiper.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}