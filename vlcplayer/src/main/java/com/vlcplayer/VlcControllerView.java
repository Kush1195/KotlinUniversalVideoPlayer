package com.vlcplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.log.Logger;
import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

public class VlcControllerView extends FrameLayout {
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final String TAG = "VideoControllerView";
    public static View controller = null;
    public static ImageView mCenterImage = null;
    public static View mCenterLayout = null;
    public static ProgressBar mCenterProgress = null;
    private static final int sDefaultTimeout = 10000;
    public static TextView top_title;
    public static ImageButton bottom_next;
    public static ImageButton bottom_previous;
    ImageView btn_back,btn_for;
    private boolean isFullscreen;
    private Context mContext;

    public TextView mCurrentTime;

    public boolean mDragging;
    private TextView mEndTime;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private ImageButton mFullscreenButton;
    private OnClickListener mFullscreenListener;

    public Handler mHandler;

    public static ImageButton mPauseButton;
    private OnClickListener mPauseListener;

    public MediaPlayerControl mPlayer;
    public static ProgressBar mProgress;
    private OnSeekBarChangeListener mSeekListener;

    public boolean mShowing;
    public preVideo preVideo2;
    ImageView top_back;

    public interface MediaPlayerControl {
        void PreVideo();

        boolean canPause();

        int getBufferPercentage();

        int getCurrentPosition();

        int getDuration();

        boolean isPlaying();

        void nextVideo();

        void pause();

        void seekTo(int i);

        void start();

        void toggleFullScreen(boolean z);
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VlcControllerView> mView;

        MessageHandler(VlcControllerView vlcControllerView) {
            this.mView = new WeakReference<>(vlcControllerView);
        }

        public void handleMessage(Message message) {
            VlcControllerView vlcControllerView = (VlcControllerView) this.mView.get();
            if (vlcControllerView != null && vlcControllerView.mPlayer != null) {
                switch (message.what) {
                    case 1:
                        vlcControllerView.hide();
                        break;
                    case 2:
                        int access$800 = vlcControllerView.setProgress();
                        if (!vlcControllerView.mDragging && vlcControllerView.mShowing && vlcControllerView.mPlayer.isPlaying()) {
                            sendMessageDelayed(obtainMessage(2), (long) (1000 - (access$800 % 1000)));
                            break;
                        }
                }
            }
        }
    }

    public interface preVideo {
        void NextV();

        void back();

        void preV();

        void backW();

        void forW();
    }

