package com.kushal.kotlinuniversalvideoplayer.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.GestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.activities.SplashActivity.Companion.bassStrength
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.helpers.Util
import com.kushal.kotlinuniversalvideoplayer.helpers.Util.isOrientatinClicked
import com.kushal.kotlinuniversalvideoplayer.helpers.Util.speed
import com.kushal.kotlinuniversalvideoplayer.helpers.VideoGestureListener
import com.kushal.kotlinuniversalvideoplayer.helpers.ViewGestureListener
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel
import com.kushal.kotlinuniversalvideoplayer.service.FloatVideoService
import java.lang.reflect.Method
import java.util.*
import kotlin.math.roundToInt

class VideoPlayerActivity : AppCompatActivity(), VideoGestureListener {

    lateinit var mContext: Context
    lateinit var playPause: ImageView
    lateinit var next: ImageView
    lateinit var previous: ImageView
    lateinit var rotate: ImageView
    lateinit var mute: ImageView
    lateinit var cutVideo: ImageView
    lateinit var popup: ImageView
    lateinit var back: ImageView
    lateinit var settings: ImageView
    lateinit var lock: ImageView
    lateinit var unlock: ImageView
    lateinit var seekbar: SeekBar
    lateinit var timer: Timer
    lateinit var temp: Pair<String, String>
    lateinit var txtTime1: TextView
    lateinit var txtTime2: TextView
    lateinit var playSpeed: TextView
    lateinit var speed0_5X: TextView
    lateinit var speed0_75X: TextView
    lateinit var speed1_0X: TextView
    lateinit var speed1_25X: TextView
    lateinit var speed1_5X: TextView
    lateinit var videoname: TextView
    lateinit var llleft: LinearLayout
    lateinit var llright: LinearLayout
    lateinit var llbottom: LinearLayout
    lateinit var rltop: LinearLayout
    lateinit var btm_lay: LinearLayout
    lateinit var rlTouch: RelativeLayout
    lateinit var lltopp: RelativeLayout
    var mHandler: Handler = Handler()
    var isMute = false
    var isLocked = false
    var llSpeed: LinearLayout? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var gestureDetector: GestureDetector
    var cut_flag: Int = 0
    var x: Int = 0

    companion object {

        lateinit var videoView: VideoView
        lateinit var video: VideosModel
        var videoIndex: Int = 0
        var mMaxVolume: Int = 0
        var durationInMilliSec: Int = 0
        lateinit var videoList: ArrayList<VideosModel>
        var mAudioManager: AudioManager? = null
        var mCurBrightness: Float = -1.0f
        var PROGRESS_SEEK: Long = 800
        var mCurVolume: Int = -1
        var mCenterImage: ImageView? = null
        var mCenterLayout: LinearLayout? = null
        var mCenterProgress: ProgressBar? = null
        lateinit var txtPer: TextView
        lateinit var mediaPlayer: MediaPlayer

    }

    var seekrunnable: Runnable = object : Runnable {
        override fun run() {
            if (videoView != null && videoView.isPlaying) {
                seekbar.max = videoView.duration
                val currentPosition = videoView.currentPosition
                seekbar.progress = currentPosition
                mHandler.postDelayed(this, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player_new)

        mContext = this

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CCC", "onCreate: $e")
            }
        }

        init()
        playVideo(video.getData()!!)
        clickEvents()
        resize()
    }

    private fun resize() {

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(llleft, 123, 266)
        HelperResizer.setSize(llright, 123, 266)
        HelperResizer.setSize(rotate, 73, 72)
        HelperResizer.setSize(mute, 73, 72)
        HelperResizer.setSize(cutVideo, 73, 72)
        HelperResizer.setSize(popup, 73, 72)
        HelperResizer.setSize(btm_lay, 1080, 130)
        HelperResizer.setSize(previous, 89, 60)
        HelperResizer.setSize(next, 89, 60)
        HelperResizer.setSize(playPause, 90, 90, true)

    }

