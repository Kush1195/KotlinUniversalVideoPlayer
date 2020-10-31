package com.kushal.kotlinuniversalvideoplayer.helpers

interface VideoGestureListener {
    fun onHorizontalScroll(z: Boolean)
    fun onSingleTap()
    fun onVerticalScroll(f: Float, i: Int)
}