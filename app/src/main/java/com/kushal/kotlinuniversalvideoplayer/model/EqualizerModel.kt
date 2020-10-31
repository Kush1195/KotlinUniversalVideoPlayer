package com.kushal.kotlinuniversalvideoplayer.model

public class EqualizerModel {

    var isEqualizerEnabled = false
    var seekbarpos = IntArray(5)
    var presetPos = 0
    var reverbPreset: Short = -1
    var bassStrength: Short = -1

    fun EqualizerModel() {
        isEqualizerEnabled = true
        reverbPreset = -1
        bassStrength = -1
    }

    @JvmName("isEqualizerEnabled1")
    fun isEqualizerEnabled(): Boolean {
        return isEqualizerEnabled
    }

    @JvmName("setEqualizerEnabled1")
    fun setEqualizerEnabled(equalizerEnabled: Boolean) {
        isEqualizerEnabled = equalizerEnabled
    }

    @JvmName("getSeekbarpos1")
    fun getSeekbarpos(): IntArray? {
        return seekbarpos
    }

    @JvmName("setSeekbarpos1")
    fun setSeekbarpos(seekbarpos: IntArray?) {
        this.seekbarpos = seekbarpos!!
    }

    @JvmName("getPresetPos1")
    fun getPresetPos(): Int {
        return presetPos
    }

    @JvmName("setPresetPos1")
    fun setPresetPos(presetPos: Int) {
        this.presetPos = presetPos
    }

    @JvmName("getReverbPreset1")
    fun getReverbPreset(): Short {
        return reverbPreset
    }

    @JvmName("setReverbPreset1")
    fun setReverbPreset(reverbPreset: Short) {
        this.reverbPreset = reverbPreset
    }

    @JvmName("getBassStrength1")
    fun getBassStrength(): Short {
        return bassStrength
    }

    @JvmName("setBassStrength1")
    fun setBassStrength(bassStrength: Short) {
        this.bassStrength = bassStrength
    }

}