package com.itheima.liaotianqi.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.itheima.liaotianqi.R;
import com.itheima.liaotianqi.database.MyDatabaseHelperNotify;
import com.itheima.liaotianqi.fragment.DummyFragment;
import com.itheima.liaotianqi.fragment.MyFragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
/*
 * @创建者     Administrator
 * @创建时间   2016/10/28 20:11
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 *///
public class TabActivity extends AppCompatActivity {
    private       SearchView mSearchView;
    private       Context    mContext;
    private       Socket     s;
    public static String     myName;
    public static ArrayList<String> al = new ArrayList<String>();
    public static TreeMap<String,Integer> treeMap_HongDian_notify = new TreeMap<>();
    private       OutputStream           os;
    private       MyDatabaseHelperNotify mMyDatabaseHelperNotify;
    public static SQLiteDatabase         db;
    private       ViewPager              mViewPager;
    private       ArrayList<Fragment>    mList;
    private       TabLayout              mTablayout;
    private       TabLayout.Tab          one;
    private       TabLayout.Tab          two;
   /* @Override
    protected void onStart() {
        String key = myName+"#"+friendName;
        boolean flag = TabActivity.treeMap_HongDian_notify.containsKey(key);
        if(flag == true){
            TabActivity.treeMap_HongDian_notify.put(key,0);
            TabActivity.db.delete("notify","friendName = ?",new String[]{key});
        }
        DummyFragment.friendItemAdapter.notifyDataSetChanged();
        super.onStart();
    }*/