    public VlcControllerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowing = false;
        this.isFullscreen = false;
        this.mHandler = new MessageHandler(this);
        this.mPauseListener = new OnClickListener() {
            public void onClick(View view) {
                if (!VlcControllerView.this.mPlayer.isPlaying()) {
                    VlcControllerView.this.mPauseButton.setImageResource(R.drawable.n_pause_button);
                } else {
                    VlcControllerView.this.mPauseButton.setImageResource(R.drawable.n_play_button);
                }
                VlcControllerView.this.doPauseResume();
                VlcControllerView.this.show(VlcControllerView.sDefaultTimeout);
            }
        };
        this.mFullscreenListener = new OnClickListener() {
            public void onClick(View view) {
                VlcControllerView.this.doToggleFullscreen();
                VlcControllerView.this.show(VlcControllerView.sDefaultTimeout);
            }
        };
        this.mSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
                VlcControllerView.this.show(600000);
                VlcControllerView.this.mDragging = true;
                VlcControllerView.this.mHandler.removeMessages(2);
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (VlcControllerView.this.mPlayer != null && z) {
                    int duration = (int) ((((long) VlcControllerView.this.mPlayer.getDuration()) * ((long) i)) / 1000);
                    VlcControllerView.this.mPlayer.seekTo(duration);
                    if (VlcControllerView.this.mCurrentTime != null) {
                        VlcControllerView.this.mCurrentTime.setText(VlcControllerView.this.stringForTime(duration));
                    }
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                VlcControllerView.this.mDragging = false;
                VlcControllerView.this.setProgress();
                VlcControllerView.this.updatePausePlay();
                VlcControllerView.this.show(VlcControllerView.sDefaultTimeout);
                VlcControllerView.this.mHandler.sendEmptyMessage(2);
            }
        };
        this.mContext = context;
        Log.i(TAG, TAG);
    }

    public VlcControllerView(Context context, boolean z) {
        super(context);
        this.mShowing = false;
        this.isFullscreen = false;
        this.mHandler = new MessageHandler(this);
        this.mPauseListener = new OnClickListener() {
            public void onClick(View view) {
                if (!VlcControllerView.this.mPlayer.isPlaying()) {
                    VlcControllerView.this.mPauseButton.setImageResource(R.drawable.n_pause_button);
                } else {
                    VlcControllerView.this.mPauseButton.setImageResource(R.drawable.n_play_button);
                }
                VlcControllerView.this.doPauseResume();
                VlcControllerView.this.show(VlcControllerView.sDefaultTimeout);
            }
        };
        this.mFullscreenListener = new OnClickListener() {
            public void onClick(View view) {
                VlcControllerView.this.doToggleFullscreen();
                VlcControllerView.this.show(VlcControllerView.sDefaultTimeout);
            }
        };

        mSeekListener = new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
                show(600000);
                mDragging = true;
                mHandler.removeMessages(2);
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (mPlayer != null && z) {
                    int duration = (int) ((((long) VlcControllerView.this.mPlayer.getDuration()) * ((long) i)) / 1000);
                    VlcControllerView.this.mPlayer.seekTo(duration);
                    if (VlcControllerView.this.mCurrentTime != null) {
                        VlcControllerView.this.mCurrentTime.setText(VlcControllerView.this.stringForTime(duration));
                    }
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mDragging = false;
                setProgress();
                updatePausePlay();
                show(VlcControllerView.sDefaultTimeout);
                mHandler.sendEmptyMessage(2);
            }
        };
        this.mContext = context;
        Log.i(TAG, TAG);
    }

    public VlcControllerView(Context context) {
        this(context, true);
        Log.i(TAG, TAG);
    }

    @SuppressLint({"MissingSuperCall"})
    public void onFinishInflate() {
        View view = controller;
        if (view != null) {
            initControllerView(view);
        }
    }

    public void setMediaPlayerListener(MediaPlayerControl mediaPlayerControl) {
        this.mPlayer = mediaPlayerControl;
        if (!this.mPlayer.isPlaying()) {
            this.mPauseButton.setImageResource(R.drawable.n_pause_button);
        } else {
            this.mPauseButton.setImageResource(R.drawable.n_play_button);
        }
        updatePausePlay();
        updateFullScreen();
    }

    public void setControllerView(VlcVideoView vlcVideoView) {
        StringBuilder sb = new StringBuilder();
        sb.append(vlcVideoView.getWidth() + vlcVideoView.getHeight());
        sb.append("");
//        Logger.e(sb.toString());
        controller = makeControllerView();
        addView(controller, new LayoutParams(-1, -1, 80));
    }

    @SuppressLint("WrongConstant")
    public View makeControllerView() {
        controller = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R.layout.layout_controller, null);
        initControllerView(controller);
        return controller;
    }

    @SuppressLint({"WrongConstant"})
    private void initControllerView(View view) {
        mCenterLayout = view.findViewById(R.id.layout_center);
        mCenterLayout.setVisibility(8);
        mCenterImage = (ImageView) view.findViewById(R.id.image_center_bg);
        btn_back = (ImageView) view.findViewById(R.id.btn_back);
        btn_for = (ImageView) view.findViewById(R.id.btn_for);
        mCenterProgress = (ProgressBar) view.findViewById(R.id.progress_center);
        mPauseButton = (ImageButton) view.findViewById(R.id.pause);
        bottom_previous = (ImageButton) view.findViewById(R.id.bottom_previous);
        bottom_next = (ImageButton) view.findViewById(R.id.bottom_next);
        top_title = (TextView) view.findViewById(R.id.top_title);
        top_back = (ImageView) view.findViewById(R.id.top_back);
        bottom_previous.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                preVideo2.preV();
                mPauseButton.setImageResource(R.drawable.n_pause_button);
            }
        });

        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                preVideo2.backW();
            }
        });

        btn_for.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                preVideo2.forW();
            }
        });

        bottom_next.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                preVideo2.NextV();
                mPauseButton.setImageResource(R.drawable.n_pause_button);
            }
        });
        top_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                preVideo2.back();
            }
        });
        ImageButton imageButton = mPauseButton;
        if (imageButton != null) {
            imageButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mFullscreenButton = (ImageButton) view.findViewById(R.id.fullscreen);
        ImageButton imageButton2 = mFullscreenButton;
        if (imageButton2 != null) {
            imageButton2.requestFocus();
            mFullscreenButton.setOnClickListener(mFullscreenListener);
        }
        this.mProgress = (SeekBar) view.findViewById(R.id.mediacontroller_progress);
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            if (progressBar instanceof SeekBar) {
                ((SeekBar) progressBar).setOnSeekBarChangeListener(this.mSeekListener);
            }
            this.mProgress.setMax(1000);
        }
        this.mEndTime = (TextView) view.findViewById(R.id.time);
        this.mCurrentTime = (TextView) view.findViewById(R.id.time_current);
        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(this.mFormatBuilder, Locale.getDefault());
    }

    public void startPreviousPlay(preVideo prevideo) {
        this.preVideo2 = prevideo;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    private void disableUnsupportedButtons() {
        MediaPlayerControl mediaPlayerControl = this.mPlayer;
        if (mediaPlayerControl != null) {
            try {
                if (this.mPauseButton != null && !mediaPlayerControl.canPause()) {
                    this.mPauseButton.setEnabled(false);
                }
            } catch (IncompatibleClassChangeError unused) {
            }
        }
    }

    public void show(int i) {
        if (!this.mShowing) {
            setProgress();
            ImageButton imageButton = this.mPauseButton;
            if (imageButton != null) {
                imageButton.requestFocus();
            }
            disableUnsupportedButtons();
            this.mShowing = true;
        }
        updatePausePlay();
        updateFullScreen();
        this.mHandler.sendEmptyMessage(2);
        Message obtainMessage = this.mHandler.obtainMessage(1);
        if (i != 0) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(obtainMessage, (long) i);
        }
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public void hide() {
        try {
            this.mHandler.removeMessages(2);
        } catch (IllegalArgumentException unused) {
            Log.w("MediaController", "already removed");
        }
        this.mShowing = false;
    }

    public String stringForTime(int i) {
        int i2 = i / 1000;
        int i3 = i2 % 60;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 / 3600;
        this.mFormatBuilder.setLength(0);
        if (i5 > 0) {
            return this.mFormatter.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3)}).toString();
        }
        return this.mFormatter.format("%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i3)}).toString();
    }

    public int setProgress() {
        MediaPlayerControl mediaPlayerControl = this.mPlayer;
        if (mediaPlayerControl == null || this.mDragging) {
            return 0;
        }
        int currentPosition = mediaPlayerControl.getCurrentPosition();
        int duration = this.mPlayer.getDuration();
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            if (duration > 0) {
                progressBar.setProgress((int) ((((long) currentPosition) * 1000) / ((long) duration)));
            }
            this.mProgress.setSecondaryProgress(this.mPlayer.getBufferPercentage() * 10);
        }
        TextView textView = this.mEndTime;
        if (textView != null) {
            textView.setText(stringForTime(duration));
        }
        TextView textView2 = this.mCurrentTime;
        if (textView2 != null) {
            textView2.setText(stringForTime(currentPosition));
        }
        return currentPosition;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        show(sDefaultTimeout);
        return true;
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        show(sDefaultTimeout);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent);
    }

    public void updatePausePlay() {
        if (controller != null && this.mPauseButton != null && this.mPlayer != null) {
        }
    }

    public void updateFullScreen() {
        if (controller != null) {
            ImageButton imageButton = this.mFullscreenButton;
            if (!(imageButton == null || this.mPlayer == null)) {
                if (this.isFullscreen) {
                    imageButton.setImageResource(R.drawable.rotate);
                } else {
                    imageButton.setImageResource(R.drawable.rotate);
                }
            }
        }
    }

    public void doPauseResume() {
        MediaPlayerControl mediaPlayerControl = mPlayer;
        if (mediaPlayerControl != null) {
            if (mediaPlayerControl.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
            updatePausePlay();
        }
    }

    public void doToggleFullscreen() {
        MediaPlayerControl mediaPlayerControl = mPlayer;
        if (mediaPlayerControl != null) {
            isFullscreen = ! isFullscreen;
            mediaPlayerControl.toggleFullScreen(isFullscreen);
        }
    }

    public void setEnabled(boolean z) {
        ImageButton imageButton = this.mPauseButton;
        if (imageButton != null) {
            imageButton.setEnabled(z);
        }
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setEnabled(z);
        }
        disableUnsupportedButtons();
        super.setEnabled(z);
    }
}
