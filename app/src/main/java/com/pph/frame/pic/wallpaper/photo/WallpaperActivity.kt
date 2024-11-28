package com.pph.frame.pic.wallpaper.photo

import android.os.Bundle
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pph.frame.pic.wallpaper.photo.databinding.ActivityHomeBinding

import com.pph.frame.pic.wallpaper.photo.databinding.ActivityMainBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ActivityWallpaperBinding
import com.pph.frame.pic.wallpaper.photo.databinding.ItemWallpaperBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WallpaperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWallpaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.back.setOnClickListener {

        }
        binding.all.setOnClickListener {
            if (curIndex.not()){
                curIndex = true
                binding.all.setBackgroundResource(R.drawable.btn_gr)
                binding.like.setBackgroundResource(R.drawable.btn_gray)
                left();
            }

        }
        binding.like.setOnClickListener {
            if (curIndex){
                curIndex = false
                binding.like.setBackgroundResource(R.drawable.btn_gr)
                binding.all.setBackgroundResource(R.drawable.btn_gray)
                right();
            }
        }


        initRv();
        left()
    }


    var curIndex = true;

    fun left(){
        itemList.clear()
        itemList.addAll(sourceArray.toMutableList())
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (curIndex.not()){
            right();
        }
    }
    fun right(){
       val list : MutableList<Info> =  App.getLikeList();
       val ls2= list.map {
            it.res
        }
        itemList.clear()
        itemList.addAll(ls2)
        adapter.notifyDataSetChanged()
    }


    val sourceArray = arrayOf(R.mipmap.fx_0,R.mipmap.fx_1,R.mipmap.fx_2,R.mipmap.fx_3,R.mipmap.fx_4,R.mipmap.fx_5,R.mipmap.fx_6,R.mipmap.fx_7,R.mipmap.fx_8,R.mipmap.fx_9,
        R.mipmap.fx_10,R.mipmap.fx_11,R.mipmap.fx_12,R.mipmap.fx_13,R.mipmap.fx_14,R.mipmap.fx_15,R.mipmap.fx_16,R.mipmap.fx_17)


    fun initRv(){
        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = GridLayoutManager(this,3);
        binding.rv.adapter = adapter
    }

    var itemList:MutableList<Int> = mutableListOf();

    inner class VH public constructor(val bin:ItemWallpaperBinding) : RecyclerView.ViewHolder(bin.root)
    val adapter = object :RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemWallpaperBinding.inflate(layoutInflater,parent,false))
        }
        override fun getItemCount(): Int {
            return itemList.size
        }
        override fun onBindViewHolder(vh: VH, position: Int) {
            val data = itemList.get(position)

            vh.bin.img.setBackgroundResource(data)

            vh.itemView.setOnClickListener {

                WallpaperSetActivity.start(this@WallpaperActivity,data)
            }
        }
    }



}