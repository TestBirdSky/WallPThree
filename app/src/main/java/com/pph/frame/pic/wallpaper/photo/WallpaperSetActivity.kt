package com.pph.frame.pic.wallpaper.photo

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pph.frame.pic.wallpaper.photo.databinding.ActivityHomeBinding

import com.pph.frame.pic.wallpaper.photo.databinding.ActivityMainBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ActivityWallpaperBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ActivityWallpaperSetBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ItemWallpaperBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WallpaperSetActivity : AppCompatActivity() {

    companion object {

        var resId = 0;
        fun start(mContext: Context, id: Int) {
            resId = id;
            mContext.startActivity(Intent(mContext, WallpaperSetActivity::class.java))
        }
    }

    private lateinit var binding: ActivityWallpaperSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallpaperSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.img.setImageResource(resId)
        binding.back.setOnClickListener {
            finish()
        }
        binding.likeBtn.setOnClickListener {
            isLike = !isLike;
            if (isLike) {
                App.addLike(System.currentTimeMillis(), resId)
                binding.likeBtn.setImageResource(R.mipmap.ic_set_like_yellow)
            } else {
                App.removeLike(resId)
                binding.likeBtn.setImageResource(R.mipmap.ic_set_like_gray)
            }
        }
        binding.apply.setOnClickListener {
            binding.bottomMenu.visibility = View.GONE
            binding.viewDialog.visibility = View.VISIBLE
        }
        binding.save.setOnClickListener {
            lifecycleScope.launch {
                App.saveBitmap(App.app, BitmapFactory.decodeResource(resources,resId),{
                    Snackbar.make(this@WallpaperSetActivity.window.decorView,"Successfully saved", Snackbar.LENGTH_SHORT).show()
                },{})
            }
        }

        binding.lock.setOnClickListener {
            disDialog()
            WallpaperManager.getInstance(this).setResource(resId,2)
            Snackbar.make(this.window.decorView,"Wallpaper setting successful", Snackbar.LENGTH_SHORT).show()
        }
        binding.home.setOnClickListener {
            disDialog()
            WallpaperManager.getInstance(this).setResource(resId,1)
            Snackbar.make(this.window.decorView,"Wallpaper setting successful", Snackbar.LENGTH_SHORT).show()
        }
        binding.both.setOnClickListener {
            disDialog()
            WallpaperManager.getInstance(this).setResource(resId)
            Snackbar.make(this.window.decorView,"Wallpaper setting successful", Snackbar.LENGTH_SHORT).show()
        }
        binding.cancel.setOnClickListener { disDialog() }


        load();
    }


    fun disDialog() {
        binding.bottomMenu.visibility = View.VISIBLE
        binding.viewDialog.visibility = View.GONE
    }

    var isLike: Boolean = false
    fun load() {
        isLike = App.isLike(resId)
        if (isLike) {
            binding.likeBtn.setImageResource(R.mipmap.ic_set_like_yellow)
        } else {
            binding.likeBtn.setImageResource(R.mipmap.ic_set_like_gray)
        }


    }


}