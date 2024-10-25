package dev.jescas.wificollector.main.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import dev.jescas.wificollector.R
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jescas.wificollector.databinding.ActivityMainBinding
import dev.jescas.wificollector.main.data.WifiItem
import dev.jescas.wificollector.main.ui.mapRecord.MapRecordFragment
import dev.jescas.wificollector.main.ui.settings.SettingsFragment
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    // Handle permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                // Enable wifi
                Toast.makeText(
                    this,
                    "Permission granted",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Required permissions denied. Cannot scan for Wi-Fi networks.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Request permissions
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val fragment = SettingsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, fragment)
                transaction.commit()
                true
            }

            R.id.mapRecord -> {
                val fragment = MapRecordFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, fragment)
                transaction.commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
