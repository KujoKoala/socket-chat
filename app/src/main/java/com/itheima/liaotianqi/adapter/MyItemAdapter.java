package com.itheima.liaotianqi.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.liaotianqi.R;
import com.itheima.liaotianqi.activity.TabActivity;

/*
 * @创建者     Administrator
 * @创建时间   2016/11/3 17:11
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class MyItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    public MyItemAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.activity_liaotianfrienditem,null);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView_friend);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.touxiang));
        TextView textView1  = (TextView)convertView.findViewById(R.id.text01_friend);
        textView1.setText("Name:"+TabActivity.myName);
        TextView textView2  = (TextView)convertView.findViewById(R.id.text02_friend);
        textView2.setText("XXXXXXXXXXXX");
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
