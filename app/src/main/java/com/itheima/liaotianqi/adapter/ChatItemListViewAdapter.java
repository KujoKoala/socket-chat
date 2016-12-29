package com.itheima.liaotianqi.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.liaotianqi.QQHead.ExpressionUtil;
import com.itheima.liaotianqi.R;
import com.itheima.liaotianqi.bean.ChatItemListViewBean;

import java.util.List;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/27 21:46
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class ChatItemListViewAdapter extends BaseAdapter {
    private List<ChatItemListViewBean> list;
    private LayoutInflater             mInflater;
    private Context                    mContext;
    public ChatItemListViewAdapter(Context context, List<ChatItemListViewBean> list){
        this.list = list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            if(getItemViewType(position)==1) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.layout_liaotianzijiemianin, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.image_in);
                holder.text = (TextView) convertView.findViewById(R.id.text_in);
                holder.time = (TextView)convertView.findViewById(R.id.time_in);
            }else{
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.layout_liaotianzijiemian, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.image_out);
                holder.text = (TextView) convertView.findViewById(R.id.text_out);
                holder.time = (TextView)convertView.findViewById(R.id.time_out);
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.icon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_launcher));



        //---------进行表情包判断
        String zhengze = "f0[0-9]{2}|f10[0-7]";											//正则表达式，用来判断消息内是否有表情
        try {
            SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, list.get(position).getText(), zhengze);
            holder.text.setText(spannableString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //------

//        holder.text.setText(list.get(position).getText());
        holder.time.setText(list.get(position).getTime());
        return convertView;
    }

    @Override
    public int getViewTypeCount() {         //很重要
        return 2;                           //如果是一则不会变化
    }

    @Override
    public int getItemViewType(int position) {
        ChatItemListViewBean bean = list.get(position);
        return bean.getType();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public final class ViewHolder{
        public ImageView icon;
        public TextView text;
        public TextView time;
    }
}
