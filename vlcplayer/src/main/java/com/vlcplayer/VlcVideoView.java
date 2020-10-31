package com.vlcplayer;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.log.Logger;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.vlc.listener.MediaListenerEvent;
import org.videolan.vlc.listener.MediaPlayerControl;
import org.videolan.vlc.listener.VideoSizeChange;

public class VlcVideoView extends TextureView implements MediaPlayerControl, SurfaceTextureListener, VideoSizeChange {
    private boolean isRotation;

    public int mVideoHeight;

    public int mVideoWidth;
    private boolean mirror;
    private final String tag;
    private VlcVideoPlayer vlcVideoPlayer;

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public VlcVideoView(Context context) {
        this(context, null);
    }

    public VlcVideoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VlcVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.tag = "VlcVideoView";
        this.mirror = false;
        this.isRotation = true;
        if (!isInEditMode()) {
            init(context);
        }
    }

    public void setMediaListenerEvent(MediaListenerEvent mediaListenerEvent) {
        this.vlcVideoPlayer.setMediaListenerEvent(mediaListenerEvent);
    }

    public boolean canControl() {
        return this.vlcVideoPlayer.canControl();
    }

    public void onStop() {
        this.vlcVideoPlayer.onStop();
    }

    public void onDestory() {
        VlcVideoPlayer vlcVideoPlayer2 = this.vlcVideoPlayer;
        if (vlcVideoPlayer2 != null) {
            vlcVideoPlayer2.setVideoSizeChange(null);
        }
        Logger.i("VlcVideoView", "onDestory");
    }

    private void init(Context context) {
        this.vlcVideoPlayer = new VlcVideoPlayer(context);
        this.vlcVideoPlayer.setVideoSizeChange(this);
        setSurfaceTextureListener(this);
    }

    public void setMediaPlayer(LibVLC libVLC) {
        this.vlcVideoPlayer.setMediaPlayer(libVLC);
    }

    public void setMedia(Media media) {
        this.vlcVideoPlayer.setMedia(media);
    }

    public boolean isPrepare() {
        return this.vlcVideoPlayer.isPrepare();
    }

    public void startPlay(String str) {
        this.vlcVideoPlayer.startPlay(str);
    }

    public void saveState() {
        this.vlcVideoPlayer.saveState();
    }

    public void start() {
        this.vlcVideoPlayer.start();
    }

    public void pause() {
        this.vlcVideoPlayer.pause();
    }

    public void resume() {
        this.vlcVideoPlayer.start();
    }

    public long getDuration() {
        return this.vlcVideoPlayer.getDuration();
    }

    public long getCurrentPosition() {
        return this.vlcVideoPlayer.getCurrentPosition();
    }

    public void seekTo(long j) {
        this.vlcVideoPlayer.seekTo(j);
    }

    public boolean isPlaying() {
        return this.vlcVideoPlayer.isPlaying();
    }

    public void setMirror(boolean z) {
        this.mirror = z;
        if (z) {
            setScaleX(-1.0f);
        } else {
            setScaleX(1.0f);
        }
    }

    public boolean getMirror() {
        return this.mirror;
    }

    public int getBufferPercentage() {
        return this.vlcVideoPlayer.getBufferPercentage();
    }

    public boolean setPlaybackSpeedMedia(float f) {
        return this.vlcVideoPlayer.setPlaybackSpeedMedia(f);
    }

    public float getPlaybackSpeed() {
        return this.vlcVideoPlayer.getPlaybackSpeed();
    }

    public void setLoop(boolean z) {
        this.vlcVideoPlayer.setLoop(z);
    }

    public boolean isLoop() {
        return this.vlcVideoPlayer.isLoop();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Logger.i("VlcVideoView", "onSurfaceTextureAvailable");
        this.vlcVideoPlayer.setSurface(surfaceTexture);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        Logger.i("VlcVideoView", "onSurfaceTextureSizeChanged");
        post(new Runnable() {
            public void run() {
                VlcVideoView vlcVideoView = VlcVideoView.this;
                vlcVideoView.adjustAspectRatio(vlcVideoView.mVideoWidth, VlcVideoView.this.mVideoHeight);
            }
        });
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Logger.i("VlcVideoView", "onSurfaceTextureDestroyed");
        this.vlcVideoPlayer.onSurfaceTextureDestroyed();
        return true;
    }


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.i("VlcVideoView", "onAttachedToWindow");
        if (!isInEditMode()) {
            setKeepScreenOn(true);
            VlcVideoPlayer vlcVideoPlayer2 = this.vlcVideoPlayer;
            if (vlcVideoPlayer2 != null) {
                vlcVideoPlayer2.onAttachedToWindow(true);
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.i("VlcVideoView", "onDetachedFromWindow");
        if (!isInEditMode()) {
            setKeepScreenOn(false);
            VlcVideoPlayer vlcVideoPlayer2 = this.vlcVideoPlayer;
            if (vlcVideoPlayer2 != null) {
                vlcVideoPlayer2.onAttachedToWindow(false);
            }
        }
    }

    public boolean isRotation() {
        return this.isRotation;
    }

    public void adjustAspectRatio(int i, int i2) {
        int i3;
        int i4;
        if (i * i2 != 0) {
            if (i > i2) {
                this.isRotation = true;
            } else {
                this.isRotation = false;
            }
            int width = getWidth();
            int height = getHeight();
            double d = (double) width;
            double d2 = (double) height;
            Double.isNaN(d);
            Double.isNaN(d2);
            double d3 = d / d2;
            double d4 = (double) i;
            double d5 = (double) i2;
            Double.isNaN(d4);
            Double.isNaN(d5);
            double d6 = d4 / d5;
            if (i <= i2) {
                Double.isNaN(d2);
                i4 = (int) (d2 * d6);
                i3 = height;
            } else if (d3 > d6) {
                Double.isNaN(d2);
                i4 = (int) (d2 * d6);
                i3 = height;
            } else {
                Double.isNaN(d);
                i3 = (int) (d / d6);
                i4 = width;
            }
            int i5 = (width - i4) / 2;
            int i6 = (height - i3) / 2;
            Matrix matrix = new Matrix();
            getTransform(matrix);
            matrix.setScale(((float) i4) / ((float) width), ((float) i3) / ((float) height));
            matrix.postTranslate((float) i5, (float) i6);
            setTransform(matrix);
            StringBuilder sb = new StringBuilder();
            sb.append("video=");
            sb.append(i);
            sb.append("x");
            sb.append(i2);
            sb.append(" view=");
            sb.append(width);
            sb.append("x");
            sb.append(height);
            sb.append(" newView=");
            sb.append(i4);
            sb.append("x");
            sb.append(i3);
            sb.append(" off=");
            sb.append(i5);
            sb.append(",");
            sb.append(i6);
            Logger.i("VlcVideoView", sb.toString());
        }
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            adjustAspectRatio(this.mVideoWidth, this.mVideoHeight);
        }
    }

    public void onVideoSizeChanged(int i, int i2, int i3, int i4, int i5, int i6) {
        if (i * i2 != 0) {
            this.mVideoWidth = i3;
            this.mVideoHeight = i4;
            post(new Runnable() {
                public void run() {
                    VlcVideoView vlcVideoView = VlcVideoView.this;
                    vlcVideoView.adjustAspectRatio(vlcVideoView.mVideoWidth, VlcVideoView.this.mVideoHeight);
                }
            });
        }
    }
}
