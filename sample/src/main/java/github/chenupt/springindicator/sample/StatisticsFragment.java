/*
 * Copyright 2015 chenupt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.chenupt.springindicator.sample;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.content.res.Resources;
import android.os.Bundle;

import android.view.View;
import com.hrules.charter.CharterBar;
import com.hrules.charter.CharterLine;
import com.hrules.charter.CharterXLabels;
import com.hrules.charter.CharterYLabels;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import  java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.hrules.charter.CharterLine;
import com.hrules.charter.CharterXLabels;
import com.timqi.sectorprogressview.ColorfulRingProgressView;




/**
 * Created by chenupt@gmail.com on 2015/1/31.
 * Description TODO
 */
public class StatisticsFragment extends Fragment{

    private static final int DEFAULT_ITEMS_COUNT = 15;
    private static final int DEFAULT_RANDOM_VALUE_MIN = 10;
    private static final int DEFAULT_RANDOM_VALUE_MAX = 100;

    TextView tvMain;
    TextView tvShare;
    TextView tvSettings;
    TextView tvAbout;
    private final static long DURATION_SHORT = 400;

    void close (){
        hide(tvShare);
        hide(tvSettings);
        hide(tvAbout);

        isOpen = false;
//        btnMain.startAnimation(mShowAction);
        tvMain.setVisibility(View.VISIBLE);
    }

    void open() {
        show(tvSettings, 1, 200);
        show(tvShare, 2, 250);
        show(tvAbout, 3, 250);
        isOpen = true;
//        btnMain.startAnimation(mHiddenAction);
        tvMain.setVisibility(View.INVISIBLE);
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


    private boolean isOpen = false;

    private final void hide( final View child) {
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .start();
    }

    private final void show(final View child, final int position, int radius) {
        float angleDeg = 180.f;
        radius=((FrameLayout)getView().findViewById(R.id.buttonFrame)).getWidth()/3;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 90.f;
                dist=0;
                break;
            case 2:
                angleDeg += 180.f;
                break;
            case 3:
                angleDeg += 360.f;
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


    TextView tvTotalCount;
    TextView tvTotalHeight;
    TextView tvTotalEnergy;

    CharterXLabels charterLineLabelX;
    CharterYLabels charterLineLabelY;
    CharterLine charterLineWithLabel;

    Record records[];
    private float[] values;

    private ImageView imageView;
    private FileManager fm;
    private TextView text;
    private Button refresh;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) this.getActivity()).registerMyTouchListener(mTouchListener);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sta, container, false);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            records=MainActivity.getRecords();
            values=new float[7];
            values[0]=records[6].count;
            values[1]=records[5].count;
            values[2]=records[4].count;
            values[3]=records[3].count;
            values[4]=records[2].count;
            values[5]=records[1].count;
            values[6]=records[0].count;

            String[] date = new String[7];
            date[6]="今天";
            date[5]="昨天";
            date[4]=records[2].month+"."+records[2].date;
            date[3]=records[3].month+"."+records[3].date;
            date[2]=records[4].month+"."+records[4].date;
            date[1]=records[5].month+"."+records[5].date;
            date[0]=records[6].month+"."+records[6].date;
            charterLineWithLabel.setValues(values);
            charterLineWithLabel.show();
            charterLineLabelX.setValues(date);
            int max=0;

            for(int i=0; i<7; i++){
                if(records[i].count>max)
                    max=records[i].count;
            }
            charterLineLabelY.setValues(new float[]{0, max / 2, max});
            int total=MainActivity.total;
            tvTotalCount.setText(total+"个");
            tvTotalEnergy.setText(String.format("%.1f", total * 105.0 / 1000) + "卡");
            tvTotalHeight.setText(String.format("%.1f", total * 0.13) + "米");


        } else {
            //相当于Fragment的onPause
            if(charterLineWithLabel!=null) {
                charterLineWithLabel.setValues(new float[]{0, 0, 0, 0, 0, 0, 0});
                charterLineWithLabel.show();
            }
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        records=MainActivity.getRecords();


        tvShare=(TextView)getView().findViewById(R.id.tvShare);
        tvSettings=(TextView)getView().findViewById(R.id.tvSettings);
        tvMain=(TextView)getView().findViewById(R.id.tvMain);
        tvAbout=(TextView)getView().findViewById(R.id.tvAbout);
        tvTotalEnergy=(TextView)getView().findViewById(R.id.tvTotalEnergy);
        tvTotalCount=(TextView)getView().findViewById(R.id.tvTotalCount);
        tvTotalHeight=(TextView)getView().findViewById(R.id.tvTotalHeight);

        charterLineLabelX =
                    (CharterXLabels) getView().findViewById(R.id.charter_line_XLabel);
        charterLineLabelY =
                (CharterYLabels) getView().findViewById(R.id.charter_line_YLabel);
            charterLineLabelX.setStickyEdges(true);
            charterLineWithLabel =
                    (CharterLine) getView().findViewById(R.id.charter_line_with_XLabel);
        charterLineWithLabel.setLineColor(0xff86C1B5);
        charterLineWithLabel.setChartFillColor(0xFF68A899);
//            values =
//                    fillRandomValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN);


            values=new float[7];
            values[0]=records[6].count;
            values[1]=records[5].count;
            values[2]=records[4].count;
            values[3]=records[3].count;
            values[4]=records[2].count;
            values[5]=records[1].count;
            values[6]=records[0].count;

            String[] date = new String[7];
            date[6]="今天";
            date[5]="昨天";
            date[4]=records[2].month+"."+records[2].date;
            date[3]=records[3].month+"."+records[3].date;
            date[2]=records[4].month+"."+records[4].date;
            date[1]=records[5].month+"."+records[5].date;
            date[0]=records[6].month+"."+records[6].date;
            charterLineWithLabel.setValues(values);
            charterLineWithLabel.show();
            charterLineLabelX.setValues(date);


        int total=MainActivity.total;
        tvTotalCount.setText(total+"个");
        tvTotalEnergy.setText(String.format("%.1f", total * 105.0 / 1000) + "卡");
        tvTotalHeight.setText(String.format("%.1f", total * 0.13) + "米");


        int max=0;

        for(int i=0; i<7; i++){
            if(records[i].count>max)
                max=records[i].count;
        }
        charterLineLabelY.setValues(new float[]{0, max / 2, max});

//        ((ImageButton)getView().findViewById(R.id.btAbout)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getActivity(), AboutActivity.class);
//                startActivity(in);
//            }
//        });
//        ((ImageButton)getView().findViewById(R.id.btShare)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ((ImageButton)getView().findViewById(R.id.btSetting)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getActivity(),Setgoal.class);
//                startActivity(intent);
//            }
//        });
        tvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), AboutActivity.class);
                startActivity(in);
            }
        });
        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Setgoal.class);
                startActivity(intent);
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).wechatShare(0,"原来用手机也可以跳绳！快试试\"空气跳绳\"吧！","很新鲜很好玩！");
            }
        });


    }

}
