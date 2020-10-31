package com.kushal.kotlinuniversalvideoplayer.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.activities.VideoPlayerActivity
import com.kushal.kotlinuniversalvideoplayer.helpers.Util
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel
import java.io.IOException
import java.io.Serializable

class FloatVideoService : Service(), TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener {

    lateinit var removeButton: ImageView
    lateinit var resizeButton: ImageView
    lateinit var videoView: TextureView
    lateinit var inflater: LayoutInflater
    var params: WindowManager.LayoutParams? = null
    var dm: DisplayManager? = null
    var display1: Display? = null
    var displayMetrics = DisplayMetrics()
    var dispWidth = 0
    var dispHeight = 0
    var statusBarHeight = 0
    var w = 0f
    var h = 0f
    var density = 0f
    var dispRect = Rect()
    var scale = 1.0f
    var attached = false
    var path1: String? = null
    var pos = 0
    lateinit var btnPlayPause: ImageView
    lateinit var fullscreen: ImageView
    lateinit var context: Context
    var vHeight = 0
    var vWidth = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        context = this
        val extras = intent.extras
        videoList1 = extras!!.getSerializable("list") as List<VideosModel>?
        videoIndex1 = extras.getInt("index")
        video1 = videoList1!![videoIndex1]
        path1 = video1!!.getData()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        dm = getSystemService(
            DISPLAY_SERVICE
        ) as DisplayManager
        display1 = wm!!.defaultDisplay
        val resources = resources
        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = if (statusBarHeightId > 0) {
            resources.getDimensionPixelSize(statusBarHeightId)
        } else {
            0
        }
        updateDefaultDisplayInfo()
        set_video_screen()
        dm!!.registerDisplayListener(displayListener, null)
        inflater = LayoutInflater.from(this)
        container = inflater.inflate(R.layout.video_content, null) as FrameLayout
        videoView = container!!.findViewById(R.id.video_view)
        removeButton = container!!.findViewById(R.id.remove_button)
        resizeButton = container!!.findViewById(R.id.resize_button)
        fullscreen = container!!.findViewById(R.id.fullscreen)
        btnPlayPause = container!!.findViewById(R.id.btnPlayPause)
        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )
        params!!.format = PixelFormat.TRANSLUCENT
        params!!.alpha = 1.0f
        params!!.gravity = Gravity.TOP or Gravity.LEFT
        setupCoordinates()
        wm!!.addView(container, params)
        attached = true
        this.videoView.surfaceTextureListener = this

        container!!.setOnTouchListener(moveListener)
        removeButton.setOnTouchListener(removeListener)
        resizeButton.setOnTouchListener(resizeListener)

        btnPlayPause.setOnClickListener(View.OnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                btnPlayPause.setImageResource(R.drawable.play)
            } else {
                mediaPlayer!!.start()
                btnPlayPause.setImageResource(R.drawable.pause)
            }
        })
        fullscreen.setOnClickListener(View.OnClickListener {
            val currentPosition = mediaPlayer!!.currentPosition
                .toLong().toInt()
            Util.stopVideoPos = currentPosition
            Util.stopVideoPath = path1!!
            dismiss()
            stopSelf()

//
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("index", videoIndex1)
            intent.putExtra("list", videoList1 as Serializable?)
            intent.addFlags(268435456)
            context.startActivity(intent)
        })
        resize()
        return START_NOT_STICKY
    }

    private fun resize() {
        val w = resources.displayMetrics.widthPixels
        val h = resources.displayMetrics.heightPixels
        val btnPlayPause_p = FrameLayout.LayoutParams(w * 100 / 1080, w * 100 / 1080)
        btnPlayPause_p.gravity = Gravity.CENTER
        btnPlayPause!!.layoutParams = btnPlayPause_p
        val fullscreen_p = FrameLayout.LayoutParams(w * 90 / 1080, w * 90 / 1080)
        fullscreen!!.layoutParams = fullscreen_p
        val remove_button_p = FrameLayout.LayoutParams(w * 90 / 1080, w * 90 / 1080)
        remove_button_p.gravity = Gravity.RIGHT
        removeButton!!.layoutParams = remove_button_p
        val resize_button_p = FrameLayout.LayoutParams(w * 90 / 1080, w * 90 / 1080)
        resize_button_p.gravity = Gravity.BOTTOM or Gravity.RIGHT
        resizeButton!!.layoutParams = resize_button_p
    }

    private fun updateDefaultDisplayInfo(): Boolean {
        display1!!.getMetrics(displayMetrics)
        dispWidth = displayMetrics.widthPixels
        dispHeight = displayMetrics.heightPixels
        return true
    }

    private fun setupCoordinates() {
//        w = (w == 0) ? displayMetrics.widthPixels / MAX_SCALE : Math.min(w, displayMetrics.widthPixels / MAX_SCALE);
//        h = (h == 0) ? w * 3f / 4f : Math.min(h, w * 3f / 4f);
        w = vWidth.toFloat()
        h = vHeight.toFloat()
        dispRect = Rect(0, statusBarHeight, dispWidth, dispHeight)
        params!!.x = constrain(
            dispRect.left,
            dispRect.right - Math.round(Math.round(w * scale).toFloat()),
            Math.round(dispWidth - w * scale)
        )
        params!!.y = constrain(
            dispRect.top,
            dispRect.bottom - Math.round(h * scale),
            Math.round(dispHeight - h * scale)
        )
        params!!.width = Math.round(w * scale)
        params!!.height = Math.round(h * scale)
        videoView!!.pivotX = 0f
        videoView!!.pivotY = 0f
        videoView!!.layoutParams.width = Math.round(w)
        videoView!!.layoutParams.height = Math.round(h)
        videoView!!.scaleX = scale
        videoView!!.scaleY = scale
    }

    private fun relayout() {
        if (!attached) return
        setupCoordinates()
        wm!!.updateViewLayout(container, params)
    }

    private fun dismiss() {
        if (!attached) return
        attached = false
        dm!!.unregisterDisplayListener(displayListener)
        wm!!.removeView(container)
    }

    private val moveListener: View.OnTouchListener = object : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params!!.x
                    initialY = params!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = (event.rawX - initialTouchX).toInt()
                    val diffY = (event.rawY - initialTouchY).toInt()
                    params!!.x = constrain(
                        dispRect.left, dispRect.right - Math.round(
                            Math.round(
                                params!!.width.toFloat()
                            ).toFloat()
                        ), initialX + diffX
                    )
                    params!!.y = constrain(
                        dispRect.top, dispRect.bottom - Math.round(
                            Math.round(
                                params!!.height.toFloat()
                            ).toFloat()
                        ), initialY + diffY
                    )
                    wm!!.updateViewLayout(container, params)
                    return true
                }
                MotionEvent.ACTION_CANCEL -> return true
                MotionEvent.ACTION_UP -> return true
            }
            return false
        }
    }
    private val removeListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dismiss()
                stopSelf()
                return@OnTouchListener true
            }
            MotionEvent.ACTION_MOVE -> return@OnTouchListener true
            MotionEvent.ACTION_CANCEL -> return@OnTouchListener true
            MotionEvent.ACTION_UP -> return@OnTouchListener true
        }
        false
    }
    private val resizeListener: View.OnTouchListener = object : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params!!.x
                    initialY = params!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val touchX = event.rawX
                    val touchY = event.rawY
                    val diffX = (touchX - initialTouchX).toInt()
                    val oldW = params!!.width
                    val newW = oldW + diffX
                    val newScale = scale * newW.toFloat() / oldW.toFloat()
                    scale = constrain(MIN_SCALE, MAX_SCALE, newScale)
                    videoView!!.scaleX = scale
                    videoView!!.scaleY = scale
                    params!!.width = Math.round(w * scale)
                    params!!.height = Math.round(h * scale)
                    wm!!.updateViewLayout(container, params)
                    initialTouchX = touchX
                    initialTouchY = touchY
                    return true
                }
                MotionEvent.ACTION_CANCEL -> return true
                MotionEvent.ACTION_UP -> return true
            }
            return false
        }
    }
    private val displayListener: DisplayManager.DisplayListener =
        object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {
                Log.d(TAG, "onDisplayAdded")
            }

            override fun onDisplayChanged(displayId: Int) {
                Log.d(TAG, "onDisplayChanged")
                if (displayId == display1!!.displayId) {
                    updateDefaultDisplayInfo()
                    setupCoordinates()
                    relayout()
                }
            }

            override fun onDisplayRemoved(displayId: Int) {
                Log.d(TAG, "onDisplayRemoved")
                if (displayId == display1!!.displayId) {
                    dismiss()
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
    }

    private fun constrain(min: Int, max: Int, value: Int): Int {
        if (value < min) return min
        return if (value > max) max else value
    }

    private fun constrain(min: Float, max: Float, value: Float): Float {
        if (value < min) return min
        return if (value > max) max else value
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val s = Surface(surface)
        try {
            val pos = Util.stopVideoPos as Int
            val path2 = Util.stopVideoPath
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(this, Uri.parse(path1))
            mediaPlayer!!.setSurface(s)
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.prepare()
            if (path2 == null) {
            } else {
                if (path2.equals(path1, ignoreCase = true)) {
                    mediaPlayer!!.seekTo(pos)
                }
            }
            mediaPlayer!!.start()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureSizeChanged")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.d(TAG, "onSurfaceTextureDestroyed")
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    override fun onCompletion(mp: MediaPlayer) {
        mediaPlayer!!.seekTo(0)
        mediaPlayer!!.start()
    }

    fun set_video_screen() {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(this, Uri.parse(path1))
            mediaPlayer.prepare()
            vHeight = mediaPlayer.getVideoHeight()
            vWidth = mediaPlayer.getVideoWidth()
        } catch (e: Exception) {
        }
        VideoResize()
    }

    fun VideoResize() {
        var newheight: Int
        var newwidth: Int
        val layoutwidth = dispWidth
        val layoutheight = dispHeight
        val imagewidth = vWidth
        val imageheight = vHeight
        if (imagewidth >= imageheight) {
            newwidth = layoutwidth
            newheight = newwidth * imageheight / imagewidth
            if (newheight > layoutheight) {
                newwidth = layoutheight * newwidth / newheight
                newheight = layoutheight
            }
        } else {
            newheight = layoutheight
            newwidth = newheight * imagewidth / imageheight
            if (newwidth > layoutwidth) {
                newheight = newheight * layoutwidth / newwidth
                newwidth = layoutwidth
            }
        }
        vHeight = newheight
        vWidth = newwidth
        if (vHeight >= dispHeight) {
            vHeight -= 50
        }
    }

    companion object {
        private val TAG = FloatVideoService::class.java.simpleName
        private const val MIN_SCALE = 0.3f
        private const val MAX_SCALE = 2.0f
        var container: FrameLayout? = null
        lateinit var mediaPlayer: MediaPlayer
        var wm: WindowManager? = null
        var video1: VideosModel? = null
        var videoIndex1 = 0
        var videoList1: List<VideosModel>? = null
        fun removeFloatView() {
            mediaPlayer!!.pause()
            wm!!.removeView(container)
        }
    }
}