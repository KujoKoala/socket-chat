package com.itheima.liaotianqi.bean;

import android.graphics.Bitmap;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/27 22:52
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class ChatItemListViewBean {
    private int type;
    private String text;
    private Bitmap icon;
    private String time;
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text = text;
    }
    public void SetTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
}
