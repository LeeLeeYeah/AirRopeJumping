package github.chenupt.springindicator.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;


public class MainActivity extends ActionBarActivity {


    static public int goal;
    static public int total=0;
    static public Record records[]=new Record[7];
    SpringIndicator springIndicator;
    public static IWXAPI wxApi;
    public static final String APP_ID = "wx087d7b23be2d1a9b";



    public void wechatShare(int flag,String title,String des){


        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://mp.weixin.qq.com/s?__biz=MzI3MTEwOTQ1Nw==&mid=400402557&idx=1&sn=836adbca15025084b0f4c157fdd14371&scene=2&srcid=1202DUk0zOakdExvXUbENJ32&from=timeline&isappinstalled=0#wechat_redirect";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = des;

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;

        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession: SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }
    //写数据
    public void writeFile(final String record) throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences("count", Context.MODE_PRIVATE).edit();
        //步骤2-2：将获取过来的值放入文件
        editor.putString("record", record);
        editor.commit();
    }
    public void writeLog(final String record) throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences("count", Context.MODE_PRIVATE).edit();
        //步骤2-2：将获取过来的值放入文件
        editor.putString("Log", record);
        editor.commit();
    }
    public  void unpdateLog()
    {

        SharedPreferences read = getSharedPreferences("count", Context.MODE_PRIVATE);
        //步骤2：获取文件中的值
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar ca = Calendar.getInstance();
        int year=ca.get(Calendar.YEAR);
        int month=ca.get(Calendar.MONTH)+1;
        int day=ca.get(Calendar.DATE);
        String towrite = read.getString("Log", "NO");
        if(towrite=="NO")
            towrite=""+year+" "+month+" "+day+"\n";
        else
            towrite+=""+year+" "+month+" "+day+"\n";
        try {
            writeLog(towrite);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public  boolean checkLog(int year,int month,int day)
    {

        SharedPreferences read = getSharedPreferences("count", Context.MODE_PRIVATE);
        //步骤2：获取文件中的值
        String log;
        log = read.getString("Log", "NO");
        Toast toast;

        if(log=="NO")
            return true;
        else
        {
            String[]item=log.split("\n");
            for(int i=0;i<item.length;i++)
            {
                String[]itemdata=item[i].split(" ");
                if(itemdata.length==3)
                {
                    int thisyear=Integer.parseInt(itemdata[0]);
                    int thismonth=Integer.parseInt(itemdata[1]);
                    int thisdate=Integer.parseInt(itemdata[2]);
//                    toast = Toast.makeText(this,
//                            ""+thisyear+" "+thismonth+" "+thisdate,
//                            Toast.LENGTH_SHORT);
//                    toast.show();
                    if(thisyear==year&&thismonth==month&&thisdate==day)
                        return false;
                }
            }
        }
        return true;
    }
    //读数据
    public String readFile() throws IOException{
        SharedPreferences read = getSharedPreferences("count", Context.MODE_PRIVATE);
        //步骤2：获取文件中的值
        String value;
        value = read.getString("record", "NO");
        return value;
    }
    static int[]getdate(int year,int month,int day,int num)
    {
        int[][]monthdata={{31,28,31,30,31,30,31,31,30,31,30,31},{31,29,31,30,31,30,31,31,30,31,30,31}};
        int flag;
        if(year%4==0&&year%100!=0||year%400==0)
            flag=1;
        else
            flag=0;
        int[]date=new int[3];
        date[0]=year;
        date[1]=month;
        date[2]=day;
        if(day-num>0)
        {
            date[2]=date[2]-num;
            return date;
        }
        else if(month!=1)
        {
            date[1]=month-1;
            date[2]=monthdata[flag][month-2]-(num-day);
        }
        else
        {
            date[0]=year-1;
            date[1]=12;
            date[2]=31-(num-day);
        }
        return date;

    }
    public void saveData(){
        //@CLF
        String towrite=""+goal+" "+total+"\n";

        for(int i=0;i<records.length;i++)
        {
            towrite+=""+records[i].year+" "+records[i].month+" "+records[i].date+" "+records[i].count+"\n";
        }
        try {
            writeFile(towrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadData() throws IOException {
        //@CLF
        //有几天就几天record，没有记录的为null
        //如果中间有一天没跳，要补上count=0的数据
        //records[0]是今天

        //@CLF 这个变量做一下存取！！
        //total=0;


        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar ca = Calendar.getInstance();
        int year=ca.get(Calendar.YEAR);
        int month=ca.get(Calendar.MONTH)+1;
        int day=ca.get(Calendar.DATE);
        for(int i=0;i<7;i++)
        {
            int[]thisday=getdate(year,month,day,i);
            records[i]=new Record(thisday[0],thisday[1],thisday[2],0);
        }

        //FileManager fm = new FileManager();
        String mydata = "";
        mydata = readFile();

        if(mydata=="NO")
        {
            goal=200;
            total=0;
        }
        else
        {
            String[] item = mydata.split("\n");
            String[]summary=item[0].split(" ");
            if(summary.length==2)
            {
                goal = Integer.parseInt(summary[0]);
                total=Integer.parseInt(summary[1]);
            }

            for (int i = 1; i < 8; i++)
            {
                String[]itemdata=item[i].split(" ");
                int thisyear=Integer.parseInt(itemdata[0]);
                int thismonth=Integer.parseInt(itemdata[1]);
                int thisdate=Integer.parseInt(itemdata[2]);
                int thiscount=Integer.parseInt(itemdata[3]);
                for(int j=0;j<7;j++)
                {
                    if(records[j].date==thisdate&&records[j].month==thismonth&&records[j].year==thisyear)
                    {
                        records[j].count=thiscount;
                        break;
                    }
                }
            }

        }


    }


    static public Record[] getRecords(){

        return records;
    }

    ScrollerViewPager viewPager;

    /**
     * 回调接口
     * @author zhaoxin5
     *
     */
    public interface MyTouchListener
    {
        public void onTouchEvent(MotionEvent event);
    }

    /*
    * 保存MyTouchListener接口的列表
    */
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener)
    {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener)
    {
        myTouchListeners.remove( listener );
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected void onStop(){
        super.onStop();
        springIndicator.clrFlag();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, true);
        wxApi.registerApp(APP_ID);
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        springIndicator = (SpringIndicator) findViewById(R.id.indicator);


        PagerModelManager manager = new PagerModelManager();


        manager.addCommonFragment(GuideFragment.class, Lists.newArrayList(0), Lists.newArrayList("今日"));
        manager.addCommonFragment(StatisticsFragment.class, Lists.newArrayList(0), Lists.newArrayList("统计"));


        ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_about) {
//            Intent intent = new Intent(this, AboutActivity.class);
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
