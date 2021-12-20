package uz.juo.triple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import uz.juo.triple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller = findNavController(R.id.my_nav_host_fragment)
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            bottomNavListener(item)
            true
        }

    }

    private fun bottomNavListener(item: MenuItem) {
        when (item.itemId) {
            R.id.news_item -> {
                if (controller.currentDestination?.id != R.id.newsFragment){
                    controller.popBackStack()
                }
            }
            R.id.weather_item -> {
                if (controller.currentDestination?.id != R.id.weatherFragment) {
                    if (controller.currentDestination?.id != R.id.newsFragment) {
                        controller.popBackStack()
                    }
                    controller.navigate(R.id.weatherFragment)
                }
            }
            R.id.azon_item -> {
                if (controller.currentDestination?.id != R.id.azonFragment) {
                    if (controller.currentDestination?.id != R.id.newsFragment) {
                        controller.popBackStack()
                    }
                    controller.navigate(R.id.azonFragment)
                }
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.newsFragment).navigateUp()
    }

}