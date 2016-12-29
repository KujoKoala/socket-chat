package com.itheima.liaotianqi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itheima.liaotianqi.adapter.MyItemAdapter;

/*
 * @创建者     Administrator
 * @创建时间   2016/11/3 17:09
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = new ListView(getActivity());
        MyItemAdapter myItemAdapter = new MyItemAdapter(getActivity());
        lv.setAdapter(myItemAdapter);
        return lv;
    }
}
