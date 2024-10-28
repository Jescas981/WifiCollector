package dev.jescas.wificollector.main.ui.settings

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.jescas.wificollector.databinding.FragmentMapRecordBinding
import dev.jescas.wificollector.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var dxDrag = 0.0f
    private var dyDrag = 0.0f
    private var mapScaleX = 0.0f
    private var mapScaleY = 0.0f

    private val imageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { it ->
            it.let {
                Glide.with(this).load(it).into(binding.ivMap)
                with(binding) {
                    mapPixelsX.setText(ivMap.measuredWidth.toString())
                    mapPixelsY.setText(ivMap.measuredHeight.toString())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)


        binding.flMap.setOnClickListener {
            imageContract.launch("image/*")
        }

        binding.mapSizeX.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                updateScale()
            }

        })

        binding.mapSizeX.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                updateScale()
            }

        })

        binding.mapSizeY.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                updateScale()
            }

        })

        binding.btnNext.setOnClickListener {
            val mapPixelsX = binding.mapPixelsX.text.toString().toFloatOrNull()
            val mapPixelsY = binding.mapPixelsY.text.toString().toFloatOrNull()
            val mapSizeX = binding.mapSizeX.text.toString().toFloatOrNull()
            val mapSizeY = binding.mapSizeY.text.toString().toFloatOrNull()

            if (mapPixelsX != null && mapPixelsY != null && mapSizeX != null && mapSizeY != null) {
                binding.layForm.visibility = View.GONE
                binding.ivMapPlaceholder.visibility = View.GONE
                // Enable offset
                binding.layOffset.visibility = View.VISIBLE
                binding.ivLoc.visibility = View.VISIBLE
                binding.ivMap.setOnClickListener(null)
            } else {
                Toast.makeText(this.context, "Please, fill the form", Toast.LENGTH_LONG).show()
            }
        }

        binding.ivLoc.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dxDrag = view.x - event.rawX
                    dyDrag = view.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    view.animate().x(event.rawX + dxDrag).y(event.rawY + dyDrag).setDuration(0)
                        .start()
                }

                MotionEvent.ACTION_UP -> {
                    // Remove padding
                    val padx = (binding.ivMap.width - binding.ivMap.measuredWidth) / 2
                    val pady = (binding.ivMap.height - binding.ivMap.measuredHeight) / 2
                    // Compute meters


                    val ox = (view.x) * mapScaleX
                    val oy = (view.y) * mapScaleY

                    binding.offsetX.setText(ox.toString())
                    binding.offsetY.setText(oy.toString())
                }
            }
            true
        }

        return binding.root
    }

    private fun clear() {}

    private fun updateScale() {
        val mapPixelsX = binding.mapPixelsX.text.toString().toFloatOrNull()
        val mapPixelsY = binding.mapPixelsY.text.toString().toFloatOrNull()
        val mapSizeX = binding.mapSizeX.text.toString().toFloatOrNull()
        val mapSizeY = binding.mapSizeY.text.toString().toFloatOrNull()

        if (mapPixelsX != null && mapPixelsY != null && mapSizeX != null && mapSizeY != null) {
            // Calculate scale as pixels per unit of map size
            mapScaleX = mapSizeX / mapPixelsX
            mapScaleY = mapSizeY / mapPixelsY

            binding.scalePixelsX.setText(String.format("%.2f", mapScaleX))
            binding.scalePixelsY.setText(String.format("%.2f", mapScaleY))
        } else {
            // Handle invalid or missing data cases if needed
            binding.scalePixelsX.setText("")
            binding.scalePixelsY.setText("")
        }
    }
}