package com.kushal.kotlinuniversalvideoplayer.model

public class VideoFolderModel {

    private var name: String? = null
    private var path: String? = null
    private var totalSize: Long = 0
    private var totalVideos: Long = 0

    override fun equals(obj: Any?): Boolean {
        return false
    }

    fun VideoFolderModel() {
        name = null
        totalVideos = 0
        totalSize = 0
    }

    fun getName(): String? {
        return name
    }

    fun getPath(): String? {
        return path
    }

    fun getTotalSize(): Long {
        return totalSize
    }

    fun getTotalVideos(): Long {
        return totalVideos
    }

    fun setName(str: String?) {
        name = str
    }

    fun setPath(str: String?) {
        path = str
    }

    fun setTotalSize(j: Long) {
        totalSize = j
    }

    fun setTotalVideos(j: Long) {
        totalVideos = j
    }

    fun sizePP(j: Long) {
        totalSize += j
    }

    fun videosPP() {
        totalVideos++
    }
    
    
}