package com.vlcplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.log.Logger;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.Media.Event;
import org.videolan.libvlc.Media.EventListener;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.listener.MediaListenerEvent;
import org.videolan.vlc.listener.MediaPlayerControl;
import org.videolan.vlc.listener.VideoSizeChange;
import org.videolan.vlc.util.VLCInstance;

public class VlcVideoPlayer implements MediaPlayerControl, Callback, IVLCVout.Callback {
    public static final int MSG_START = 8;
    public static final int MSG_STOP = 9;

    public static long abTimeEnd = 0;
    public static long abTimeStart = 0;
    public static boolean canInfo = false;
    public static boolean canPause = false;
    public static boolean canSeek = false;
    public static boolean isABLoop = false;
    public static boolean isAttachSurface = false;
    public static boolean isAttached = false;
    public static boolean isInitPlay = false;
    public static boolean isInitStart = false;
    private static boolean isInstance = true;
    public static boolean isLoop = true;
    public static boolean isPlayError = false;
    private static boolean isSaveState = false;
    public static boolean isSufaceDelayerPlay = false;
    public static boolean isViewLife = false;
    public static LibVLC libVLC = null;
    public static Context mContext = null;
    public static MediaPlayer mMediaPlayer = null;
    public static Handler mVideoHandler = null;
    public static Handler mainHandler = null;
    public static MediaListenerEvent mediaListenerEvent = null;
    public static boolean othereMedia = false;
    public static String path = null;
    public static final HandlerThread sThread = new HandlerThread("VlcVideoPlayThread");
    public static MediaPlayer staticMediaPlayer = null;
    public static SurfaceTexture surface = null;
    public static final String tag = "VideoMediaLogic";
    public static String TAG = tag;
    private boolean isSeeking;
    private final EventListener mMediaListener = new EventListener() {
        public void onEvent(Event event) {
            int i = event.type;
            if (i == 0) {
                String str = VlcVideoPlayer.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Media.Event.MetaChanged:  =");
                sb.append(event.getMetaId());
                Logger.i(str, sb.toString());
            } else if (i == 3) {
                String str2 = VlcVideoPlayer.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Media.Event.ParsedChanged  =");
                sb2.append(event.getMetaId());
                Logger.i(str2, sb2.toString());
            } else if (i != 5) {
                String str3 = VlcVideoPlayer.TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Media.Event.type=");
                sb3.append(event.type);
                sb3.append("   eventgetParsedStatus=");
                sb3.append(event.getParsedStatus());
                Logger.i(str3, sb3.toString());
            } else {
                String str4 = VlcVideoPlayer.TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("StateChanged   =");
                sb4.append(event.getMetaId());
                Logger.i(str4, sb4.toString());
            }
        }
    };
    private float speed = 1.0f;
    private VideoSizeChange videoSizeChange;

    public int getBufferPercentage() {
        return 0;
    }

    public boolean getMirror() {
        return false;
    }

    public void onSurfacesCreated(IVLCVout iVLCVout) {
    }

    public void onSurfacesDestroyed(IVLCVout iVLCVout) {
    }

    public void resume() {
    }

    public void setMirror(boolean z) {
    }

    static {
        sThread.start();
    }

    public static MediaPlayer getMediaPlayer(Context context) {
        if (staticMediaPlayer == null) {
            staticMediaPlayer = new MediaPlayer(VLCInstance.get(context));
        }
        return staticMediaPlayer;
    }

    public void setInstance(boolean z) {
        isInstance = z;
    }

    public boolean handleMessage(Message message) {
        synchronized (VlcVideoPlayer.class) {
            switch (message.what) {
                case 8:
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("-----HandlerThread init 1=");
                    sb.append(isInitStart);
                    Logger.i(str, sb.toString());
                    if (isInitStart) {
                        opendVideo();
                    }
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("-----HandlerThread init 2=");
                    sb2.append(isInitStart);
                    Logger.i(str2, sb2.toString());
                    break;
                case 9:
                    String str3 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("-----HandlerThread stop 3=");
                    sb3.append(isInitStart);
                    Logger.i(str3, sb3.toString());
                    String str4 = TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("-----HandlerThread stop 4=");
                    sb4.append(isInitStart);
                    Logger.i(str4, sb4.toString());
                    break;
            }
        }
        return true;
    }

