package github.chenupt.springindicator.sample;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import github.chenupt.springindicator.sample.Record;

import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class GuideFragment extends Fragment{



    private SeekBar seekBar;

    private WaveView waveView;

    Animation mShowAction;
    Animation  mHiddenAction;
    Animation mRotate;
    TextView timeCount;
    int oldCount;
    WaveView wave;
    int Mode;
    final int xiuxianModeId=1;
    final int jingsuModeId=2;
    private FileManager fm=new FileManager();
    final int running=1;
    final int stopped=2;
    final int pause=3;
    int State=stopped;



    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            if(State==running){
                timeCount.setText(String.format("%.1fs", (float) millisUntilFinished / 1000));//更新秒数
                waveView.setProgress(((int)(millisUntilFinished/600)));//更新进度条
            }
            else
                this.cancel();
        }
        @Override
        public void onFinish() {//计时完毕时触发
            if(State==running){
                oldCount=count;
                btnMain.callOnClick();
                pool.play(poolMap.get("ding"), 1.0f, 1.0f, 0, 0, 1.0f);
                new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("你在60秒内跳了"+oldCount+"下！")
                        .setContentText("分享到朋友圈吗？")
                        .setCancelText("完成")
                        .setConfirmText("打卡")
                        .setCustomImage(R.mipmap.ic_launcher)
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                ((MainActivity) getActivity()).wechatShare(1,"我在60秒内跳绳"+oldCount+"下！不服来战！"," ");
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
            else
                this.cancel();

        }

    }

    class TipsTimeCount extends CountDownTimer {
        public TipsTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {//计时完毕时触发
            this.cancel();
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            refreshTips();//更新提示语
        }
    }


    private TimeCount time;
    private TipsTimeCount tipTime;


    void close (){
        hide(btnOrange);
        hide(btnIndigo);
        isOpen = false;
//        btnMain.startAnimation(mShowAction);
        btnMain.setVisibility(View.VISIBLE);
    }

    void open() {
        show(btnOrange, 1, 150);
        show(btnIndigo, 5, 150);
        isOpen = true;
//        btnMain.startAnimation(mHiddenAction);
        btnMain.setVisibility(View.INVISIBLE);
    }

    private MainActivity.MyTouchListener mTouchListener = new MainActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            if(event.getAction() == MotionEvent.ACTION_UP){
                close();
            }
        }
    };

    public void refreshTips(){
        int n=1+(int)(Math.random()*5);
        switch (n){
            case 1:
                tvTip.setText("小心把手机摔坏了哦");
                break;
            case 2:
                tvTip.setText("听说跳绳可以瘦腿耶");
                break;
            case 3:
                tvTip.setText("加油！你已经消耗了"+String.format("%.1f",today*105.0/1000/64)+"杯啤酒的热量！");
                break;
            case 4:
                tvTip.setText("加油！你已经甩掉了"+String.format("%.1f",today*105.0/1000/300)+"块炸鸡的脂肪！");
                break;
            case 5:
                tvTip.setText("加油！你已经跳上了"+String.format("%.1f",today*0.13/3)+"层楼的高度！");
                break;
            default:
        }

    }


    TextView tvTip;
    ColorfulRingProgressView crpv;
    TextView tvPercent;
    TextView tvGoal;
    TextView tvFinished;
    TextView tvEnergy;
    private SoundPool pool;
    private MediaPlayer mp;
    private Map<String, Integer> poolMap;
    int count=0;
    int today;
    int goal;

    SensorManager sm;
    private static final int FORCE_THRESHOLD = 1500;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 400;
    private static final int SHAKE_COUNT = 2;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    private long mLastTime;
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private byte[]log=new byte[1024*1024];
    private  int index=0;
    private final static long DURATION_SHORT = 400;

    TextView btnMain;
    TextView btnOrange;
    TextView btnIndigo;

    private boolean isOpen = false;

    private final void hide( final View child) {
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .start();
    }

    private final void show(final View child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = ((FrameLayout)getView().findViewById(R.id.btFrame)).getWidth()/4;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                break;
            case 2:
                angleDeg += 45.f;
                break;
            case 3:
                angleDeg += 90.f;
                break;
            case 4:
                angleDeg += 135.f;
                break;
            case 5:
                angleDeg += 180.f;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                break;
        }

        final float angleRad = (float) (angleDeg * Math.PI / 180.f);

        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(x)
                .translationY(y)
                .start();
    }

    public void jumpSignal(int estimationT) {
        pool.play(poolMap.get("sound"), 1.0f, 1.0f, 0, 0, 1.0f);
        count++;
        tvPercent.setText(count + "");
//        today++;

//        if(Mode==xiuxianModeId) {
        crpv.setPercent((float) (count+today) * 100 / goal);
        mRotate.setDuration(400);
        crpv.startAnimation(mRotate);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goal=MainActivity.goal;
        today=MainActivity.records[0].count;

        //bgRes = getArguments().getInt("data");
        //在该Fragment的构造函数中注册mTouchListener的回调
                ((MainActivity) this.getActivity()).registerMyTouchListener(mTouchListener);

        ////it starts from here
    }
    @Override
    public void onResume(){
        super.onResume();
        goal=MainActivity.goal;
        today=MainActivity.records[0].count;
        crpv.setPercent(((float) today * 100) / goal);

    }


    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density*dp+0.5f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        time = new TimeCount(60000, 100);//构造CountDownTimer对象
        tipTime = new TipsTimeCount(90000000, 10000);//构造CountDownTimer对象
        tipTime.start();

        btnMain=(TextView)getView().findViewById(R.id.btn_main);
