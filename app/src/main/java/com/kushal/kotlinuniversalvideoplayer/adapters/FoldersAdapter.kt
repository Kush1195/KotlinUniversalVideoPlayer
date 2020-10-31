package com.kushal.kotlinuniversalvideoplayer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.activities.VideoListActivity
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.model.VideoFolderModel

class FoldersAdapter(mContext: Context, folders: List<VideoFolderModel>) :
    RecyclerView.Adapter<FoldersAdapter.Holder>() {

    var mContext: Context? = mContext
    private var folders: List<VideoFolderModel>? = folders

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(mContext).inflate(R.layout.folder_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(holder.tab_bg, 450, 450, true)
        HelperResizer.setSize(holder.folder_thumb, 244, 197, true)
        HelperResizer.setSize(holder.text_bg, 450, 112, true)

        val folderModel: VideoFolderModel = folders!![position]

        holder.tvFolderName!!.text = folderModel.getName()
        holder.tv_folder_size!!.text = "" + folderModel.getTotalVideos().toString() + " Videos"

        holder.itemView.setOnClickListener {

            val intent: Intent = Intent(mContext, VideoListActivity::class.java)
            intent.putExtra("key", "folder")
            intent.putExtra("folder_path", folderModel.getPath())
            intent.putExtra("folder_name", folderModel.getName())
            mContext!!.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return folders!!.size
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        var folder_thumb: ImageView
        var iv_devider: ImageView? = null
        var rlytFolderItemLayout: RelativeLayout? = null
        var tab_bg: LinearLayout
        var text_bg: LinearLayout
        var tvFolderName: TextView
        var tv_folder_size: TextView

        init {
            tvFolderName = view.findViewById(R.id.tv_folder_name)
            folder_thumb = view.findViewById(R.id.iv_folder_thumb)
            tab_bg = view.findViewById(R.id.tab_bg)
            tv_folder_size = view.findViewById(R.id.tv_folder_size)
            text_bg = view.findViewById(R.id.text_bg)
        }
    }

}