    public VlcVideoPlayer(Context context) {
        mContext = context.getApplicationContext();
        mVideoHandler = new Handler(sThread.getLooper(), this);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setMediaPlayer(LibVLC libVLC2) {
        mMediaPlayer = new MediaPlayer(libVLC2);
    }

    public void setMedia(Media media) {
        othereMedia = true;
        if (mMediaPlayer == null) {
            mMediaPlayer = getMediaPlayer(mContext);
        }
        mMediaPlayer.setMedia(media);
    }

    public void setSurface(SurfaceTexture surfaceTexture) {
        isAttached = true;
        surface = surfaceTexture;
        if (isSufaceDelayerPlay) {
            isSufaceDelayerPlay = false;
            startPlay(path);
        }
    }

    public void onSurfaceTextureDestroyed() {
        isAttached = false;
        if (isAttachSurface) {
            isAttachSurface = false;
            pause();
            mMediaPlayer.getVLCVout().detachViews();
        }
    }

    public void onAttachedToWindow(boolean z) {
        isViewLife = z;
    }

    public void setLoop(boolean z) {
        isLoop = z;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void onStop() {
        mainHandler.post(new Runnable() {
            public void run() {
                if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                    VlcVideoPlayer.mediaListenerEvent.eventPlayInit(false);
                }
                VlcVideoPlayer.isViewLife = false;
            }
        });
        mVideoHandler.obtainMessage(9).sendToTarget();
    }

    private void opendVideo() {
        String str = path;
        if (str != null && !"".equals(str.trim())) {
            Context context = mContext;
            if (context != null && isAttached) {
                isSufaceDelayerPlay = false;
                canSeek = false;
                canPause = false;
                isPlayError = false;
                if (libVLC == null) {
                    libVLC = VLCInstance.get(context);
                }
                if (mMediaPlayer == null) {
                    if (isInstance) {
                        mMediaPlayer = getMediaPlayer(mContext);
                    } else {
                        mMediaPlayer = new MediaPlayer(VLCInstance.get(mContext));
                    }
                }
                if (!isAttachSurface && mMediaPlayer.getVLCVout().areViewsAttached()) {
                    mMediaPlayer.getVLCVout().detachViews();
                }
                if (!othereMedia) {
                    loadMedia();
                }
                if (!mMediaPlayer.getVLCVout().areViewsAttached() && isAttached && surface != null) {
                    isAttachSurface = true;
                    mMediaPlayer.getVLCVout().setVideoSurface(surface);
                    mMediaPlayer.getVLCVout().attachViews();
                    mMediaPlayer.getVLCVout().addCallback(this);
                    Logger.i(tag, "setVideoSurface   attachViews");
                }
                mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
                    public void onEvent(MediaPlayer.Event event) {
                        VlcVideoPlayer.this.onEventNative(event);
                    }
                });
                isInitPlay = true;
                othereMedia = false;
                String str2 = tag;
                StringBuilder sb = new StringBuilder();
                sb.append("isAttached=");
                sb.append(isAttached);
                sb.append(" isInitStart=");
                sb.append(isInitStart);
                Logger.i(str2, sb.toString());
                if (isAttached && isInitStart && isAttachSurface) {
                    mMediaPlayer.play();
                }
                return;
            }
        }
        mainHandler.post(new Runnable() {
            public void run() {
                if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                    VlcVideoPlayer.mediaListenerEvent.eventError(2, true);
                }
            }
        });
    }

    private void loadMedia() {
        if (isSaveState) {
            isSaveState = false;
            Media media = mMediaPlayer.getMedia();

            if (media != null && !media.isReleased()) {
                canSeek = true;
                canPause = true;
                canInfo = true;
                return;
            }
        }
        if (path.contains("://")) {
            Media media2 = new Media(libVLC, Uri.parse(path));
            if (VERSION.SDK_INT <= 19) {
                media2.setHWDecoderEnabled(false, false);
            }
            media2.setEventListener(this.mMediaListener);
            media2.parseAsync(4, 5000);
            mMediaPlayer.setMedia(media2);
            media2.release();
        } else {
            Media media3 = new Media(libVLC, path);
            if (VERSION.SDK_INT <= 19) {
                media3.setHWDecoderEnabled(false, false);
            }
            media3.setEventListener(this.mMediaListener);
            mMediaPlayer.setMedia(media3);
            media3.release();
        }
    }

    public void saveState() {
        if (isInitPlay) {
            isSaveState = true;
            onStop();
        }
    }

    public void startPlay(String str) {
        path = str;
        isInitStart = true;
        mainHandler.post(new Runnable() {
            public void run() {
                VlcVideoPlayer.isViewLife = true;
                if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                    VlcVideoPlayer.mediaListenerEvent.eventPlayInit(true);
                }
            }
        });
        if (isAttached) {
            mVideoHandler.obtainMessage(8).sendToTarget();
        } else {
            isSufaceDelayerPlay = true;
        }
    }

    private void reStartPlay() {
        if (!isAttached || !isLoop || !isPrepare()) {
            mainHandler.post(new Runnable() {
                public void run() {
                    if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                        VlcVideoPlayer.mediaListenerEvent.eventStop(VlcVideoPlayer.isPlayError);
                    }
                }
            });
            return;
        }
        Logger.i(tag, "reStartPlay setMedia");
        MediaPlayer mediaPlayer = mMediaPlayer;
        mediaPlayer.setMedia(mediaPlayer.getMedia());
        if (isAttached) {
            mMediaPlayer.play();
        }
    }

    private void release() {
        Logger.i(tag, "release");
        canSeek = false;
        if (mMediaPlayer != null && isInitPlay) {
            String str = tag;
            StringBuilder sb = new StringBuilder();
            sb.append("release SaveState  isAttachSurface=");
            sb.append(isAttachSurface);
            Logger.i(str, sb.toString());
            isInitPlay = false;
            mMediaPlayer.getVLCVout().removeCallback(this);
            if (isAttachSurface) {
                isAttachSurface = false;
                mMediaPlayer.getVLCVout().detachViews();
            }
            if (!isSaveState) {
                Media media = mMediaPlayer.getMedia();
                if (media != null) {
                    media.setEventListener(null);
                    mMediaPlayer.stop();
                    Logger.i(tag, "release setMedia null");
                    mMediaPlayer.setMedia(null);
                    media.release();
                    isSaveState = false;
                }
            } else if (canPause) {
                mMediaPlayer.pause();
            }
            Logger.i(tag, "release over");
        }
        canPause = false;
        isInitStart = false;
    }

    public void onDestory() {
        MediaPlayer mediaPlayer = mMediaPlayer;
        if (mediaPlayer != null && !mediaPlayer.isReleased()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void onEventNative(final MediaPlayer.Event event) {
        switch (event.type) {
            case 256:
                String str = tag;
                StringBuilder sb = new StringBuilder();
                sb.append("MediaChanged=");
                sb.append(event.getEsChangedType());
                Logger.i(str, sb.toString());
                return;
            case MediaPlayer.Event.Opening /*258*/:
                Logger.i(tag, "Opening");
                canInfo = true;
                this.speed = 1.0f;
                mMediaPlayer.setRate(1.0f);
                return;
            case MediaPlayer.Event.Buffering /*259*/:
                if (event.getBuffering() == 100.0f) {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("MediaPlayer.Event.Buffering");
                    sb2.append(event.getBuffering());
                    Logger.i(str2, sb2.toString());
                }
                mainHandler.post(new Runnable() {
                    public void run() {
                        if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                            VlcVideoPlayer.mediaListenerEvent.eventBuffing(event.getBuffering(), event.getBuffering() < 100.0f);
                        }
                    }
                });
                return;
            case MediaPlayer.Event.Playing /*260*/:
                Logger.i(tag, "Playing");
                canInfo = true;
                mainHandler.post(new Runnable() {
                    public void run() {
                        if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                            VlcVideoPlayer.mediaListenerEvent.eventPlay(true);
                        }
                    }
                });
                if (!isAttached || !mMediaPlayer.getVLCVout().areViewsAttached()) {
                    Logger.i(tag, "surface");
                    MediaPlayer mediaPlayer = mMediaPlayer;
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        return;
                    }
                    return;
                }
                return;
            case MediaPlayer.Event.Paused /*261*/:
                mainHandler.post(new Runnable() {
                    public void run() {
                        if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                            VlcVideoPlayer.mediaListenerEvent.eventPlay(false);
                        }
                    }
                });
                Logger.i(tag, "Paused");
                return;
            case MediaPlayer.Event.Stopped /*262*/:
                canInfo = false;
                canSeek = false;
                canPause = false;
                String str3 = tag;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Stopped  isLoop=");
                sb3.append(isLoop);
                sb3.append("  ");
                Logger.i(str3, sb3.toString());
                reStartPlay();
                return;
            case MediaPlayer.Event.EndReached /*265*/:
                Logger.i(tag, "EndReached");
                return;
            case MediaPlayer.Event.EncounteredError /*266*/:
                isPlayError = true;
                canInfo = false;
                Logger.i(tag, "EncounteredError");
                mainHandler.post(new Runnable() {
                    public void run() {
                        if (VlcVideoPlayer.isViewLife && VlcVideoPlayer.mediaListenerEvent != null) {
                            VlcVideoPlayer.mediaListenerEvent.eventError(1, true);
                        }
                    }
                });
                return;
            case MediaPlayer.Event.TimeChanged /*267*/:
            case MediaPlayer.Event.PositionChanged /*268*/:
                return;
            case MediaPlayer.Event.SeekableChanged /*269*/:
                canSeek = event.getSeekable();
                String str4 = tag;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("SeekableChanged=");
                sb4.append(canSeek);
                Logger.i(str4, sb4.toString());
                return;
            case MediaPlayer.Event.PausableChanged /*270*/:
                canPause = event.getPausable();
                String str5 = tag;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("PausableChanged=");
                sb5.append(canPause);
                Logger.i(str5, sb5.toString());
                return;
            case MediaPlayer.Event.Vout /*274*/:
                String str6 = tag;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("Vout");
                sb6.append(event.getVoutCount());
                Logger.i(str6, sb6.toString());
                return;
            case MediaPlayer.Event.ESAdded /*276*/:
                Logger.i(tag, "ESAdded");
                return;
            case MediaPlayer.Event.ESDeleted /*277*/:
                Logger.i(tag, "ESDeleted");
                return;
            default:
                String str7 = tag;
                StringBuilder sb7 = new StringBuilder();
                sb7.append("event.type=");
                sb7.append(event.type);
                Logger.i(str7, sb7.toString());
                return;
        }
    }

    public boolean isPrepare() {
        return mMediaPlayer != null && isInitPlay && !isPlayError;
    }

    public boolean canControl() {
        return canPause && canInfo && canSeek;
    }

    public void start() {
        Logger.i(tag, "start");
        if (isPrepare()) {
            mMediaPlayer.play();
        }
    }

    public void pause() {
        if (isPrepare() && canPause) {
            mMediaPlayer.pause();
        }
    }

    public long getDuration() {
        if (!isPrepare() || !canInfo) {
            return 0;
        }
        return mMediaPlayer.getLength();
    }

    public long getCurrentPosition() {
        if (!isPrepare() || !canInfo) {
            return 0;
        }
        return (long) (((float) getDuration()) * mMediaPlayer.getPosition());
    }

    public void seekTo(long j) {
        if (isPrepare() && canSeek && !this.isSeeking) {
            this.isSeeking = true;
            mMediaPlayer.setTime(j);
            this.isSeeking = false;
        }
    }

    public void setABLoop(boolean z) {
        isABLoop = z;
    }

    public boolean setABLoop(long j, long j2) {
        if (!isABLoop || !isPrepare() || !canSeek) {
            abTimeStart = 0;
            abTimeEnd = 0;
            return false;
        } else if (j2 - j < 1000) {
            Logger.i(tag, "");
            return false;
        } else {
            abTimeStart = j;
            abTimeEnd = j2;
            mMediaPlayer.setTime(j);
            return true;
        }
    }

    public boolean isPlaying() {
        if (isPrepare()) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean setPlaybackSpeedMedia(float f) {
        if (isPrepare() && canSeek) {
            this.speed = f;
            mMediaPlayer.setRate(f);
            seekTo(getCurrentPosition());
        }
        return true;
    }

    public float getPlaybackSpeed() {
        if (!isPrepare() || !canSeek) {
            return this.speed;
        }
        return mMediaPlayer.getRate();
    }

    public void setPause(boolean z) {
        if (z) {
            pause();
        }
    }

    public void setMediaListenerEvent(MediaListenerEvent mediaListenerEvent2) {
        mediaListenerEvent = mediaListenerEvent2;
    }

    public void setVideoSizeChange(VideoSizeChange videoSizeChange2) {
        this.videoSizeChange = videoSizeChange2;
    }

    public void onNewLayout(IVLCVout iVLCVout, int i, int i2, int i3, int i4, int i5, int i6) {
        VideoSizeChange videoSizeChange2 = this.videoSizeChange;
        if (videoSizeChange2 != null) {
            videoSizeChange2.onVideoSizeChanged(i, i2, i3, i4, i5, i6);
        }
    }
}
