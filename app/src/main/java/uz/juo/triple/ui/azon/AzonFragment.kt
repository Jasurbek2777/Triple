package uz.juo.triple.ui.azon

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.util.*
import android.content.Context.ALARM_SERVICE
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.juo.triple.adapters.AzonAdapter
import uz.juo.triple.databinding.FragmentAzonBinding
import uz.juo.triple.retrofit.ApiClient
import uz.juo.triple.room.AppDataBase
import uz.juo.triple.room.entity.AzonEntity
import uz.juo.triple.utils.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AzonFragment : Fragment() {

    lateinit var binding: FragmentAzonBinding
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "AzonFragment"
    lateinit var azonAdapter: AzonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAzonBinding.inflate(inflater, container, false)
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        showLoading()
        loadData()
//        loadTest()
        binding.setBtn.setOnClickListener {
            if (SharedPreference.getInstance(requireContext()).hasAlarm) {
                Snackbar.make(requireView(), "Alarm is already active", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                startAlert()
                Snackbar.make(requireView(), "Alarms are  activated", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadData() {
        var db = AppDataBase.getInstance(requireContext()).dao()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            lifecycleScope.launch {
                var data = ApiClient.apiService.getData()
                db.deleteAll()
                for (i in data.data) {
                    var timesList = ArrayList<String>()
                    timesList.add(i.timings.Fajr)
                    timesList.add(i.timings.Dhuhr)
                    timesList.add(i.timings.Asr)
                    timesList.add(i.timings.Maghrib)
                    timesList.add(i.timings.Isha)
                    var type = ""
                    for (j in timesList) {
                        var index = timesList.indexOf(j)
                        when (index) {
                            0 -> {
                                type = "Fajr"
                            }
                            1 -> {
                                type = "Dhuhr"
                            }
                            2 -> {
                                type = "Asr"
                            }
                            3 -> {
                                type = "Maghrib"
                            }
                            4 -> {
                                type = "Isha"
                            }

                        }
                        var l1 = Functions().getTime(j)
                        var date = Calendar.getInstance()
                        date.set(Calendar.YEAR, i.date.gregorian.date.substring(6).toInt())
                        date.set(
                            Calendar.MONTH,
                            i.date.gregorian.date.substring(3, 5).toInt() - 1
                        )
                        date.set(
                            Calendar.DAY_OF_MONTH,
                            i.date.gregorian.date.substring(0, 2).toInt()
                        )
                        var hour = (l1[0]).toInt()
                        var minut = (l1[1]).toInt()
                        date.set(Calendar.HOUR_OF_DAY, hour)
                        date.set(Calendar.MINUTE, minut)
                        date.set(Calendar.SECOND, 0)
                        if (date.time.time >= Date().time) {
                            Log.d(TAG, "loadDataMilliesecondData:  ${date.time.time}")
                            db.add(
                                AzonEntity(
                                    date = SimpleDateFormat("dd/M/yyyy").format(date.time.time),
                                    hour = (l1[0]).toInt(),
                                    minut = (l1[1]).toInt(),
                                    alarmSeconds = date.time.time,
                                    type = type
                                )
                            )
                        }
                    }
                }

                azonAdapter = AzonAdapter(requireContext(), db.getAll() as ArrayList<AzonEntity>)
                binding.azonRv.adapter = azonAdapter
                startAlert()
            }
            hideLoading()
        } else {
            hideLoading()
            Toast.makeText(
                requireContext(),
                "Please check internet connection !",
                Toast.LENGTH_SHORT
            ).show()
            azonAdapter = AzonAdapter(requireContext(), db.getAll() as ArrayList<AzonEntity>)
            binding.azonRv.adapter = azonAdapter
            startAlert()
        }
    }

    fun showLoading() {
        binding.spinKit.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.spinKit.visibility = View.INVISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startAlert() {

        val intent = Intent(requireContext(), MyBroadcastReceiver::class.java)
        var important = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var not = NotificationChannel("id", "name", important)
            not.description = "description"
            var manager = getSystemService(
                requireContext(),
                NotificationManager::class.java
            ) as NotificationManager
            manager.createNotificationChannel(not)
            intent.removeFlags(0)
        }
        var request = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().enqueue(request)
        WorkManager.getInstance().getWorkInfoByIdLiveData(request.id)
            .observe(viewLifecycleOwner, {

            })
        SharedPreference.getInstance(requireContext()).hasAlarm = true

    }

    private fun loadTest() {
        var db = AppDataBase.getInstance(requireContext()).dao()
        db.deleteAll()
        for (i in 1..10) {
            var date = Calendar.getInstance()
            date.set(Calendar.YEAR, 2021)
            date.set(
                Calendar.MONTH, 11
            )
            date.set(
                Calendar.DAY_OF_MONTH, 20
            )
            date.set(Calendar.HOUR_OF_DAY, 24)
            date.set(Calendar.MINUTE, 10 + i)
            date.set(Calendar.SECOND, 0)
            db.add(
                AzonEntity(
                    hour = 21,
                    date = "20-12-2021",
                    minut = (41),
                    type = "",
                    alarmSeconds = date.time.time
                )
            )
            Log.d(TAG, "loadTestMil:${date.time.time} ")
        }
    }

}