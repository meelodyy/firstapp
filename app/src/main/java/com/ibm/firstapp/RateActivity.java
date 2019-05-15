package com.ibm.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable{
    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showRate);

        //获取sp里保存的数据（下面两列分别为两种方法）
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this); //通过这种方法获取sp里保存的数据，与上一列的方法所不一样的是，保存文件的名字(即上面的myrate)无法更改，是默认的
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

//        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
//        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
//        Log.i(TAG, "onCreate: sp wonRate=" + wonRate);
//        Log.i(TAG, "onCreate: sp updateDate=" + updateDate);
//        Log.i(TAG, "onCreate: todayStr=" + todayStr);

        //判断时间
        if(todayStr.equals(updateDate)){
//            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this); //this不能少，否则就不能调用到run方法
            t.start();
        }else{
//            Log.i(TAG, "onCreate: 不需要更新");
        }
        
        handler = new Handler(){
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    Bundle bdl = (Bundle)msg.obj;
//                    Log.i(TAG, "handleMessage: getMessage msg=" + str);
                    //show.setText(str);
                    dollarRate = bdl.getFloat("dollar_rate");
                    euroRate = bdl.getFloat("euro_rate");
                    wonRate = bdl.getFloat("won_rate");

                    Log.i(TAG, "handleMessage: dollarRate" + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate" + euroRate);
                    Log.i(TAG, "handleMessage: wonRate" + wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_arte",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.putString("update_date","todayStr");
                    editor.apply();

                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    public void onClick(View btn){
        //获取用户输入内容
//        Log.i(TAG, "onClick: ");
        String str = rmb.getText().toString();
//        Log.i(TAG, "onClick: get str=" + str);

        float r=0;
        if(str.length()>0){
            r=Float.parseFloat(str);
        }else{
            //用户没有输入内容
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }

//        Log.i(TAG, "onClick: r=" + r);

        //计算
        float val=0;
        if(btn.getId()==R.id.btn_dollar){
            val = r * dollarRate;
        }else if(btn.getId()==R.id.btn_euro){
            val = r * euroRate;
        }else{
            val = r * wonRate;
        }
        show.setText(String.valueOf(val));
    }

    public void openOne(View btn){
//        Log.i("open", "openOne: ");
//        Intent hello = new Intent(this,MainActivity.class);
//        Intent web = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.jd.com"));
//        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:12345678"));
//        startActivity(hello);
        openConfig();
    }

    private void openConfig(){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

//        Log.i("openOne", "dollar_rate_key: "+dollarRate);
//        Log.i("openOne", "euro_rate_key: "+euroRate);
//        Log.i("openOne", "won_rate_key: "+wonRate);

        //startActivity(config);
        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_rate) {
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口（同上面的openConfig方法，复制粘贴而来，没有参数传递，去掉config等代码，将activity改成RateListActivity,并将参数改为list）
            Intent list = new Intent(this,MyListActivity.class);

//        Log.i("openOne", "dollar_rate_key: "+dollarRate);
//        Log.i("openOne", "euro_rate_key: "+euroRate);
//        Log.i("openOne", "won_rate_key: "+wonRate);

            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1 && requestCode==2){
            Bundle bundle = data.getExtras();
            dollarRate =bundle.getFloat("key_dollar",0.1f);
            euroRate =bundle.getFloat("key_euro",0.1f);
            wonRate =bundle.getFloat("key_won",0.1f);

//            Log.i(TAG, "onActivityResult: dollarRate=" + dollarRate);
//            Log.i(TAG, "onActivityResult: euroRate=" + euroRate);
//            Log.i(TAG, "onActivityResult: wonRate=" + wonRate);

            //将新设置的汇率写到sp里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_arte",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
//            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(TAG, "run: run......");
        for(int i =1;i<6;i++){
            Log.i(TAG, "run: i" + i);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        //用于保存获取的汇率
        Bundle bundle;

        //获取Msg对象，用于返回主线程

        //获取网络数据
//        URL url = null;
//        try {
//            url = new URL("www.usd-cny.com/bankofchina.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//
//            String html = inputStream2String(in);
//            Log.i(TAG, "run: html" + html);
//            Document doc = Jsoup.parse(html);
//
//        }catch(MalformedURLException e) {
//            e.printStackTrace();
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        bundle = getFromBOC();

        //bundle中保存获取的数据

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        //msg.obj = "Data from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    /*
    从bankofchina中获取数据
     */
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: "+ doc.title());
            Elements tables =  doc.getElementsByTag("table");
//            for(Element table : tables){
//                Log.i(TAG, "run: table["+i+"]=" + table);
//                i++;
//            }
            Element table6 = tables.get(5);
            //Log.i(TAG, "run: table6=" + table6);
            //获取td中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);//币种
                Element td2 = tds.get(i+5);//汇率
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());

                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
//            for(Element td : tds){
//                Log.i(TAG, "run: td=" + td);
//                Log.i(TAG, "run: text=" + td.text());
//                Log.i(TAG, "run: html=" + td.html());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: "+ doc.title());
            Elements tables =  doc.getElementsByTag("table");
//            for(Element table : tables){
//                Log.i(TAG, "run: table["+i+"]=" + table);
//                i++;
//            }
            Element table6 = tables.get(5);
            //Log.i(TAG, "run: table6=" + table6);
            //获取td中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);//币种
                Element td2 = tds.get(i+5);//汇率
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());

                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
//            for(Element td : tds){
//                Log.i(TAG, "run: td=" + td);
//                Log.i(TAG, "run: text=" + td.text());
//                Log.i(TAG, "run: html=" + td.html());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws IOException{
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
