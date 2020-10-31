package com.kushal.kotlinuniversalvideoplayer.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.activities.VideoPlayerActivity
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.helpers.TheUtility
import com.kushal.kotlinuniversalvideoplayer.interfaces.OnDeleteClick
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel
import com.makeramen.roundedimageview.RoundedImageView
import java.io.File
import java.io.Serializable

public class VideosAdapter(
    mContext: Context,
    videos: List<VideosModel>,
    deleteClick: OnDeleteClick
) : RecyclerView.Adapter<VideosAdapter.Holder>() {

    var mContext: Context = mContext
    var videos: List<VideosModel> = videos
    var deleteClick: OnDeleteClick = deleteClick
    var temppos = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(holder.ivVideoThumbnail, 444, 355, true)
        HelperResizer.setMargin(holder.ivVideoThumbnail, 4, 3, 4, 4)
        HelperResizer.setSize(holder.tab_bg, 450, 450, true)
        HelperResizer.setSize(holder.text_bg, 450, 112, true)
        HelperResizer.setSize(holder.ic_delete, 48, 64, true)
        HelperResizer.setSize(holder.rel_thumb, 245, 210, true)
        HelperResizer.setMargin(holder.rel_thumb, 20, 0, 0, 0)

        holder.ivVideoThumbnail.cornerRadius = 25.0f

        val videoModel: VideosModel = videos[position]
        holder.tvTitle.text = videoModel.getTitle()
        holder.tvDuration.text = videoModel.getDuration()
        holder.tvResolution.text = TheUtility.humanReadableByteCount(videoModel.getSize(), true)
        holder.tv_date.text= videoModel.getDateAdded()

        val fromFile = Uri.fromFile(File(videoModel.getData()))
        val videoAndView = VideoAndView()
        videoAndView.uri = fromFile
        videoAndView.imageView = holder.ivVideoThumbnail
        videoAndView.thumbnail = videoModel.getData()

        Glide.with(mContext).load(videoAndView.thumbnail).error(R.drawable.ic_video).into(
            videoAndView.imageView as RoundedImageView
        )

        holder.ivVideoThumbnail.setOnClickListener {

            temppos = position

            val intent = Intent(mContext, VideoPlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("list", videos as Serializable)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(intent)

        }

        if (position == videos.size - 1) {
            FrameLayout.LayoutParams(-1, -2)
        }

        holder.ic_delete.setOnClickListener {

            deleteClick.onVideoDelete(videoModel.getData().toString(), position)

        }

    }

    override fun getItemCount(): Int {
        return videos.size
    }

    internal class VideoAndView {
        var imageView: ImageView? = null
        var thumbnail: String? = null
        var uri: Uri? = null
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        var cvItem: LinearLayout
        var tab_bg: LinearLayout
        var text_bg:LinearLayout
        var ic_delete: ImageView
        var ivVideoThumbnail: RoundedImageView
        var rlytVideoItemLayout: RelativeLayout
        var tvDuration: TextView
        var tvResolution:TextView
        var tvTitle:TextView
        var tv_date:TextView
        var rel_thumb: RelativeLayout
        var rel_duration: RelativeLayout

        init {

            cvItem = view.findViewById(R.id.video_item)
            rel_thumb = view.findViewById(R.id.rel_thumb)
            rel_duration = view.findViewById(R.id.rel_duration)
            rlytVideoItemLayout = view.findViewById(R.id.video_item_layout)
            tvTitle = view.findViewById(R.id.tv_title)
            tvDuration = view.findViewById(R.id.tv_duration)
            tvResolution = view.findViewById(R.id.tv_resolution)
            ivVideoThumbnail = view.findViewById(R.id.iv_video_thumb)
            tv_date = view.findViewById(R.id.tv_date)
            tab_bg = view.findViewById(R.id.tab_bg)
            text_bg = view.findViewById(R.id.text_bg)
            ic_delete = view.findViewById(R.id.ic_delete)

        }

    }

    fun updateList(list: List<VideosModel>) {
        videos = list
        notifyDataSetChanged()
    }

}