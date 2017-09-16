package github.chenupt.springindicator.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class Setgoal extends ActionBarActivity {
    SeekBar seekBar;
    Toolbar toolbar;
    TextView curgoal;
    TextView setgoal;

    public void writeFile(String record) throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences("count", Context.MODE_PRIVATE).edit();
        //步骤2-2：将获取过来的值放入文件
        editor.putString("record", record);
        editor.commit();
    }
    public void saveData(){
        //@CLF
        String towrite=""+MainActivity.goal+" "+MainActivity.total+"\n";

        for(int i=0;i<MainActivity.records.length;i++)
        {
            towrite+=""+MainActivity.records[i].year+" "+MainActivity.records[i].month+" "+MainActivity.records[i].date+" "+MainActivity.records[i].count+"\n";
        }
        try {
            writeFile(towrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setgoal);
        Typeface typeFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/numberFont.otf");
        ((TextView)findViewById(R.id.cur_goal)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.current2)).setTypeface(typeFace);

    }

    private int map(int p){
        if(p<5)
            return 200;
        else if(p<10)
            return 300;
        else if(p<15)
            return 400;
        else if(p<20)
            return 500;
        else if(p<25)
            return 600;
        else if(p<30)
            return 700;
        else if(p<35)
            return 800;
        else if(p<40)
            return 900;
        else if(p<45)
            return 1000;
        else if(p<50)
            return 1200;
        else if(p<55)
            return 1400;
        else if(p<60)
            return 1600;
        else if(p<65)
            return 1800;
        else if(p<70)
            return 2000;
        else if(p<75)
            return 2500;
        else if(p<80)
            return 3000;
        else if(p<85)
            return 3500;
        else if(p<90)
            return 4000;
        else if(p<95)
            return 4500;
        else
            return 5000;

    }

    private int inverseMap(int p){
        if(p<201)
            return 0;
        else if(p<301)
            return 5;
        else if(p<401)
            return 15;
        else if(p<501)
            return 20;
        else if(p<601)
            return 25;
        else if(p<701)
            return 30;
        else if(p<801)
            return 35;
        else if(p<901)
            return 40;
        else if(p<1001)
            return 45;
        else if(p<1201)
            return 50;
        else if(p<1401)
            return 55;
        else if(p<1601)
            return 60;
        else if(p<1801)
            return 65;
        else if(p<2001)
            return 70;
        else if(p<2501)
            return 75;
        else if(p<3001)
            return 80;
        else if(p<3501)
            return 85;
        else if(p<4001)
            return 90;
        else if(p<4501)
            return 95;
        else
            return 100;

    }


    @Override
    protected void onResume() {
        super.onResume();
        seekBar = (SeekBar) findViewById(R.id.myseekbar);
        setgoal = (TextView) findViewById(R.id.setgoal);
        curgoal=(TextView)findViewById(R.id.cur_goal);
        toolbar = (Toolbar) findViewById(R.id.setgoalbar);
        toolbar.setTitle("设定目标");
        curgoal.setText("" + MainActivity.goal);
        seekBar.setProgress(inverseMap(MainActivity.goal));
        ((TextView)findViewById(R.id.tvEnergy2)).setText(String.format("%.1f", MainActivity.goal * 105.0 / 1000) + "卡");
        ((TextView)findViewById(R.id.tvTime2)).setText(String.format("%d", (int) MainActivity.goal / 70) + "分钟");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int goal=map(seekBar.getProgress());
                curgoal.setText("" + goal);
                ((TextView)findViewById(R.id.tvEnergy2)).setText(String.format("%.1f", goal * 105.0 / 1000) + "卡");
                ((TextView)findViewById(R.id.tvTime2)).setText(String.format("%d", (int) goal / 70) + "分钟");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setgoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int goal=map(seekBar.getProgress());
                MainActivity.goal=goal;
                saveData();
                Intent in=new Intent(Setgoal.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setgoal, menu);
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
