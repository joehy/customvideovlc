package com.joe.customvideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;
import java.util.ArrayList;

public class joevideo  extends RelativeLayout{
    private VLCVideoLayout vlcVideoLayout;
    private ImageView pause;
    private SeekBar seekBar;
    private double current_pos, total_duration;
    private TextView current, total;
    private RelativeLayout showProgress;
    private Handler mHandler,handler;
    private boolean isVisible = false;
    private RelativeLayout relativeLayout;
    private Context mContext;
    private AttributeSet attrs;
    private  int defStyleAttr;
    private  int defStyleRes;
    private onVisiblatyChange onVisiblatyChange;
    private onVideoStartPlaying onVideoStartPlaying;
    private onVideoFinished onVideoFinished;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private boolean firstTime=true;
    private String currentVideoUri;
    private boolean isLocal;
    private ArrayList<View> viewArrayList;

    public joevideo(Context context) {
        super(context);
        this.mContext=context;
        initView();
    }
    public joevideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        this.attrs=attrs;
        initView();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public joevideo(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext=context;
        this.attrs=attrs;
        this.defStyleAttr=defStyleAttr;
        this.defStyleRes=defStyleRes;
        initView();
    }
    public joevideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        this.attrs=attrs;
        this.defStyleAttr=defStyleAttr;
        initView();
    }
    private void initView() {
        View view = this;
        inflate(mContext, R.layout.joevideo,this);
        vlcVideoLayout= view.findViewById(R.id.videoView);
        viewArrayList = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("--no-drop-late-frames");
        arrayList.add("--no-skip-frames");
        arrayList.add("--rtsp-tcp");
        arrayList.add("-vvv");
        libVLC = new LibVLC(getContext(), arrayList);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        pause = view.findViewById(R.id.pause);
        seekBar = view.findViewById(R.id.seekbar);
        current = view.findViewById(R.id.current);
        total =  view.findViewById(R.id.total);
        showProgress =  view.findViewById(R.id.showProgress);
        relativeLayout =  view.findViewById(R.id.full_layout);
        mHandler = new Handler();
        handler = new Handler();
        showProgress.setVisibility(GONE);
        setPauseButton();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                mediaPlayer.setTime((long) current_pos);
            }
        });
        mediaPlayer.setEventListener(event -> {
            if (event.type==MediaPlayer.Event.Playing) {
                if (firstTime) {
                    current_pos = mediaPlayer.getTime();
                    total_duration = mediaPlayer.getLength();
                    total.setText(timeConversion((long) total_duration));
                    current.setText(timeConversion((long) current_pos));
                    seekBar.setMax((int) total_duration);
                    if (onVideoStartPlaying != null) {
                        onVideoStartPlaying.onVideoStartPlaying();
                    }
                    firstTime=false;
                }
                if (!mediaPlayer.getVLCVout().areViewsAttached()) {
                    mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
                }
            }
            if (event.getSeekable()) {
                current_pos = mediaPlayer.getTime();
                current.setText(timeConversion((long) current_pos));
                seekBar.setProgress((int) current_pos);
            }
            if (event.type==MediaPlayer.Event.EndReached) {
                pause.setImageResource(R.drawable.play);
                if (isLocal){
                    setVideoUri(currentVideoUri);
                }else {
                    setVideoUri(Uri.parse(currentVideoUri));
                }
                if (onVideoFinished!=null){
                    onVideoFinished.onVideoFinished();
                }
            }
        });
        mediaPlayer.setEventListener(event -> {
            if (event.type==MediaPlayer.Event.Playing) {
                if (firstTime) {
                    current_pos = mediaPlayer.getTime();
                    total_duration = mediaPlayer.getLength();
                    total.setText(timeConversion((long) total_duration));
                    current.setText(timeConversion((long) current_pos));
                    seekBar.setMax((int) total_duration);
                    if (onVideoStartPlaying != null) {
                        onVideoStartPlaying.onVideoStartPlaying();
                    }
                    firstTime=false;
                }
                if (!mediaPlayer.getVLCVout().areViewsAttached()) {
                    mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
                }
            }
            if (event.getSeekable()) {
                current_pos = mediaPlayer.getTime();
                current.setText(timeConversion((long) current_pos));
                seekBar.setProgress((int) current_pos);
            }
            if (event.type==MediaPlayer.Event.EndReached) {
                pause.setImageResource(R.drawable.play);
                if (isLocal){
                    setVideoUri(currentVideoUri);
                }else {
                    setVideoUri(Uri.parse(currentVideoUri));
                }
                if (onVideoFinished!=null){
                    onVideoFinished.onVideoFinished();
                }
            }
        });
    }
    public  void hide(){
        Log.d("vlcVideoLayout", String.valueOf(current_pos));
        viewArrayList.clear();
        for (int i = 0; i <  vlcVideoLayout.getChildCount(); i++) {
            View v = vlcVideoLayout.getChildAt(i);
            viewArrayList.add(v);
        }
        vlcVideoLayout.removeAllViewsInLayout();
    }
    public  void display(){
        Log.d("vlcVideoLayout", String.valueOf(current_pos));
        for (int i = 0; i < viewArrayList.size(); i++) {
            vlcVideoLayout.addView(viewArrayList.get(i));
        }
        Log.d("vlcVideoLayout", String.valueOf(current_pos));
        mediaPlayer.setTime((long) current_pos);
    }
    private void setPauseButton() {
        pause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pause.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.play();
                pause.setImageResource(R.drawable.pause);
            }
        });
    }
    @SuppressLint("DefaultLocale")
    private String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;
        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }
    public void showController() {
        final Runnable runnable = () -> {
            showProgress.setVisibility(View.GONE);
            isVisible = false;
            if (onVisiblatyChange!=null) {
                onVisiblatyChange.onVisiblatyChange(isVisible);
            }
        };
        handler.postDelayed(runnable, 5000);
        relativeLayout.setOnClickListener(v -> {
            mHandler.removeCallbacks(runnable);
            if (isVisible) {
                showProgress.setVisibility(View.GONE);
                isVisible = false;
            } else {
                showProgress.setVisibility(View.VISIBLE);
                mHandler.postDelayed(runnable, 5000);
                isVisible = true;
            }
            if (onVisiblatyChange!=null) {
                onVisiblatyChange.onVisiblatyChange(isVisible);
            }
        });
    }
    public void pause(){
        mediaPlayer.pause();
        pause.setImageResource(R.drawable.play);
    }
    public void release(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer.detachViews();
        vlcVideoLayout.removeAllViews();
        vlcVideoLayout.setVisibility(GONE);
    }
    public void start(){
        mediaPlayer.play();
        pause.setImageResource(R.drawable.pause);
    }
    public void getFirstFrame(){
        mediaPlayer.play();
        if (!mediaPlayer.getVLCVout().areViewsAttached()) {
            mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        }
        mediaPlayer.updateVideoSurfaces();
        mediaPlayer.setEventListener(event -> {
            if (event.getSeekable()) {
                mediaPlayer.pause();
            }
        });
    }
    public void setVideoUri(Uri cuttentVideoUri) {
        this.currentVideoUri =String.valueOf(cuttentVideoUri);
        isLocal=false;
        Media media=new Media(libVLC,cuttentVideoUri);
        mediaPlayer.setMedia(media);
    }
    public void setVideoUri(String cuttentVideoPath) {
        this.currentVideoUri = cuttentVideoPath;
        isLocal = true;
        Media media = new Media(libVLC, cuttentVideoPath);
        mediaPlayer.setMedia(media);
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void setOnControllerVisiblatyChangeListener(onVisiblatyChange onVisiblatyChange){
        this.onVisiblatyChange=onVisiblatyChange;
    }
    public void setOnvideoStartPlayingListener(onVideoStartPlaying onvideoStartPlaying){
        this.onVideoStartPlaying=onvideoStartPlaying;
    }
    public void setOnVideoFinishedListener(onVideoFinished onVideoFinished){
        this.onVideoFinished=onVideoFinished;
    }
    public interface onVisiblatyChange{
         void onVisiblatyChange(boolean visiable);
    }
    public interface onVideoStartPlaying{
         void onVideoStartPlaying();
    }
    public interface onVideoFinished{
         void onVideoFinished();
    }
}
