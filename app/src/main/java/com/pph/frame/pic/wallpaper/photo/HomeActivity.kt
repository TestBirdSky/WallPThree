package com.pph.frame.pic.wallpaper.photo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity

import com.pph.frame.pic.wallpaper.photo.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var pickLaunch: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickLaunch = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                PhotoActivity.start(this, it)
            }
        }

        binding.setup.setOnClickListener {
            binding.drawer.open()
        }
        binding.wallpaper.setOnClickListener {
            startActivity(Intent(this, WallpaperActivity::class.java))
        }
        binding.photo.setOnClickListener {
            pickLaunch.launch(
                PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
            )

        }
        binding.privacy.setOnClickListener {
            binding.drawer.close()
            // TODO:HTTP
            startActivity(IntentEx("https://www.bing.com"))
        }
    }

    inner class IntentEx : Intent {
        public constructor( http: String){
            action = ACTION_VIEW
            setData(Uri.parse(http))
        }
    }

}