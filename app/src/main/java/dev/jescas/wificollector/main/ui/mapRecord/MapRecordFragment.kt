package dev.jescas.wificollector.main.ui.mapRecord

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jescas.wificollector.databinding.FragmentMapRecordBinding
import dev.jescas.wificollector.main.data.WifiItem
import dev.jescas.wificollector.main.ui.main.MainViewModel
import dev.jescas.wificollector.main.ui.main.WifiItemsAdapter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date

class MapRecordFragment : Fragment() {
    private lateinit var binding: FragmentMapRecordBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val wifiList = mutableListOf<WifiItem>()
    private lateinit var wifiAdapter: WifiItemsAdapter
    private var counter: Int = 0
    private var mapRealWidth = 14f  // Replace with your real map width
    private var mapRealHeight = 11f  // Replace with your real map height
    private val offsetX = 7.85
    private val offsetY = 7.5
    private var isScanning: Boolean = false
    private lateinit var csvFilename: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapRecordBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        wifiAdapter = WifiItemsAdapter(wifiList)

        // Start wifi
        mainViewModel.startWifi()
        mainViewModel.wifiItems.observe(viewLifecycleOwner) { it ->
            // Update wifiList correctly
            wifiList.addAll(it)
            wifiAdapter.notifyDataSetChanged()
            binding.btnScan.isEnabled = true
            binding.pbLoading.visibility = View.GONE
        }

        // Set tree
        with(binding) {
            ivMap.viewTreeObserver.addOnGlobalLayoutListener {
                val imageWidth = ivMap.measuredWidth
                val imageHeight = ivMap.measuredHeight

                // Calculate scaling factors
                val scaleX = imageWidth / mapRealWidth
                val scaleY = imageHeight / mapRealHeight

                // Add TextWatchers
                layPoses.etXPos.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        updateCursorPosition(scaleX, scaleY)
                    }
                })

                layPoses.etXPos.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        updateCursorPosition(scaleX, scaleY)
                    }
                })
            }
        }

        // Set listeners
        with(binding) {
            rvWifi.adapter = wifiAdapter
            rvWifi.layoutManager = LinearLayoutManager(this@MapRecordFragment.activity)

            btnStart.setOnClickListener {
                startScanning()
            }

            btnStop.setOnClickListener {
                stopScanning()
            }

            btnCapture.setOnClickListener {
                captureScanning()
            }

            btnScan.setOnClickListener {
                btnScan.isEnabled = false
                wifiList.clear()
                performScan()
            }
        }


        return binding.root
    }

    private fun updateCursorPosition(scaleX: Float, scaleY: Float) {
        // Get the input values for X and Y
        val xInput = binding.layPoses.etXPos.text.toString().toFloatOrNull()
        val yInput = binding.layPoses.etXPos.text.toString().toFloatOrNull()

        // Check if inputs are valid
        if (xInput != null && yInput != null) {
            // Calculate the position in the ImageView
            val cursorX = (xInput + offsetX) * scaleX
            val cursorY = (yInput + offsetY) * scaleY

            // Update the position of the cursor (assuming ivLoc is an ImageView or similar)
            binding.ivLoc.x = (cursorX - binding.ivLoc.width / 2).toFloat() // Center the cursor
            binding.ivLoc.y = (cursorY - binding.ivLoc.height / 2).toFloat() // Center the cursor

            // Optionally, invalidate the view if necessary
            binding.ivLoc.invalidate()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun captureScanning() {
        val xPos = binding.layPoses.etXPos.text.toString().toFloatOrNull()
        val yPos = binding.layPoses.etXPos.text.toString().toFloatOrNull()

        if (xPos == null || yPos == null) {
            Toast.makeText(
                this@MapRecordFragment.context,
                "Invalid position coordinates",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Show confirmation dialog
        AlertDialog.Builder(this@MapRecordFragment.requireActivity())
            .setTitle("Save Fingerprint")
            .setMessage("Do you want to save the Wi-Fi data with coordinates X:$xPos, Y:$yPos?")
            .setPositiveButton("OK") { _, _ ->
                val directory =
                    this@MapRecordFragment.activity?.baseContext?.getExternalFilesDir(null)
                // Append to CSV file a row with XY RSSI SSID
                try {
                    // Append to CSV file
                    val outputStream =
                        FileOutputStream("$directory/$csvFilename", true) // 'true' for append mode
                    val writer = OutputStreamWriter(outputStream)

                    // Write the data
                    wifiList.forEach { it ->
                        writer.append("${counter},${it.ssid},${it.bssid},${it.frequency},${it.capabilities},${it.level},$xPos,$yPos\n")
                    }
                    writer.flush()
                    writer.close()

                    wifiList.clear()
                    wifiAdapter.notifyDataSetChanged();

                    Toast.makeText(
                        this@MapRecordFragment.context,
                        "Saving fingerprint .. ",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@MapRecordFragment.context,
                        "Error saving to file: ${e.message}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.e("MainActivity", "Error saving to file", e)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // User canceled the action
                Toast.makeText(
                    this@MapRecordFragment.context,
                    "Action canceled",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }


    private fun startScanning() {
        counter = 0
        isScanning = true
        with(binding) {
            layPoses.root.visibility = View.VISIBLE
            btnStart.visibility = View.GONE
            btnStop.visibility = View.VISIBLE
            btnCapture.visibility = View.VISIBLE
            btnScan.visibility = View.VISIBLE
        }
        Toast.makeText(
            this@MapRecordFragment.context,
            "Starting Wi-Fi scan database...",
            Toast.LENGTH_SHORT
        ).show()
        csvFilename = "wifi_scan_${System.currentTimeMillis()}.csv"

        val sdf: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val currentDateAndTime: String = sdf.format(Date())
        csvFilename = "wifiList_$currentDateAndTime.csv"
    }

    private fun stopScanning() {
        isScanning = false
        with(binding) {
            btnStop.visibility = View.GONE
            btnStart.visibility = View.VISIBLE
            btnCapture.visibility = View.GONE
            btnScan.visibility = View.GONE
            layPoses.root.visibility = View.GONE
        }
        Toast.makeText(this@MapRecordFragment.context, "Stopping Wi-Fi scan.", Toast.LENGTH_SHORT)
            .show()
        Toast.makeText(
            this@MapRecordFragment.context,
            "Records saved in $csvFilename",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun performScan() {
        binding.pbLoading.visibility = View.VISIBLE
        mainViewModel.triggerScanNetwork()
    }

}