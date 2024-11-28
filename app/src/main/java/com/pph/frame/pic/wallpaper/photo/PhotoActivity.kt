package com.pph.frame.pic.wallpaper.photo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


import com.pph.frame.pic.wallpaper.photo.databinding.ActivityPhotoBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ItemPhotoBinding
import kotlinx.coroutines.launch

class PhotoActivity : AppCompatActivity() {

    companion object {

        lateinit var resUri :Uri;
        fun start(mContext: Context, uri: Uri) {
            resUri = uri;
            mContext.startActivity(Intent(mContext, PhotoActivity::class.java))
        }
    }

    private lateinit var binding: ActivityPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.back.setOnClickListener {

        }
        binding.img.setImageURI(resUri)
        binding.apply.setOnClickListener {


            lifecycleScope.launch {
                App.saveBitmap(App.app,  binding.layout.drawToBitmap(Bitmap.Config.RGB_565),{
                    binding.apply.visibility = View.GONE
                    binding.bottomRv.visibility = View.GONE
                    Snackbar.make(this@PhotoActivity.window.decorView,"Successfully saved", Snackbar.LENGTH_SHORT).show()
                },{})
            }

        }
        initRv()
    }

    fun initRv(){
        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        binding.rv.adapter = adapter
    }
    var currentIndex = 0
    var lastIndex = 0
    val itemList = mutableListOf<Int>(R.mipmap.wx_0,R.mipmap.wx_1,R.mipmap.wx_2,R.mipmap.wx_3,R.mipmap.wx_4,R.mipmap.wx_5,R.mipmap.wx_6,R.mipmap.wx_7,
        R.mipmap.wx_8,R.mipmap.wx_9,R.mipmap.wx_10,R.mipmap.wx_11)
    inner class VH public constructor(val bin:ItemPhotoBinding) : RecyclerView.ViewHolder(bin.root)
    val adapter = object :RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemPhotoBinding.inflate(layoutInflater,parent,false))
        }
        override fun getItemCount(): Int {
            return itemList.size
        }
        override fun onBindViewHolder(vh: VH, position: Int) {
            val data = itemList.get(position)
            vh.bin.img.setImageResource(data)

            if (lastIndex==position){
                vh.bin.frmae.setBackgroundResource(R.mipmap.ic_frmae)
            }else{
                vh.bin.frmae.setBackgroundResource(0)
            }

            vh.itemView.setOnClickListener {
                notifyItemChanged(lastIndex)
                notifyItemChanged(position)
                lastIndex = position
                binding.frame.setBackgroundResource(data)
            }
        }
    }



}