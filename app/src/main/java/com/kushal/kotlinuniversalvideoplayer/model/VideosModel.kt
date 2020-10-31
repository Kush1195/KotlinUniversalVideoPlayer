package com.kushal.kotlinuniversalvideoplayer.model

import java.io.Serializable

public class VideosModel : Serializable {

    val SIZE_READABLE = "size_readable"
    private var _ID: Long = 0
    private var data: String? = null
    private var dateAdded: String? = null
    private var duration: String? = null
    private var name: String? = null
    private var resolution: String? = null
    private var size: Long = 0
    private var sizeReadable: String? = null
    private var time: String? = null
    private var title: String? = null

    fun VideosModel() {}

    fun VideosModel(
        j: Long,
        str: String?,
        str2: String?,
        str3: String?,
        str4: String?,
        str5: String?,
        j2: Long,
        str6: String?
    ) {
        _ID = j
        name = str
        title = str2
        dateAdded = str3
        duration = str4
        resolution = str5
        size = j2
        data = str6
    }

    fun getData(): String? {
        return data
    }

    fun getDateAdded(): String? {
        return dateAdded
    }

    fun getDuration(): String? {
        return duration
    }

    fun getMime(): String? {
        return time
    }

    fun getName(): String? {
        return name
    }

    fun getResolution(): String? {
        return resolution
    }

    fun getSize(): Long {
        return size
    }

    fun getSizeReadable(): String? {
        return sizeReadable
    }

    fun getTitle(): String? {
        return title
    }

    fun get_ID(): Long {
        return _ID
    }

    fun setData(str: String?) {
        data = str
    }

    fun setDateAdded(str: String?) {
        dateAdded = str
    }

    fun setDuration(str: String?) {
        duration = str
    }

    fun setTime(str: String?) {
        time = str
    }

    fun setName(str: String?) {
        name = str
    }

    fun setResolution(str: String?) {
        resolution = str
    }

    fun setSize(j: Long) {
        size = j
    }

    fun setSizeReadable(str: String?) {
        sizeReadable = str
    }

    fun setTitle(str: String?) {
        title = str
    }

    fun set_ID(j: Long) {
        _ID = j
    }

}