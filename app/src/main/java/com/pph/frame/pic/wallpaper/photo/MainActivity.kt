package com.pph.frame.pic.wallpaper.photo

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.pph.frame.pic.wallpaper.photo.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onBackPressed() {
    }


    override fun onResume() {
        super.onResume()
        init();
    }


    var job: Job?=null
    fun init(){
        job?.cancel()
        job = lifecycleScope.launch {
            repeat(10) {
                delay(110)
            }
            startActivity(Intent(this@MainActivity,HomeActivity::class.java))
            finish()
        }

    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        job?.cancel()
        job=null;
        super.onDestroy()
    }
}