//        wave = (WaveView)getView().findViewById(R.id.waveView);
        btnOrange=(TextView)getView().findViewById(R.id.btn_orange);
        btnIndigo=(TextView)getView().findViewById(R.id.btn_indigo);


        waveView = (WaveView) getView().findViewById(R.id.wave_view);
        waveView.setProgress(0);



        mShowAction= AnimationUtils.loadAnimation(getActivity(), R.anim.show);
        mHiddenAction= AnimationUtils.loadAnimation(getActivity(), R.anim.hide);
        mRotate= AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);


        poolMap = new HashMap<String, Integer>();
        pool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        // 装载音频进音频池，并且把ID记录在Map中
        poolMap.put("sound", pool.load(getActivity(), R.raw.sound, 1));
        poolMap.put("fuck", pool.load(getActivity(), R.raw.fuck, 1));
        poolMap.put("ding", pool.load(getActivity(), R.raw.ding, 1));



        sm=(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        final  Sensor acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final SensorEventListener acceleromererListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //什么也不干
            }

            //传感器数据变动事件
            @Override
            public void onSensorChanged(SensorEvent event) {


                //获取加速度传感器的三个参数
                float x = event.values[SensorManager.DATA_X];
                float y = event.values[SensorManager.DATA_Y];
                float z = event.values[SensorManager.DATA_Z];


//                byte[]bx=fm.getBytes(x);
//                byte[]by=fm.getBytes(y);
//                byte[]bz=fm.getBytes(z);
//                for(int i=0;i<4;i++)
//                {
//                    log[index+i]=bx[i];
//                    log[index+4+i]=by[i];
//                    log[index+8+i]=bz[i];
//                }
//                index+=12;



                long now = System.currentTimeMillis();

                if ((now - mLastForce) > SHAKE_TIMEOUT) {
                    mShakeCount = 0;
                }

                if ((now - mLastTime) > TIME_THRESHOLD) {
                    long diff = now - mLastTime;
                    float speed = Math.abs(event.values[SensorManager.DATA_X] + event.values[SensorManager.DATA_Y] + event.values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
                    if (speed > FORCE_THRESHOLD) {
                        if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                            mLastShake = now;
                            mShakeCount = 0;
                            if (true) {
                                jumpSignal(400);
                            }
                        }
                        mLastForce = now;
                    }
                    mLastTime = now;
                    mLastX = event.values[SensorManager.DATA_X];
                    mLastY = event.values[SensorManager.DATA_Y];
                    mLastZ = event.values[SensorManager.DATA_Z];
                }
            }
        };


        crpv = (ColorfulRingProgressView) getView().findViewById(R.id.crpv);
//        begin = (Button) getView().findViewById(R.id.begin);
        tvTip = (TextView) getView().findViewById(R.id.tvTip);
        tvGoal = (TextView) getView().findViewById(R.id.tvGoal);
