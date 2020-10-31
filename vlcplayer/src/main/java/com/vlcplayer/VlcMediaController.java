package com.vlcplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import com.log.Logger;
import com.vlcplayer.VlcControllerView.MediaPlayerControl;
import org.videolan.vlc.listener.FullScreenListener;
import org.videolan.vlc.listener.MediaListenerEvent;

public class VlcMediaController implements MediaPlayerControl, MediaListenerEvent {
    private static final long PROGRESS_SEEK = 2500;
    public static AudioManager mAudioManager = null;
    public static float mCurBrightness = -1.0f;
    public static int mCurVolume = -1;
    public static int mMaxVolume;
    public static org.videolan.vlc.listener.MediaPlayerControl mediaPlayer;
    public long diffX;
    public long diffY;
    public Display display;
    public float downX;
    public float downY;
    errorBack errorBack2;
    public FullScreenListener fullScreenListener;
    public boolean intLeft;
    public boolean intRight;
    public int sHeight;
    public int sWidth;
    public Point size;
    public VlcControllerView vlcControllerView;

    public interface errorBack {
        void back();
    }

    public static int setSeekProgress() {
        return 1;
    }

    public void PreVideo() {
    }

    public void eventBuffing(float f, boolean z) {
    }

    public void nextVideo() {
    }

    public VlcMediaController(final VlcControllerView vlcControllerView2, VlcVideoView vlcVideoView) {
        mediaPlayer = vlcVideoView;
        this.vlcControllerView = vlcControllerView2;
        vlcControllerView2.setControllerView(vlcVideoView);
        vlcControllerView2.setMediaPlayerListener(this);
        vlcVideoView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!vlcControllerView2.isShowing()) {
                    vlcControllerView2.show();
                } else {
                    vlcControllerView2.hide();
                }
            }
        });
    }

    public void errorBackActivty(errorBack errorback) {
        this.errorBack2 = errorback;
    }

    public void setFullScreenListener(FullScreenListener fullScreenListener2) {
        this.fullScreenListener = fullScreenListener2;
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return (int) mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo((long) i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return mediaPlayer.getBufferPercentage();
    }

    public boolean canPause() {
        return mediaPlayer.isPrepare();
    }

    public void toggleFullScreen(boolean z) {
        if (z) {
            this.fullScreenListener.Fullscreen(z);
        } else {
            this.fullScreenListener.Fullscreen(z);
        }
    }

    public void eventPlayInit(boolean z) {
        Logger.w("");
    }

    public void eventStop(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("Stop");
        sb.append(z ? "  " : "");
        Logger.w(sb.toString());
    }

    @Override
    public void eventError(int error, boolean show) {

    }

    public void eventError(Context context, int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(" error=");
        sb.append(i);
        Logger.w(sb.toString());
        this.errorBack2.back();
    }

    public void eventPlay(boolean z) {
        if (z) {
            this.vlcControllerView.show();
        }
    }

    public static void seekBackWardScroll() {
//        org.videolan.vlc.listener.MediaPlayerControl mediaPlayerControl = mediaPlayer;
        if (mediaPlayer != null) {
            VlcControllerView.mPauseButton.setImageResource(R.drawable.n_pause_button);
            mediaPlayer.seekTo((long) ((int) (mediaPlayer.getCurrentPosition() - 500)));
            setSeekProgress();
            mediaPlayer.pause();
            mediaPlayer.start();
        }
    }

    public static void seekForWardScroll() {
//        org.videolan.vlc.listener.MediaPlayerControl mediaPlayerControl = mediaPlayer;
        if (mediaPlayer != null) {
            VlcControllerView.mPauseButton.setImageResource(R.drawable.n_pause_button);
            mediaPlayer.seekTo((long) ((int) (mediaPlayer.getCurrentPosition() + 500)));
            setSeekProgress();
            mediaPlayer.pause();
            mediaPlayer.start();
        }
    }

    public static void seekBackWard() {
//        org.videolan.vlc.listener.MediaPlayerControl mediaPlayerControl = mediaPlayer;
        if (mediaPlayer != null) {
            VlcControllerView.mPauseButton.setImageResource(R.drawable.n_pause_button);
            mediaPlayer.seekTo((long) ((int) (mediaPlayer.getCurrentPosition() - PROGRESS_SEEK)));
            setSeekProgress();
            mediaPlayer.pause();
            mediaPlayer.start();

        }
    }

    public static void seekForWard() {
//        org.videolan.vlc.listener.MediaPlayerControl mediaPlayerControl = mediaPlayer;
        if (mediaPlayer != null) {
            VlcControllerView.mPauseButton.setImageResource(R.drawable.n_pause_button);
            mediaPlayer.seekTo((long) ((int) (mediaPlayer.getCurrentPosition() + PROGRESS_SEEK)));
            setSeekProgress();
            mediaPlayer.pause();
            mediaPlayer.start();
        }
    }

    public static void updateVolume(Activity activity, float f) {
        VlcControllerView.mCenterLayout.setVisibility(View.VISIBLE);
        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(3);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }
        int i = mMaxVolume;
        int i2 = ((int) (((float) i) * f)) + mCurVolume;
        if (i2 <= i) {
            i = i2;
        }
        if (i < 0) {
            i = 0;
        }
        mAudioManager.setStreamVolume(3, i, 0);
        VlcControllerView.mCenterProgress.setProgress((i * 100) / mMaxVolume);
    }

    public static void updateBrightness(Activity activity, float f) {
        if (mCurBrightness == -1.0f) {
            mCurBrightness = activity.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness <= 0.01f) {
                mCurBrightness = 0.01f;
            }
        }
        VlcControllerView.mCenterLayout.setVisibility(View.VISIBLE);
        LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + f;
        if (attributes.screenBrightness >= 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        activity.getWindow().setAttributes(attributes);
        VlcControllerView.mCenterProgress.setProgress((int) (attributes.screenBrightness * 100.0f));
    }
}