    @SuppressLint("WrongConstant")
    private fun init() {

        sharedPreferences = getSharedPreferences("PREF", MODE_PRIVATE)

        if (isOrientatinClicked) {
            video = videoList[videoIndex]
            isOrientatinClicked = false
        } else {
            val extras = intent.extras
            videoList = (extras!!.getSerializable("list") as ArrayList<VideosModel>)
            videoIndex = extras.getInt("index")
            video = videoList[videoIndex]
        }

        videoView = findViewById(R.id.videoview)
        btm_lay = findViewById(R.id.btm_lay)
        playPause = findViewById(R.id.playPause)
        next = findViewById(R.id.next)
        previous = findViewById(R.id.previous)
        seekbar = findViewById(R.id.seekbar)
        txtTime1 = findViewById(R.id.txtTime1)
        txtTime2 = findViewById(R.id.txtTime2)
        playSpeed = findViewById(R.id.playSpeed)
        rotate = findViewById(R.id.rotate)
        mute = findViewById(R.id.mute)
        cutVideo = findViewById(R.id.cutVideo)
        popup = findViewById(R.id.popup)
        llSpeed = findViewById(R.id.llSpeed)
        speed0_5X = findViewById(R.id.speed0_5X)
        speed0_75X = findViewById(R.id.speed0_75X)
        speed1_0X = findViewById(R.id.speed1_0X)
        speed1_25X = findViewById(R.id.speed1_25X)
        speed1_5X = findViewById(R.id.speed1_5X)
        videoname = findViewById(R.id.videoname)
        back = findViewById(R.id.back)
        settings = findViewById(R.id.settings)
        lock = findViewById(R.id.lock)
        rltop = findViewById(R.id.rltop)
        llbottom = findViewById(R.id.llbottom)
        llleft = findViewById(R.id.llleft)
        llright = findViewById(R.id.llright)
        unlock = findViewById(R.id.unlock)
        rlTouch = findViewById(R.id.rlTouch)
        txtPer = findViewById(R.id.txtPer)
        lltopp = findViewById(R.id.lltopp)
        mCenterLayout = findViewById(R.id.layout_center)
        mCenterImage = findViewById(R.id.image_center_bg)
        mCenterProgress = findViewById(R.id.progress_center)

        mAudioManager = getSystemService("audio") as AudioManager
        mMaxVolume = mAudioManager!!.getStreamMaxVolume(3)
        mAudioManager!!.setStreamMute(3, false)

        videoname.text = video.getTitle()
        mCenterLayout!!.visibility = View.GONE

        videoView.setOnCompletionListener {

            videoView.start()
            playPause.setImageResource(R.drawable.pause)

            setTime()
        }

        videoView.setOnPreparedListener {

            mediaPlayer = it

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

            seekbar.progress = 0
            seekbar.max = videoView.duration

            videoView.start()
            playPause.setImageResource(R.drawable.pause)

            val pos: Int = Util.stopVideoPos
            val path1 = Util.stopVideoPath

            if (path1 == null) {

            } else {
                if (path1 == video.getData()) {
                    videoView.requestFocus()
                    videoView.seekTo(pos)
                }
            }

            if (videoView.isPlaying) {

                try {

                    videoView.pause()
                    mediaPlayer.pause()

                    videoView.start()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            videoView.setOnTouchListener { _, motionEvent ->

                if (motionEvent.action === 1) {
                    mCurVolume = -1
                    mCurBrightness = -1.0f
                    mCenterLayout!!.visibility = View.GONE
                }

                val gesturedetector = gestureDetector
                gesturedetector.onTouchEvent(motionEvent)
                true
            }

            popup.setOnClickListener {

                videoView.pause()
                val currentPosition = (videoView.currentPosition)
                Util.stopVideoPos = currentPosition
                Util.stopVideoPath = video.getData()

                val i = Intent(mContext, FloatVideoService::class.java)
                i.action = "StartService"
                val currentPOs: Int = videoView.currentPosition
                i.putExtra("path", video.getData())
                i.putExtra("pos", currentPOs)
                i.putExtra("index", videoIndex)
                i.putExtra("list", videoList)
                startService(i)
                onBackPressed()

            }

            setTime()

        }
    }

    private fun playVideo(path: String) {

        val uri: Uri = Uri.parse(path)
        videoView.setVideoURI(uri)

    }

    private fun clickEvents() {

        next.setOnClickListener {

            if (videoIndex == (videoList.size - 1)) {
                Toast.makeText(mContext, "Last Video", Toast.LENGTH_SHORT).show()
            } else {

                videoIndex = videoIndex.plus(1)
                video = videoList[videoIndex]

                videoView.setVideoURI(Uri.parse(video.getData()))
                videoView.requestFocus()
                videoView.start()

            }

        }

        previous.setOnClickListener {

            if (videoIndex == 0) {
                Toast.makeText(mContext, "First Video", Toast.LENGTH_SHORT).show()
            } else {

                videoIndex = videoIndex.minus(1)
                video = videoList[videoIndex]

                videoView.setVideoURI(Uri.parse(video.getData()))
                videoView.requestFocus()
                videoView.start()

            }

        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mHandler.removeCallbacks(seekrunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (!videoView.isPlaying) {
                    videoView.start()
                }
                mHandler.removeCallbacks(seekrunnable)
                videoView.seekTo(seekBar.progress)
                mHandler.postDelayed(seekrunnable, 100)
            }
        })

        playPause.setOnClickListener {

            if (videoView.isPlaying) {
                videoView.pause()
                playPause.setImageResource(R.drawable.play)
            } else {
                videoView.start()
                playPause.setImageResource(R.drawable.pause)
            }

        }

        rotate.setOnClickListener {

            isOrientatinClicked = true
            val orientation = resources.configuration.orientation

            requestedOrientation = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }

        mute.setOnClickListener {

            if (isMute) {
                mAudioManager!!.setStreamMute(3, false)
                isMute = false
                mute.setImageResource(R.drawable.ic_volume_up_black_24dp)
            } else {
                mAudioManager!!.setStreamMute(3, true)
                isMute = true
                mute.setImageResource(R.drawable.ic_volume_mute_black_24dp)
            }

        }

        speed0_5X.setOnClickListener {

            speed = 0.5f
            playSpeed.text = "Play Speed : 0.5X"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

        }

        speed0_75X.setOnClickListener {

            speed = 0.75f
            playSpeed.text = "Play Speed : 0.75X"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

        }

        speed1_0X.setOnClickListener {

            speed = 1.0f
            playSpeed.text = "Play Speed : 1.0X"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

        }

        speed1_25X.setOnClickListener {

            speed = 1.25f
            playSpeed.text = "Play Speed : 1.25X"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

        }

        speed1_5X.setOnClickListener {

            speed = 1.5f
            playSpeed.text = "Play Speed : 1.5X"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            }

        }

        playSpeed.setOnClickListener {

            if (llSpeed!!.visibility == View.VISIBLE) {
                llSpeed!!.visibility = View.GONE
            } else {
                llSpeed!!.visibility = View.VISIBLE
            }

        }

        lock.setOnClickListener {

            isLocked = true

            unlock.visibility = View.VISIBLE
            llleft.visibility = View.GONE
            llright.visibility = View.GONE
            llbottom.visibility = View.GONE
            rltop.visibility = View.GONE

        }

        unlock.setOnClickListener {

            unlock.visibility = View.GONE
            llleft.visibility = View.VISIBLE
            llright.visibility = View.VISIBLE
            llbottom.visibility = View.VISIBLE
            rltop.visibility = View.VISIBLE

            isLocked = false

        }

        gestureDetector = GestureDetector(mContext, ViewGestureListener(mContext, this))

        cutVideo.setOnClickListener {

            val orientation = resources.configuration.orientation

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (x == 0) {
                    x = 1
                    sharedPreferences.edit().putString("RATIO", "Best Fit").apply()
                    onResume()
                } else if (x == 1) {
                    x = 0
                    sharedPreferences.edit().putString("RATIO", "Fill").apply()
                    onResume()
                }
            } else {
                if (cut_flag == 0) {
                    cut_flag = 1
                    sharedPreferences.edit().putString("RATIO", "Best Fit").apply()
                    onResume()
                } else if (cut_flag == 1) {
                    cut_flag = 2
                    sharedPreferences.edit().putString("RATIO", "Fill").apply()
                    onResume()
                } else if (cut_flag == 2) {
                    cut_flag = 3
                    sharedPreferences.edit().putString("RATIO", "16_9").apply()
                    onResume()
                } else if (cut_flag == 3) {
                    cut_flag = 0
                    sharedPreferences.edit().putString("RATIO", "4_3").apply()
                    onResume()
                }
            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
        val currentPosition = videoView.currentPosition.toLong().toInt()
        Util.stopVideoPos = currentPosition
        Util.stopVideoPath = video.getData()
    }

    override fun onResume() {

        try {

            val w = resources.displayMetrics.widthPixels
            val h = resources.displayMetrics.heightPixels

            val RATIO = sharedPreferences.getString("RATIO", "Best Fit")

            if (RATIO.equals("Fill")) {

                val videoviewlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(w, h)
                videoviewlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                videoviewlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                rlTouch.layoutParams = videoviewlp

                val videoviewlp1: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    videoView.layoutParams
                )
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                videoView.layoutParams = videoviewlp1

            } else if (RATIO.equals("16_9")) {

                val videoviewlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    w,
                    (w * 9) / 16
                )
                videoviewlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                videoviewlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                rlTouch.layoutParams = videoviewlp

                val videoviewlp1: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    videoView.layoutParams
                )
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                videoView.layoutParams = videoviewlp1

            } else if (RATIO.equals("4_3")) {

                val videoviewlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    w,
                    (w * 3) / 4
                )
                videoviewlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                videoviewlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                rlTouch.layoutParams = videoviewlp

