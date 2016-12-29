package com.itheima.liaotianqi.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itheima.liaotianqi.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/29 16:07
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class ZhuCeActivity extends Activity {
    private Context  mContext;
    private EditText mEditTextZhangHao;
    private EditText mEditTextMiMa;
    private Button   mButton;
    String zhangHao;
    String miMa;
    //Handler handler = null;
    //ZhuCeThread zhuCeThread;
    Socket mSocket;
    Message msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);
        mEditTextZhangHao = (EditText)findViewById(R.id.edit_zhanghao_zhuce);
        mEditTextMiMa = (EditText)findViewById(R.id.edit_mima_zhuce);
        mButton = (Button)findViewById(R.id.button_zhuce);
        mContext = this;
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj.toString().equals("Sucess")){
                    Toast.makeText(mContext,"注册成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"注册失败",Toast.LENGTH_SHORT).show();
                }
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zhangHao = mEditTextZhangHao.getText().toString();
                miMa = mEditTextMiMa.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            mSocket = new Socket(getResources().getString(R.string.ip),45000);
                            OutputStream os = mSocket.getOutputStream();
                            os.write((zhangHao+"#"+miMa+"#"+"ZhuCe"+"\n").getBytes("utf-8"));


                            String content = null;
                            BufferedReader br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                            if((content = br.readLine())!=null){
                                if(content.contains("Sucess")){
                                    msg = new Message();
                                    msg.obj="Sucess";

                                }else{
                                    msg.obj="Faile";
                                }
                                handler.sendMessage(msg);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });
    }
}
