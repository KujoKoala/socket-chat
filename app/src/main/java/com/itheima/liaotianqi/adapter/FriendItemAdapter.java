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
import com.jauker.widget.BadgeView;

import java.util.ArrayList;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/28 22:09
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class FriendItemAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private   Context mContext;
    private ArrayList<String> al;
    public FriendItemAdapter(Context context,ArrayList<String> al){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.al = al;
    }
    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_liaotianfrienditem,null);
            viewHolder.mImageView = (ImageView)convertView.findViewById(R.id.imageView_friend);
            viewHolder.mTextView01 = (TextView)convertView.findViewById(R.id.text01_friend);
            viewHolder.mTextView02 = (TextView)convertView.findViewById(R.id.text02_friend);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.mImageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
              R.drawable.touxiang));

        /*
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.touxiang));
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView_friend);*/

        viewHolder.mTextView01.setText(al.get(position));
        //通知信息的红点
        String key = TabActivity.myName+"#"+al.get(position);
        int  count=0;
        BadgeView badgeView = new com.jauker.widget.BadgeView(mContext);
        boolean flag = TabActivity.treeMap_HongDian_notify.containsKey(key);
        if(flag==true){
            count = TabActivity.treeMap_HongDian_notify.get(key);

            badgeView.setTargetView(viewHolder.mImageView);
        //    badgeView.setBadgeGravity(Gravity.LEFT);;
            badgeView.setBadgeCount(count);
        }else {
            badgeView.setTargetView(viewHolder.mImageView);
            //    badgeView.setBadgeGravity(Gravity.LEFT);;
            badgeView.setBadgeCount(0);
        }

        //----

        viewHolder.mTextView02.setText("ni hao wo shi:"+al.get(position));
        /*
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView_friend);
        imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.touxiang));
        TextView textView1  = (TextView)convertView.findViewById(R.id.text01_friend);
        textView1.setText("dengchao");
        TextView textView2  = (TextView)convertView.findViewById(R.id.text02_friend);
        textView2.setText("ni hao dengchao");
        */
        return convertView;
    }
    public final class ViewHolder{
        ImageView mImageView;
        TextView mTextView01;
        TextView mTextView02;
    }
}
