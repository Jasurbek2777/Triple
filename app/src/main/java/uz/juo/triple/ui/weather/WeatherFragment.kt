package uz.juo.triple.ui.weather

import android.Manifest
import android.content.ContentValues
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.coroutines.launch
import uz.juo.triple.R
import uz.juo.triple.adapters.WeatherAdapter
import uz.juo.triple.databinding.FragmentWeatherBinding
import uz.juo.triple.models.weather.WeatherData
import uz.juo.triple.retrofit.ApiClient
import uz.juo.triple.utils.Functions
import uz.juo.triple.utils.NetworkHelper
import kotlin.math.roundToInt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WeatherFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    private val TAG = "WeatherFragment"
    lateinit var binding: FragmentWeatherBinding
    private var param1: String? = null
    private var param2: String? = null
    var LAT = 41.311081
    var LONG = 69.240562
    lateinit var weatherAdapter: WeatherAdapter
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
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        MapKitFactory.initialize(requireContext())
        var mapkit = MapKitFactory.getInstance()
        mapView = binding.mapview
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true;
        setDefoultLocation()
        binding.save.setOnClickListener {
            showLoading()
            if (NetworkHelper(requireContext()).isNetworkConnected()) {
                binding.save.isClickable = false
                var a = mapView!!.mapWindow.map.cameraPosition.target
                loadData(a.latitude, a.longitude)
            } else {

                Toast.makeText(
                    requireContext(),
                    "Please check internet connection !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.getCurrentLocation.setOnClickListener {
            Dexter.withContext(context)
                .withPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                getUserLocation()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    return@withErrorListener
                }
                .check()
        }
        return binding.root
    }

    fun showLoading() {
        binding.spinKit.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.spinKit.visibility = View.INVISIBLE
    }
    private fun loadData(lat: Double, long: Double) {
        lifecycleScope.launch {
            hideLoading()
            var data = ApiClient.apiService.getWeather(lat = lat, lon = long)
            Log.d(TAG, "onCreateViewWeather: $data")
            val cityName = Functions().getLocationName(requireContext(), lat, long)
            showBottomBar(data, cityName)
        }
    }

    private fun showBottomBar(data: WeatherData, cityName: String) {

        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.weather_info)
        val name = bottomSheetDialog.findViewById<TextView>(R.id.city_name)
        val temp = bottomSheetDialog.findViewById<TextView>(R.id.temp)
        val desc = bottomSheetDialog.findViewById<TextView>(R.id.desc)
        val rv = bottomSheetDialog.findViewById<RecyclerView>(R.id.bottom_sheet_rv)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        weatherAdapter = WeatherAdapter(data)
        rv?.adapter = weatherAdapter
        name?.text = cityName
        temp?.text = data.current.temp.roundToInt().toString()
        desc?.text = data.current.weather[0].main
        bottomSheetDialog.show()
        binding.save.isClickable = true

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> =
            activity?.let {
                LocationServices.getSettingsClient(it).checkLocationSettings(builder.build())
            } as Task<LocationSettingsResponse>

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable: ResolvableApiException =
                                exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                activity, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.d(ContentValues.TAG, "showLocationPrompt: ")
                    }
                }
            }
        }
    }

    private fun getUserLocation() {
        showLocationPrompt()
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    Log.d(ContentValues.TAG, "getUserLocation121212:  $location")
                    try {
                        if (location != null && location.latitude > 0 && (location.longitude) > 0) {
                            cameraMoveOn(location.latitude, location.longitude)
                        } else {
                            setDefoultLocation()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "getUserLocation: ${e.message}")
        }
    }

    private fun setDefoultLocation() {
        cameraMoveOn(41.311081, 69.240562)
    }

    fun cameraMoveOn(lat: Double, long: Double) {
        try {
            mapView!!.map.move(
                CameraPosition(
                    Point(lat, long), 15.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 2F), null
            )
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext(), "No internet connection ", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }
}