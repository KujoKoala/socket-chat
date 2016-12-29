package com.itheima.liaotianqi.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itheima.liaotianqi.activity.MainActivity;
import com.itheima.liaotianqi.activity.TabActivity;
import com.itheima.liaotianqi.adapter.FriendItemAdapter;
import com.itheima.liaotianqi.server.ClientThread;

import static com.itheima.liaotianqi.activity.MainActivity.friendName;

/*
 * @创建者     Administrator
 * @创建时间   2016/10/28 20:40
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class DummyFragment extends Fragment {
    public static FriendItemAdapter friendItemAdapter;
    public static ClientThread  clientThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ListView listView = new ListView(getActivity());

        friendItemAdapter = new FriendItemAdapter(getActivity(), TabActivity.al);
        listView.setAdapter(friendItemAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    MainActivity.myName = TabActivity.myName;
                    friendName = TabActivity.al.get(position);
                    String key = TabActivity.myName+"#"+friendName;
                    boolean flag = TabActivity.treeMap_HongDian_notify.containsKey(key);
                    if(flag == true){
                        TabActivity.treeMap_HongDian_notify.put(key,0);
                        TabActivity.db.delete("notify","friendName = ?",new String[]{key});
                    }
                    listView.setAdapter(friendItemAdapter);
                    startActivity(intent);
                }
        });
        return listView;
    }
}
