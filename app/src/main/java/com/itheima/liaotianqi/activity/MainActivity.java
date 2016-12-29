package com.itheima.liaotianqi.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itheima.liaotianqi.R;
import com.itheima.liaotianqi.adapter.ChatItemListViewAdapter;
import com.itheima.liaotianqi.bean.ChatItemListViewBean;
import com.itheima.liaotianqi.database.MyDatabaseHelper;
import com.itheima.liaotianqi.view.EmojiView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//聊天界面
public class MainActivity extends AppCompatActivity {
 //   public static int account = 0;
    public static String myName;
    public static String friendName;
    private ImageView                  mImageView;
    private Button                     button;
    private EditText                   editText;
    private ListView                   listView;
    public  Handler                    handler;
    private Handler                    revHandler;
    private List<ChatItemListViewBean> list;
    private MyDatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private EmojiView mEmojiView;
    private int flag_mImageView = 1;//标记imageview的状态  -1为弹出状态，1为隐藏状态
    private GridView mGridView;
    private int[] imageIds = new int[107]; //别人的项目

    Socket s;
    Thread t;
    BufferedReader br = null;
    OutputStream    os = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_include);


        mGridView = (GridView) findViewById(R.id.gridView);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
//        for(int i=0;i<100;i++)
//        {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.ic_launcher);//添加图像资源的ID
////            map.put("ItemText", "NO."+String.valueOf(i));//按序号做ItemText
//            lstImageItem.add(map);
//        }
        //---------------借鉴于项目 ↓
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i = 0; i < 107; i++){
            try {
                if(i<10){
                    Field field = R.drawable.class.getDeclaredField("f00" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }else if(i<100){
                    Field field = R.drawable.class.getDeclaredField("f0" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }else{
                    Field field = R.drawable.class.getDeclaredField("f" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
        mGridView.setAdapter(simpleAdapter);

        //---------------借鉴于项目 ↑
/*        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,
                R.layout.layout_emoji_image,

                //动态数组与ImageItem对应的子项
                new String[] {"ItemImage","ItemText"},

                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.emoji_image,R.id.emoji_text});
        //添加并且显示
        mGridView.setAdapter(saImageItems);
        //添加消息处理*/
        mGridView.setOnItemClickListener(new ItemClickListener());

    //当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件





    //------------------
        list = new ArrayList<>();
        //数据库变量初始化
        mMyDatabaseHelper = new MyDatabaseHelper(this,"myRecord.db3",null,3);
        db = mMyDatabaseHelper.getReadableDatabase();
        //读取数据当作记录
        Cursor cursor = db.rawQuery("select * from record where id_myName like ? and id_friendName like ?"
                ,new String[]{MainActivity.myName,MainActivity.friendName});

        int account = 0;
        String str_time_temp = null;
        String str_time = null;
        while(cursor.moveToNext()){
            ChatItemListViewBean bean = new ChatItemListViewBean();
            bean.setText(cursor.getString(2));
            bean.setType(Integer.parseInt(cursor.getString(3)));
            if(account==0){
                str_time = cursor.getString(4);
                bean.SetTime(str_time);
                account++;
            }else if(account==1){
                    str_time_temp = cursor.getString(4);
                account++;
            }else if(account>1){
                    str_time = str_time_temp;
                    str_time_temp = cursor.getString(4);
                    Log.d("String_Date_temp",str_time_temp);
            }

            Log.d("String_Date",str_time);

            if(str_time_temp!=null && str_time_temp.compareTo(str_time)==1)
            bean.SetTime(cursor.getString(4));
            list.add(bean);
        }

        mEmojiView = (EmojiView) findViewById(R.id.emojiView);
        mImageView = (ImageView) findViewById(R.id.liaotianjiemian_imageView);
        button = (Button) findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.edit);
        listView = (ListView)findViewById(R.id.listView);
        final ChatItemListViewAdapter adapter = new ChatItemListViewAdapter(this,list);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount());
        //接受数据
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x123){
                    String jieShouDeShuJu = msg.obj.toString();
                    Log.d("jieShouDeShuJu",jieShouDeShuJu);
                     jieShouDeShuJu = jieShouDeShuJu.replaceAll("!GangN!","\n");
//                    jieShouDeShuJu.replaceAll("!GangRGangN!","\r\n");
                    ChatItemListViewBean bean = new ChatItemListViewBean();
                    bean.setType(1);
                    bean.setText(jieShouDeShuJu);
                    list.add(bean);
                    //获取系统当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);

                    //记录
                    db.execSQL("insert into record values(?,?,?,?,?)"
                            ,new String[]{MainActivity.myName,MainActivity.friendName,bean.getText(),"1",str});

                    //adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    listView.setSelection(listView.getCount());
                }
            }
        };
        //发送数据到服务端
        revHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x345){
                    try{
                        String line = msg.obj.toString().replaceAll("\n","!GangN!");
//                        line.replaceAll("\r\n","!GangRGangN!");
                        Log.d("SendContent",line);
                        os.write((line+"#"+"LiaoTian"+"\r\n").getBytes("utf-8"));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

        };
        new Thread(){
            @Override
            public void run() {
            try {
                s = new Socket(getResources().getString(R.string.ip), 25000);
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                os = s.getOutputStream();
                Message msg = new Message();
                msg.what = 0x345;
                msg.obj = myName + "#" + friendName;
                revHandler.sendMessage(msg);

            }catch (Exception e){
                e.printStackTrace();
            }
                t = new Thread(new Thread_Temp());
                t.start();
            }
        }.start();
        //TODO edittext的点击事件
        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEmojiView.yinCang();
                flag_mImageView *= -1;
                mImageView.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);

            }
        });

        //TODO  ImageView按钮点击事件
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                mEmojiView.xianShi();
                //切换imageView状态
