package com.example.memodemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class VoiceActivity extends Activity {

    @BindView(R.id.anjian_btn)
    Button anjian_btn;

    @BindView(R.id.text)
    TextView text;

    //多线程后台处理
    private ExecutorService mExecutorService;
    private MediaRecorder mMediaRecorder;
    private File mAudioFile;
    private long mStartRecordTime, mStopRecordTime;
    private Handler mMainThreadHandler;//主线程
    //主线程和后台播放线程数据同步
    private volatile boolean mIsPlaying;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
        setContentView(R.layout.record);

    ButterKnife.bind(this);
    //录音jni函数不具备线程安全性，所以调用单线程
    mExecutorService = Executors.newSingleThreadExecutor();
    mMainThreadHandler = new Handler(Looper.getMainLooper());

    //按下说话，释放发送所以我们不要onClickListener
        anjian_btn.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //根据不同的touch action，执行不同的逻辑
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecord();
                    break;
                default:
                    break;
            }
            //处理了touch事件，返回true
            return true;
        }
    });
}
//利用butterknife简化代码
    @OnClick(R.id.play_btn)
    public void play(){
        //检查当前状态，防止重复播放
        if(mAudioFile!=null&&!mIsPlaying){
            //设置当前播放状态
            mIsPlaying = true;
            //提交后台任务，开始播放
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    doPlay(mAudioFile);
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //activity销毁时，停止后台任务，避免内存泄漏
        mExecutorService.shutdownNow();
        releaseRecorder();
        stopPlay();
    }
    /***
     *
     * 开始录音
     */
    private void startRecord() {
        //改变UI状态
        anjian_btn.setText("正在说话");
        //anjian_btn.setBackgroundResource();

        //提交后台任务，执行录音逻辑
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //释放之前录音的MediaRecorder
                releaseRecorder();
                //执行录音逻辑，如果失败提示用户
                if (!doStart()) {
                    recordFail();
                }
            }
        });
    }
    /***
     *
     * 停止录音
     */
    private void stopRecord() {
        //改变UI状态
        anjian_btn.setText("按住说话");
        //anjian_btn.setBackgroundResource();
        //提交后台任务，执行停止逻辑
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //执行停止录音逻辑，失败需要提醒用户
                if (!doStop()) {
                    recordFail();

                }
                //释放MediaRecorder
                releaseRecorder();
            }

        });
    }
    /**
     * 启动录音逻辑
     */
    private boolean doStart() {

        try {
            //创建MediaRecorder
            mMediaRecorder = new MediaRecorder();
            //创建录音文件
            //获取绝对路径

            mAudioFile = new File(getExternalFilesDir(null).getAbsolutePath() +
                    "/liuming/" + System.currentTimeMillis() + ".m4a");

            mAudioFile.getParentFile().mkdirs();
            mAudioFile.createNewFile();

            //配置MediaRecorder

            //(1)//从麦克风采集
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //(2)保存文件为MP4格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            //(3)所有android系统都支持的采样频率
            mMediaRecorder.setAudioSamplingRate(44100);

            //通用的AAC编码格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            //音质比较好的一个频率
            mMediaRecorder.setAudioEncodingBitRate(96000);

            //设为文件保存位置
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());

            //开始录音
            mMediaRecorder.prepare();//准备
            mMediaRecorder.start();//开始

            //记录开始录音的时间，用于统计时长
            mStartRecordTime = System.currentTimeMillis();

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            //捕获异常，避免闪退，返回false提醒用户失败
            return false;
        }
        //录音成功
        return true;
    }
    /**
     * 停止录音逻辑
     */
    private boolean doStop() {
        try {
            //停止录音
            mMediaRecorder.stop();
            //记录停止时间，统计时长
            mStopRecordTime = System.currentTimeMillis();

            //只接受超过三秒的录音，在ui上显示出来
            final int second = (int) ((mStopRecordTime - mStartRecordTime) / 1000);
            if (second > 3) {
                //text.setText(text.getText() + "\n录音成功" + second + "秒");
                //在主线程改ui，显示出来
                mMainThreadHandler. post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(text.getText()+"\n录音成功"+second+"秒");
                    }
                });
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            //捕获异常，避免闪退，返回false提醒用户失败
            return false;
        }
        //停止成功
        return true;
    }
    /**
     * 释放MediaRecorder
     */
    private void releaseRecorder() {
        //检查MediaRecorder不为null，
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    /**
     * 录音错误处理
     */
    private void recordFail() {
        mAudioFile = null;
        //给用户Toast提示失败,要在主线程执行
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VoiceActivity.this, "录音失败", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 实际播放逻辑
     * @param audioFile
     */
    private void doPlay(File audioFile) {

        //配置播放器MediaPlayer
        mMediaPlayer = new MediaPlayer();
        try {
            //设置声音文件
            mMediaPlayer.setDataSource(audioFile.getAbsolutePath());

            //设置监听回调。
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放结束，释放播放器
                    stopPlay();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //提示用户
                    playFail();

                    //释放播放器
                    stopPlay();

                    //错误已经处理，返回true
                    return true;
                }
            });
            //设置音量，是否循环
            mMediaPlayer.setVolume(1,1);//,1,1左右声道
            mMediaPlayer.setLooping(false);//不需要重复播放
            //  准备，开始
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (RuntimeException | IOException e){
            //异常处理，防止闪退
            e.printStackTrace();
            //提醒用户
            playFail();
            //释放播放器
            stopPlay();
        }
    }
    /**
     *
     * 停止播放逻辑
     */
    private void stopPlay() {

        //重置播放状态
        mIsPlaying = false;
        //释放播放器
        if(mMediaPlayer!=null){
            //设置监听器，防止内存泄漏
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);

            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }
    /**
     *
     *提醒用户播放失败
     */
    private void playFail() {
        //在主线程Toast提示用户失败
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VoiceActivity.this,"播放失败",Toast.LENGTH_LONG).show();
            }
        });
    }

}