   /* public static Handler handler_listview_porblem= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x4847){
                DummyFragment.friendItemAdapter.notifyDataSetChanged();
            }
        }
    };*/
    //添加好友
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x345){
                Log.d("dengLuThread","al.toString()1"+al.toString());
                if(msg.obj.toString().contains("Sucess")){
                    //Toast.makeText(mContext,"Sucess",0).show();
                    String [] data =  msg.obj.toString().split("#");
                    Log.d("dengLuThread","msg.obj.toString()"+msg.obj.toString());
                    Log.d("dengLuThread", "Arrays.toString(data)"+Arrays.toString(data));
                    al.add(data[0]);

                    DummyFragment.friendItemAdapter.notifyDataSetChanged();
                }else if(msg.obj.toString().contains("无该用户信息")){
                    Toast.makeText(mContext,"无该用户信息",Toast.LENGTH_SHORT).show();
                }else if(msg.obj.toString().equals("已经添加该用户为好友")){
                    Toast.makeText(mContext,"已经添加该用户为好友",Toast.LENGTH_SHORT).show();
                }
                Log.d("dengLuThread","al.toString()2"+al.toString());
            }
        }
    };

    //接受通知
    public Handler handler_notify = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x888){
               /* Toast.makeText(mContext,"收到了一条信息",0).show();
                badgeView.setTargetView(btn);
                //设置相对位置
                badgeView.setBadgeMargin(0, 5, 15, 0);
                //设置显示未读消息条数
                badgeView.setBadgeCount(2);*/

                String[] data  = msg.obj.toString().split("#");
                String key = data[0]+"#"+data[1];
                //data.length==4是处理没有登录时的通知，一登陆时服务端将数据发送过来，然后走此放法
                if(data.length==4){
                    boolean flag = treeMap_HongDian_notify.containsKey(key);
                    if(flag==true) {
                        int count = treeMap_HongDian_notify.get(key);
                        treeMap_HongDian_notify.put(key, Integer.parseInt(data[2])+count);
                        //更新数据
                        ContentValues values = new ContentValues();
                        values.put("count",Integer.parseInt(data[2])+count);
                        db.update("notify",values,"friendName = ?",new String[]{key});
                    }else {
                        treeMap_HongDian_notify.put(key, Integer.parseInt(data[2]));
                        //插入记录
                        db.execSQL("insert into notify values(?,?)",new String[]{key,data[2]+""});
                    }
                }else{  ////data.length==3 没有双方都进入聊天界面时进入此条件，登录了位于好友列表界面
                    int count = 1;
                    boolean flag = treeMap_HongDian_notify.containsKey(key);
                    if(flag==true){
                        count += treeMap_HongDian_notify.get(key);
                        //treeMap_HongDian_notify.remove(key);
                        treeMap_HongDian_notify.put(key,count);
                        //更新数据
                        ContentValues values = new ContentValues();
                        values.put("count",count);
                        db.update("notify",values,"friendName = ?",new String[]{key});
                    }else{
                        treeMap_HongDian_notify.put(key,1);
                        db.execSQL("insert into notify values(?,?)",new String[]{key,1+""});
                    }
                }
                //发送通知
//                public PendingIntent pendingIntent = new PendingIntent(getApplicationContext(),MainActivity.class)
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle(data[1])//设置通知栏标题
                        .setContentText("")
//                .setContentIntent() //设置通知栏点击意图
                        //  .setNumber(number) //设置通知集合的数量
                        .setTicker("收到一条新消息") //通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                        //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                        .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                        .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                        .setSmallIcon(R.drawable.ic_launcher);
                mNotificationManager.notify(0, mBuilder.build());


                Log.d("TabActivity",treeMap_HongDian_notify.toString());
               /* //震动手机
                Vibrator vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                //vibrator.vibrate(2000);
                long [] pattern = {100,150,100,200};
                vibrator.vibrate(pattern,-1);*/
                DummyFragment.friendItemAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isCheckable()){
            item.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem item = menu.findItem(R.id.search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("").setMessage("确定添加"+query+"为好友？").
                        setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();*/

                new Thread(){
                    @Override
                    public void run() {
                        try{
                            //添加好友
                            s = new Socket(getResources().getString(R.string.ip),45000);
                            OutputStream os = s.getOutputStream();
                            os.write((myName+"#"+query+"#"+"TianJiaHaoYou"+"\r\n").getBytes("utf-8"));
                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            String content = null;
                            if((content = br.readLine())!=null) {
                                Message msg = new Message();
                                msg.what = 0x345;
                                msg.obj = content;
                                handler.sendMessage(msg);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(mContext,"text change",0).show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyDatabaseHelperNotify = new MyDatabaseHelperNotify(this,"myRecord_notify.db3",null,1);
        db = mMyDatabaseHelperNotify.getReadableDatabase();
        //读取数据库通知记录数据，并设置红点
        Cursor cursor = db.query("notify",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String friendName = cursor.getString(0);
                int count = cursor.getInt(1);
                treeMap_HongDian_notify.put(friendName,count);
            }while(cursor.moveToNext());
        }
        cursor.close();

        //--------
        al.clear();
        Intent intent =getIntent();
        String temp = intent.getStringExtra("myName");
        String data[] = temp.split("#");
        myName = data[0];
        for(int i=1 ;i<data.length; i++){
            if(!data[i].contains("Sucess")){
                al.add(data[i]);
            }
        }
        mContext = this;

        mList = new ArrayList<>();
        //     mList.add(OneFragment.(mTitles[0], "fragment_1"));
        mList.add(new DummyFragment());
        mList.add(new MyFragment());
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTablayout = (TabLayout) findViewById(R.id.tabLayout); 
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mList.get(position);
            }
            private String[] mTitles = new String[]{"聊天", "我"};
            @Override
            public int getCount() {
                return mTitles.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        mTablayout.setupWithViewPager(mViewPager);

        one = mTablayout.getTabAt(0);
        two = mTablayout.getTabAt(1);
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
//        final ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.addTab(actionBar.newTab().setText("聊天").setTabListener(this));
//        actionBar.addTab(actionBar.newTab().setText("我").setTabListener(this));
//        actionBar.addTab(actionBar.newTab().setText("消息").setTabListener(this));
        //该线程用于接受通知信息（从服务端）
        new Thread(){
            @Override
            public void run() {
                try{
                    //获得通知消息(从服务端)
                    Socket socket = new Socket(getResources().getString(R.string.ip),15000);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String content = null;
                    os = socket.getOutputStream();
                    os.write((myName+"\n").getBytes("utf-8"));
                    while((content = br.readLine())!=null){
                            if(content.contains("Notify")){
                               Message msg = new Message();
                                msg.what=0x888;
                                msg.obj = content;
                                handler_notify.sendMessage(msg);
                            }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public TabActivity() {
        super();
    }


//    @Override
//    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//        switch (tab.getPosition()){
//            case 0:
//
//                Fragment fragment = new DummyFragment();
//                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
//                ft2.replace(R.id.container,fragment);
//                ft2.commit();
//                break;
//
//            case 1:
//                Fragment fragment1 = new MyFragment();
//                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
//                ft3.replace(R.id.container,fragment1);
//                ft3.commit();
//                break;
//
//            case 2:
//                break;
//
//        }
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//    }

    /*@Override
    protected void onDestroy() {
        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"\n").getBytes("utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }.start();
        super.onDestroy();


    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"\n").getBytes("utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }.start();
        return super.onKeyDown(keyCode, event);
    }

    /*@Override
    protected void onPause() {
        new Thread(){
            @Override
            public void run() {
                try{
                    os.write((myName+"\n").getBytes("utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }.start();
        super.onPause();
    }*/
}