//                flag_mImageView *= -1;
                if(flag_mImageView==1){
                    mEmojiView.xianShi();
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mImageView.getWindowToken(), 0);
                    mImageView.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                    flag_mImageView *= -1;

                    //让edittext获得焦点
                    editText.requestFocus();
                }else if(flag_mImageView==-1){

                    mEmojiView.yinCang();
                    //显示软键盘
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(mImageView,InputMethodManager.SHOW_FORCED);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    mImageView.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
                    flag_mImageView *= -1;
                }
            }
        });
        //TODO  Send按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String content = editText.getText().toString();
                    Log.d("SendContent",content);
                    if(content.length()!=0){  //输入信息不能为空
                        Message msg = new Message();
                        msg.what = 0x345;
                        msg.obj = content;

                        ChatItemListViewBean bean2 = new ChatItemListViewBean();

                        bean2.setText(editText.getText().toString());
                        bean2.setType(0);
                        list.add(bean2);
                        //记录
                        //获取系统当前时间
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);

                        //记录
                        db.execSQL("insert into record values(?,?,?,?,?)",new String[]{MainActivity.myName,MainActivity.friendName,bean2.getText(),"0",str});
                        adapter.notifyDataSetChanged();
                        listView.setSelection(listView.getCount());
                        revHandler.sendMessage(msg);
                        editText.setText("");
                    }/*else {
                        editText.setText("\r\n");
                    }*/


                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    /*@Override
    protected void onDestroy() {
        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"#"+"Delete_Socket_Activity"+"\n").getBytes("utf-8"));


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    super.onDestroy();
    }*/
    class Thread_Temp extends Thread{
        @Override
        public void run() {
            String content = null;
            try {
                while ((content = br.readLine()) != null) {
                    Message msg = new Message();
                    msg.what = 0x123;
                    msg.obj = content;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   /* @Override
    protected void onPause() {
        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"#"+"Delete_Socket_Activity"+"\n").getBytes("utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        super.onPause();
    }*/
    @Override
    protected void onStop() {
        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"#"+"Delete_Socket_MainActivity"+"\n").getBytes("utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(flag_mImageView==-1){
            mEmojiView.yinCang();
            flag_mImageView *= -1;
            mImageView.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
            return ;
        }
        super.onBackPressed();
    }

class  ItemClickListener implements AdapterView.OnItemClickListener
{
    /*public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                            View arg1,//The view within the AdapterView that was clicked
                            int arg2,//The position of the view in the adapter
                            long arg3//The row id of the item that was clicked
    ) {
        //在本例中arg2=arg3
        HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
        //显示所选Item的ItemText
//        setTitle((String)item.get("ItemText"));


    }*/
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(), imageIds[arg2 % imageIds.length]);
        ImageSpan imageSpan = new ImageSpan(MainActivity.this, bitmap);
        String str = null;
        if(arg2<10){
            str = "f00"+arg2;
        }else if(arg2<100){
            str = "f0"+arg2;
        }else{
            str = "f"+arg2;
        }
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.append(spannableString);
    }

}
}
