package com.kushal.kotlinuniversalvideoplayer.activities

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.adapters.FoldersAdapter
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.model.VideoFolderModel
import java.lang.Boolean
import java.util.*

class VideoFolderListActivity : AppCompatActivity() {

    lateinit var mContext : Context
    lateinit var top_bar : LinearLayout
    lateinit var iv_back : ImageView
    lateinit var mainlay : RelativeLayout
    lateinit var tv_title : TextView
    private var folders: List<VideoFolderModel> = ArrayList<VideoFolderModel>()

    companion object {
        lateinit var rv_folders : RecyclerView
        lateinit var foldersAdapter : FoldersAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_folder_list)

        mContext = this;
        init()
        resize()
    }

    private fun resize() {
        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(top_bar, 1080, 150)
        HelperResizer.setSize(iv_back, 90, 90, true)
    }

    private fun init() {

        top_bar = findViewById(R.id.top_bar)
        iv_back = findViewById(R.id.iv_back)
        mainlay = findViewById(R.id.mainlay)
        tv_title = findViewById(R.id.tv_title)
        rv_folders = findViewById(R.id.rv_folders)
        folders = SplashActivity.folders

        foldersAdapter = FoldersAdapter(mContext, folders)
        rv_folders.layoutManager = GridLayoutManager(mContext, 2)

        rv_folders.adapter = foldersAdapter

        iv_back.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            SplashActivity.AsyncLoadVideoAndFolders().execute(Boolean.TRUE)
        } catch (unused: Exception) {
            unused.printStackTrace()
        }
    }

}