package com.itheima.liaotianqi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.liaotianqi.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


/*
 * @创建者     Administrator
 * @创建时间   2016/10/29 15:30
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class DengLuActivity extends Activity {
    private EditText editTextZhangHao;
    private EditText editTextMiMa;
    private Button   button;
    private TextView textView;
    private Context  mContext;
    //Handler      mHandler = null;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu);
        mContext = this;
        editTextZhangHao = (EditText)findViewById(R.id.edit_zhanghao);
        editTextMiMa = (EditText)findViewById(R.id.edit_mima);
        button = (Button)findViewById(R.id.button_denglu);
        textView = (TextView)findViewById(R.id.text_denglu);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj.toString().contains("Fail")){
                    Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  zhangHao = editTextZhangHao.getText().toString();
                final String miMa = editTextMiMa.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        try{

                            socket = new Socket(InetAddress.getByName(getResources().getString(R.string.ip)).getHostAddress()
                                    ,45000);
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write((zhangHao+"#"+miMa+"#"+"DengLu"+"\n").getBytes("utf-8"));


                            String content = null;
                            BufferedReader br = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                            if((content = br.readLine())!=null){
                                if(content.contains("Sucess")){
                                    content = zhangHao+"#"+content;
                                    Intent intent = new Intent(DengLuActivity.this,TabActivity.class);
                                    intent.putExtra("myName",content);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Message msg = new Message();
                                    msg.obj = "Faile";
                                    handler.sendMessage(msg);
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DengLuActivity.this,ZhuCeActivity.class);
                startActivity(intent);
            }
        });

    }
}