//        timer = (Chronometer)getView().findViewById(R.id.chronometer);
//        this.registerForContextMenu(timer);
        tvGoal.setText("目标\n"+goal+"个");
        timeCount=(TextView)getView().findViewById(R.id.time);


        tvFinished=(TextView) getView().findViewById(R.id.tvFinished);
        tvEnergy=(TextView) getView().findViewById(R.id.tvEnergy);
        tvPercent = (TextView) getView().findViewById(R.id.tvPercent);

        refreshTips();

        tvFinished.setText("今日累计\n" + today + "个");
        tvEnergy.setText("热量\n" + String.format("%.1f", today * 105.0 / 1000) + "卡");

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (State == stopped) {
                    open();
                } else if (State == running) {
                    State = stopped;
                    tvTip.setText("本次共跳了" + count + "下！继续加油！");
                    if (Mode == jingsuModeId) {
                        timeCount.setVisibility(view.INVISIBLE);
//                        tipTime.cancel();
                        time.cancel();
                        mp.stop();
                    }
                    sm.unregisterListener(acceleromererListener);
//                    btnMain.setText("确认");
//                    btnMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.states_circle_yellow));
                    today += count;
                    ((MainActivity) getActivity()).total+=count;
                    MainActivity.records[0].count = today;
                    ((MainActivity) getActivity()).saveData();
                    if(MainActivity.goal<=today)//&&Mode==xiuxianModeId
                    {
                        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                        Calendar ca = Calendar.getInstance();
                        final int year=ca.get(Calendar.YEAR);
                        final int month=ca.get(Calendar.MONTH)+1;
                        final int day=ca.get(Calendar.DATE);
                        if(((MainActivity) getActivity()).checkLog(year,month,day))
                        {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("恭喜你带到今天的目标"+MainActivity.goal+"个！")
                                    .setContentText("分享到朋友圈吗？")
                                    .setCancelText("完成")
                                    .setConfirmText("打卡")
                                    .setCustomImage(R.mipmap.ic_launcher)
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel();
                                        }
                                    })
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            ((MainActivity) getActivity()).unpdateLog();
                                            ((MainActivity) getActivity()).wechatShare(1, "我今天用空气跳绳跳了" + MainActivity.goal + "下！你呢？", " ");
                                            sDialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                    }
                    count = 0;
                    crpv.setPercent(((float) today * 100) / goal);
                    tvPercent.setText(0 + "");
                    btnMain.setText("开始");
                    btnMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.states_circle_green));
                    tvFinished.setText("今日累计\n" + today + "个");
                    tvEnergy.setText("热量\n" + String.format("%.1f", today * 105.0 / 1000) + "卡");
                    waveView.setProgress(0);
                }
//                else if (State == pause) {
//                    State = stopped;
//                    tipTime.start();
////                    tvTip.setText("Tips");
////                    fm.setcount(count, "record.txt");
////                    fm.writelog(log, "log.docx", index);
//                    today += count;
//                    ((MainActivity) getActivity()).total+=count;
//                    MainActivity.records[0].count = today;
//                    ((MainActivity) getActivity()).saveData();
//                    count = 0;
//                    crpv.setPercent(((float) today * 100) / goal);
//                    tvPercent.setText(0 + "");
//                    btnMain.setText("开始");
//                    btnMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.states_circle_green));
//                    tvFinished.setText("今日累计\n" + today + "个");
//                    tvEnergy.setText("热量\n" + String.format("%.1f", today * 105.0 / 1000) + "卡");
//                    waveView.setProgress(0);
//                }
            }
        });

//        crpv.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        jumpSignal(400);
//                                    }
//                                }
//
//
//        );

        btnOrange.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Mode = xiuxianModeId;
                                             State = running;
                                             Toast.makeText(getActivity(), "拿起手机开始跳吧！", Toast.LENGTH_SHORT).show();
                                             sm.registerListener(acceleromererListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                                             btnMain.setText("停止");
                                             btnMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.states_circle_red));
                                         }
                                     }
        );
        btnIndigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode=jingsuModeId;
                State = running;
                timeCount.setVisibility(View.VISIBLE);
                time.start();//开始计时
                mp = MediaPlayer.create(getActivity(),R.raw.fuck);
                mp.start();
                Toast.makeText(getActivity(), "拿起手机开始跳吧！", Toast.LENGTH_SHORT).show();
                sm.registerListener(acceleromererListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                btnMain.setText("停止");
                btnMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.states_circle_red));

            }
        });
    }


}


