package com.zhuandian.fanyi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 谢栋 on 2016/10/3.
 *
 * 操作Sqlite的帮助类
 */
public class WordSqliteOpenHelper extends SQLiteOpenHelper{

    //构造方法
    public WordSqliteOpenHelper(Context context) {

        super(context,"word.db",null,1);
    }

    //数据库第一次被创建时调用的方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table word_table (id integer primary key autoincrement ,word varchar (20), interrupt varchar (60) )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
