package github.chenupt.springindicator.sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
public class FileManager {
    private Context context;
    /** SD卡是否存在**/
    private boolean hasSD = false;
    /** SD卡的路径**/
    private String SDPATH;
    /** 当前程序包的路径**/
   // private String FILESPATH;
    public FileManager() {
       //this.context = context;
        hasSD = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        SDPATH = Environment.getExternalStorageDirectory().getPath();
       // FILESPATH = this.context.getFilesDir().getPath();
    }
    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + "//" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
    public void setlog(String str,String filename)
    {
        String data=readSDFile(filename);
        data+=str;
        writeSDFile(data,filename);
    }
    /**
     * 删除SD卡上的文件
     *
     * @param fileName
     */
    public boolean deleteSDFile(String fileName) {
        File file = new File(SDPATH + "//" + fileName);
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        return file.delete();
    }
    /**
     * 写入内容到SD卡中的txt文本中
     * str为内容
     */

    public void setcount(int count,String filename)
    {
        String data=readSDFile(filename);
        Calendar ca = Calendar.getInstance();
        int year=ca.get(Calendar.YEAR);
        int month=ca.get(Calendar.MONTH)+1;
        int day=ca.get(Calendar.DATE);
        String towrite="";
        String record=""+year+" "+month+" "+day+" "+count+"\n";
        if(data=="")
            writeSDFile(record,filename);
        else
        {
            String[]item=data.split("\n");
            String[]itemdata=item[item.length-1].split(" ");
            int thisyear=Integer.parseInt(itemdata[0]);
            int thismonth=Integer.parseInt(itemdata[1]);
            int thisday=Integer.parseInt(itemdata[2]);
            int thiscount=Integer.parseInt(itemdata[3]);
            if(year==thisyear&&month==thismonth&&day==thisday)
            {
                count=count+thiscount;
                item[item.length-1]=""+year+" "+month+" "+day+" "+count;
                for(int i=0;i<item.length;i++)
                    towrite+=item[i]+"\n";
            }
            else
                towrite=data+year+" "+month+" "+day+" "+count+"\n";;

            writeSDFile(towrite,filename);
        }


    }
    public void writeSDFile(String str,String fileName)
    {
        try {
            FileWriter fw = new FileWriter(SDPATH + "//" + fileName);
            File f = new File(SDPATH + "//" + fileName);
            fw.write(str);
            FileOutputStream os = new FileOutputStream(f);
            DataOutputStream out = new DataOutputStream(os);
            out.writeShort(2);
            out.writeUTF("");
            System.out.println(out);
            fw.flush();
            fw.close();
            System.out.println(fw);
        } catch (Exception e) {
        }
    }
    public void writelog(byte[] data,String fileName,int size)
    {
        FileOutputStream fos = null;

        try {
            createSDFile(fileName);
            fos = new FileOutputStream(SDPATH + "//" + fileName);
            fos.write(data,0,size);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public  byte[] getBytes(float data)
    {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }
    public  byte[] getBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }
    /**
     * 读取SD卡中文本文件
     *
     * @param fileName
     * @return
     */
    public String showdata(String fileName) {
        String data="     日期      次数\n";
        try {
            File file = new File(SDPATH + "//" + fileName);
            if(!file.exists())
            {
                file.createNewFile();
                return data;
            }
            FileInputStream is=new FileInputStream(file);
            InputStreamReader fis = new InputStreamReader(is);
            BufferedReader bfr =new BufferedReader(fis);
            String record=bfr.readLine();
                while(record!=null)
                {
                    String[] item=record.split(" ");
                    int year=Integer.parseInt(item[0]);
                    int month=Integer.parseInt(item[1]);
                    int day=Integer.parseInt(item[2]);
                    int count=Integer.parseInt(item[3]);
                    data+=""+year+"年"+month+"月"+day+"日    "+count+"次\n";
                    record=bfr.readLine();
                }
            } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    public String readSDFile(String fileName)
    {
        String data="";
        try {
            File file = new File(SDPATH + "//" + fileName);
            if(!file.exists())
            {
                file.createNewFile();
                return data;
            }
            FileInputStream is=new FileInputStream(file);
            InputStreamReader fis = new InputStreamReader(is);
            BufferedReader bfr =new BufferedReader(fis);
            String record=bfr.readLine();
            while(record!=null)
            {
                data+=record+"\n";
                record=bfr.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }



    public String getSDPATH() {
        return SDPATH;
    }
    public boolean hasSD() {
        return hasSD;
    }

}