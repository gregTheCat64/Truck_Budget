package ru.javacat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.routes -> findNavController(R.id.fragment_container).navigate(R.id.routeListFragment)
                R.id.orders -> findNavController(R.id.fragment_container).navigate(R.id.orderListFragment)
                R.id.partners -> findNavController(R.id.fragment_container).navigate(R.id.customerListFragment)
                R.id.home -> findNavController(R.id.fragment_container).navigate(R.id.homeFragment)
                else -> {}
            }
            true
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.title = "TruckBudget"
        setContentView(binding.root)
    }
    private fun replaceFragment(fragment: Fragment){
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
    fun setBottomNavVisibility(visibility: Int) {
        binding.bottomNav.visibility = visibility
    }
}