                val videoviewlp1: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    videoView.layoutParams
                )
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                videoviewlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                videoView.layoutParams = videoviewlp1

            } else if (RATIO.equals("Best Fit")) {

                val videoviewlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                videoviewlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                rlTouch.layoutParams = videoviewlp

                val videoviewlp1: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    videoView.layoutParams
                )
                videoviewlp1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                videoView.layoutParams = videoviewlp1
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    private fun setTime() {

        durationInMilliSec = videoView.duration

        temp = getTime(durationInMilliSec)
        txtTime2.text = temp.first + ":" + temp.second
        seekbar.max = durationInMilliSec
        timer = Timer()

        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try {
                        temp = getTime(videoView.currentPosition)
                        runOnUiThread { txtTime1.text = temp.first + ":" + temp.second }
                        seekbar.progress = videoView.currentPosition
                    } catch (e: java.lang.Exception) {
                        Log.e("MEDIA", e.message + ":")
                    }
                }
            }, 0, 50
        )
    }

    fun getTime(millsec: Int): Pair<String, String> {

        val min: Int
        var sec: Int = millsec / 1000
        min = sec / 60
        sec %= 60
        val minS: String
        var secS: String
        minS = min.toString()
        secS = sec.toString()
        if (sec < 10) {
            secS = "0$secS"
        }
        return Pair.create(minS, secS)
    }

    override fun onHorizontalScroll(z: Boolean) {
        if (!isLocked) {
            if (z) {
                seekForWard()
            } else {
                seekBackWard()
            }
        }
    }

    override fun onSingleTap() {
        if (!isLocked) {
            if (rltop.visibility == View.VISIBLE) {
                llleft.visibility = View.GONE
                llright.visibility = View.GONE
                llbottom.visibility = View.GONE
                rltop.visibility = View.GONE
            } else {
                llleft.visibility = View.VISIBLE
                llright.visibility = View.VISIBLE
                llbottom.visibility = View.VISIBLE
                rltop.visibility = View.VISIBLE
            }
        }
    }

    override fun onVerticalScroll(f: Float, i: Int) {

        if (!isLocked) {

            if (i == 1) {
                mCenterLayout!!.visibility = View.VISIBLE
                mCenterImage!!.setImageResource(R.drawable.ic_brightness2)
                updateBrightness(this, f)
                return
            }
            mCenterImage!!.setImageResource(R.drawable.ic_volume_up_black_24dp);
            mCenterLayout!!.visibility = View.VISIBLE
            updateVolume(this, f);
        }
    }

    fun updateVolume(activity: Activity?, f: Float) {

        if (mCurVolume == -1) {
            mCurVolume = mAudioManager!!.getStreamVolume(3)
            if (mCurVolume < 0) {
                mCurVolume = 0
            }
        }
        var i = mMaxVolume
        val i2 = (i.toFloat() * f).toInt() + mCurVolume
        if (i2 <= i) {
            i = i2
        }
        if (i < 0) {
            i = 0
        }
        Log.e("CCC", "updateVolume: $i")
        mAudioManager!!.setStreamVolume(3, i, 0)
        mCenterProgress!!.progress = i * 100 / mMaxVolume
        txtPer.text = (i * 100 / mMaxVolume ).toString() + "%"
    }

    fun updateBrightness(activity: Activity, f: Float) {

        if (mCurBrightness == -1.0f) {
            mCurBrightness = activity.window.attributes.screenBrightness
            if (mCurBrightness <= 0.01f) {
                mCurBrightness = 0.01f
            }
        }

        val attributes: WindowManager.LayoutParams = activity.window.attributes
        attributes.screenBrightness = mCurBrightness + f
        if (attributes.screenBrightness >= 1.0f) {
            attributes.screenBrightness = 1.0f
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f
        }

        activity.window.attributes = attributes
        mCenterProgress!!.progress = ((attributes.screenBrightness * 100.0f).roundToInt())
        txtPer.text = (attributes.screenBrightness * 100.0f).toString() + "%"

    }

    private fun seekBackWard() {
        val currentPosition = (videoView.currentPosition.toLong() - PROGRESS_SEEK).toInt()
        videoView.seekTo(currentPosition)
    }

    private fun seekForWard() {
        val currentPosition = (videoView.currentPosition.toLong() + PROGRESS_SEEK).toInt()
        videoView.seekTo(currentPosition)
    }

    fun onBack(view: View?) {
        onBackPressed()
    }
}