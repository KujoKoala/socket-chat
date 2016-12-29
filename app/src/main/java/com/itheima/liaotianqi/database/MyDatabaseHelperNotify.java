package com.itheima.liaotianqi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * @创建者     Administrator
 * @创建时间   2016/11/7 23:04
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class MyDatabaseHelperNotify extends SQLiteOpenHelper{
    final String CREATE_TABLE_SQL = "create table notify(friendName,count)";
    public MyDatabaseHelperNotify(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SQL);

    }
}
