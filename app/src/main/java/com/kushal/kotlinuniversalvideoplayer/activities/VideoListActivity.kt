package com.kushal.kotlinuniversalvideoplayer.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.adapters.VideosAdapter
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.helpers.VideosAndFoldersUtility
import com.kushal.kotlinuniversalvideoplayer.interfaces.OnDeleteClick
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel
import java.io.File
import java.util.*

class VideoListActivity : AppCompatActivity(), OnDeleteClick {

    lateinit var mContext: Context
    lateinit var iv_back: ImageView
    lateinit var ic_search: ImageView
    var live = false
    var utility: VideosAndFoldersUtility? = null
    var videos: List<VideosModel> = ArrayList<VideosModel>()
    lateinit var videosAdapter: VideosAdapter
    lateinit var top_bar: LinearLayout
    lateinit var search_bg: LinearLayout
    lateinit var icon_bg: LinearLayout
    lateinit var et_search: EditText
    lateinit var rvVideos: RecyclerView

    companion object {

        var videospath = ArrayList<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        mContext = this
        init()
        resize()
    }

    private fun init() {

        iv_back = findViewById(R.id.iv_back)
        top_bar = findViewById(R.id.top_bar)
        rvVideos = findViewById(R.id.rv_videos)
        et_search = findViewById(R.id.et_search)
        search_bg = findViewById(R.id.search_bg)
        icon_bg = findViewById(R.id.icon_bg)
        ic_search = findViewById(R.id.ic_search)
        utility = VideosAndFoldersUtility(mContext)

        val extras = intent.extras

        if (extras!!.getString("key").equals("folder")) {

            val string = extras.getString("folder_path")
            title = extras.getString("folder_name")
            videos = utility!!.fetchVideosByFolder(string)

        } else {

            videos = SplashActivity.videos
            title = "All Videos"

        }

        videosAdapter = VideosAdapter(mContext, videos, this)
        rvVideos.layoutManager = GridLayoutManager(mContext, 2)
        rvVideos.adapter = videosAdapter

        live = true

        for (i in videos.indices) {
            videospath.add(videos[i].getData()!!)
        }

        et_search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }

        })

        iv_back.setOnClickListener { onBackPressed() }

    }

    fun filter(text: String) {

        val temp: MutableList<VideosModel> = ArrayList<VideosModel>()

        for (d in videos) {

            if (d.getName()!!.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d)
            }
        }

        videosAdapter.updateList(temp)

    }

    private fun resize() {

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(top_bar, 1080, 150)
        HelperResizer.setSize(iv_back, 90, 90, true)
        HelperResizer.setSize(search_bg, 1000, 100)
        HelperResizer.setMargin(search_bg, 0, 60, 0, 0)
        HelperResizer.setSize(et_search, 900, 100)
        HelperResizer.setSize(ic_search, 64, 66)
        HelperResizer.setSize(icon_bg, 100, 102)

    }

    override fun onVideoDelete(path: String, pos: Int) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.delete_popup_layout)

        val popup = dialog.findViewById<LinearLayout>(R.id.popup)
        val yes = dialog.findViewById<ImageView>(R.id.yes)
        val no = dialog.findViewById<ImageView>(R.id.no)

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(popup, 869, 520)
        HelperResizer.setSize(yes, 247, 102)
        HelperResizer.setSize(no, 247, 102)

        yes.setOnClickListener {

            dialog.dismiss()

            val f = File(path)
            f.delete()

            MediaScannerConnection.scanFile(mContext, arrayOf(path), null) { _, _ -> runOnUiThread {
                onBackPressed() }
            }
            Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_LONG).show()

        }

        no.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }
}