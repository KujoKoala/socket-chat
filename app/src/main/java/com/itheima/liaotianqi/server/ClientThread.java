package com.itheima.liaotianqi.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/27 21:21
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO} 这个类已经无用
 */

public class ClientThread implements Runnable {
    private Socket  s;

    public  Handler revHandler;
    BufferedReader br = null;
    OutputStream   os = null;



    @Override
    public void run() {
        try{
            s = new Socket("192.168.1.102",25000);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();

            new Thread(){
                @Override       //获取服务器数据
                public void run() {
                    String content = null;
                    try{
                        while((content = br.readLine()) != null){
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                         //   MainActivity.handler.sendMessage(msg);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
            Looper.prepare();
            revHandler = new Handler() {
                @Override   //发送数据到服务器
                public void handleMessage(Message msg) {
                    if(msg.what == 0x345){
                        try{
                            os.write((msg.obj.toString()+"#"+"LiaoTian"+"\r\n").getBytes("utf-8"));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            };
            Looper.loop();
        }catch (SocketTimeoutException el){
            System.out.println("网络连接超时");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
