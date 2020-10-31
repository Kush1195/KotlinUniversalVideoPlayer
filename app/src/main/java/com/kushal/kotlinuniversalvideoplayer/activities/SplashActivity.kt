package com.kushal.kotlinuniversalvideoplayer.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kushal.kotlinuniversalvideoplayer.R
import com.kushal.kotlinuniversalvideoplayer.helpers.HelperResizer
import com.kushal.kotlinuniversalvideoplayer.helpers.VideosAndFoldersUtility
import com.kushal.kotlinuniversalvideoplayer.model.EqualizerModel
import com.kushal.kotlinuniversalvideoplayer.model.VideoFolderModel
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel
import java.lang.Boolean.TRUE

class SplashActivity : AppCompatActivity() {

    private val REQUESTCODE: Int = 1001
    lateinit var logo: ImageView

    companion object {

        lateinit var mContext: Context
        lateinit var pgdialog : ProgressDialog
        var isEqualizerEnabled = false
        var bassStrength: Short = -1
        var reverbPreset: Short = -1
        var equalizerModel: EqualizerModel? = null
        var isEqualizerReloaded = false
        var seekbarpos = IntArray(5)
        var presetPos = 0
        var screen_width:Int = 0
        var screen_height:Int = 0

        var folders : List<VideoFolderModel> = ArrayList<VideoFolderModel>()
        var videos : List<VideosModel> = ArrayList<VideosModel>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mContext = this
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        mycheckpermission()
        init()
        resize()
    }

    private fun mycheckpermission() {

        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT > 22) {
            requestPermissions(permissions, REQUESTCODE)
        } else {
            onSuccess()
        }

    }

    private fun onSuccess() {

        grantOverlayPermission()

        try {
            AsyncLoadVideoAndFolders().execute(TRUE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    class AsyncLoadVideoAndFolders : AsyncTask<kotlin.Boolean, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pgdialog = ProgressDialog(mContext)
            pgdialog.setMessage("Please Wait... Loading")
            pgdialog.setCancelable(false)
            pgdialog.show()

        }

        override fun doInBackground(vararg params: kotlin.Boolean?): Void? {

            val videosAndFoldersUtility = VideosAndFoldersUtility(mContext)

            videos = videosAndFoldersUtility.fetchAllVideos()
            folders = videosAndFoldersUtility.fetchAllFolders()

            try {

                if (videos.size > 2) {

                    videos = videos.sortedWith(
                        compareBy(
                            VideosModel::getName,
                            VideosModel::getName
                        )
                    )

                }

            } catch (unused: NullPointerException) {
                unused.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            pgdialog.dismiss()
//            foldersAdapter = FoldersAdapter(mContext, folders)
//            VideoFolderListActivity.rv_folders.adapter = foldersAdapter
        }

    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Settings.canDrawOverlays(mContext)) {

                val handler = Handler()

                handler.postDelayed({

                    startActivity(Intent(mContext, VideoFolderListActivity::class.java))

                }, 3000)
            }
        }
    }

    private fun grantOverlayPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                        "package:$packageName"
                    )
                )
                startActivityForResult(intent, 0)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(mContext)) {

                    Toast.makeText(
                        mContext,
                        "Please Provide This Permission First..!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                            "package:$packageName"
                        )
                    )

                    startActivityForResult(intent, 0)

                } else {

                    val handler = Handler()

                    handler.postDelayed({

                        startActivity(Intent(mContext, VideoFolderListActivity::class.java))

                    }, 3000)
                }
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(mContext)) {

                    Toast.makeText(
                        mContext, "Please Provide This Permission First..!!!", Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                            "package:$packageName"
                        )
                    )
                    startActivityForResult(intent, 0)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUESTCODE) {

            if (grantResults.isNotEmpty()) {

                for (grantResult in grantResults) {

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        finish()
                        return
                    }
                }
                onSuccess()

            } else {

                finish()

            }

        }

    }

    private fun resize() {

        HelperResizer.getheightandwidth(mContext)
        HelperResizer.setSize(logo, 1080, 979)

    }

    private fun init() {

        logo = findViewById(R.id.logo)

        if (equalizerModel == null) {
            equalizerModel = EqualizerModel()
            isEqualizerEnabled = true
            isEqualizerReloaded = false
        } else {
            isEqualizerEnabled = equalizerModel!!.isEqualizerEnabled()
            isEqualizerReloaded = true
            seekbarpos = equalizerModel!!.getSeekbarpos()!!
            presetPos = equalizerModel!!.getPresetPos()
            reverbPreset = equalizerModel!!.getReverbPreset()
            bassStrength = equalizerModel!!.getBassStrength()
        }

    }
}

