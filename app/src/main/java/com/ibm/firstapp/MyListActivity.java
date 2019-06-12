package com.ibm.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyListActivity extends ListActivity implements Runnable,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    Handler handler;
    private String TAG ="";
    private List<HashMap<String, String>> listItems;//存取文字、图片信息
    private SimpleAdapter listItemAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list);
        initListView();

//        MyAdapter myAdapter = new MyAdapter(this,R.layout.list_Item,listItems);
//        this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    listItems = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyListActivity.this,listItems,//listItems数据源
                            R.layout.list_item,//listItem的xml布局实现
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.ItemDetail
                    });
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            };
        };

//        ListView listView = (ListView) findViewById(R.id.mylist);
//        String data[] = {"aaaa","bbbbb"};
//
//        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
//        listView.setAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    //初始化数据
    private void initListView() {
        listItems = new ArrayList<HashMap<String,String>>();
        for (int i=0 ; i<10 ; i++){
            HashMap<String,String>map = new HashMap<String,String>();
            map.put("ItemTitle","Rate:" + i);//标题文字
            map.put("ItemDetail","Detail:" + i);//详情描述
            listItems.add(map);
            this.setListAdapter(listItemAdapter);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItems,//listItems数据源
                R.layout.list_item,//listItem的xml布局实现
                new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.ItemDetail});
    }
}

    @Override
    public void run() {
    //获取网络数据，放入list带回到主线程中
        List<HashMap<String,String>> retList = new ArrayList<HashMap<String, String>>();
        Document doc =null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run: " + doc.title());
            Elements tables =  doc.getElementsByTag("table");

            Element table2 = tables.get(1);
            //获取td中的数据
            Elements tds = table2.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);//币种
                Element td2 = tds.get(i+5);//汇率

                String str1 = td1.text();
                String val = td2.text();

                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                retList.add(map);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");

        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());

        //打开新的页面，传入参数
        Intent rateCalc = new Intent(this,RateListActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        //删除操作
        //listItems.remove(position);//先删除数据
        //listItemAdapter.notifyDataSetChanged();//再通知adapter刷新数据
        //构造对话框进行确认操作==>使用户操作时更保险
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");
                //删除操作（见151-152列）
                listItems.remove(position);//position的位置是148列的position，所以要将148的position改为final
                listItemAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("否",null);
        builder.create().show();
        Log.i(TAG, "onItemLongClick: size ="  + listItems.size());
        return true;
    }
}