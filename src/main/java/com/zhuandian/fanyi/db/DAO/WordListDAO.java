package com.zhuandian.fanyi.db.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhuandian.fanyi.db.Entity.WordEntity;
import com.zhuandian.fanyi.db.WordSqliteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 谢栋 on 2016/10/3.
 *
 * DAO(Data Access Object)数据访问接口，对单词本数据进行访问
 */
public class WordListDAO {

    private WordSqliteOpenHelper wordSqliteOpenHelper;


    //在构造方法中对操作数据库的帮助类进行初始化，做到对象一被创建就完成初始化
    public WordListDAO(Context context) {

        wordSqliteOpenHelper = new WordSqliteOpenHelper(context);
    }


    /**
     * 添加一条数据到单词本数据库
     */

    public void addValue(String word ,String interrupt){

        //得到一个SQLiteDataBase的实例
        SQLiteDatabase db = wordSqliteOpenHelper.getWritableDatabase();

        //采用占位符的方式往数据库里面写数据，防止出现异常
        db.execSQL("insert into word_table(word ,interrupt) values (?,?)",
                                      new Object[]{word,interrupt});

        db.close();   //关闭资源

    }


    /**
     * 删除所有元素
     */
    public void deleteAll(){
        //得到一个SQLiteDataBase的实例
        SQLiteDatabase db=  wordSqliteOpenHelper.getWritableDatabase();

        //删除表
//        db.execSQL("delete * from table");   //直接删除表，再次插入数据时程序会崩溃
        db.delete("word_table",null,null);  //删除表中数据


        db.close();
    }

    /**
     * 查找对应的数据
     * @param chinese
     * @return
     */
    public boolean findValues(String chinese){

        //得到一个SQLiteDataBase实例
        SQLiteDatabase db = wordSqliteOpenHelper.getReadableDatabase();

        //得到数据库的遍历角标
        Cursor cursor  = db.rawQuery("select * from word_table where word=?",new String[]{chinese});

        boolean result = cursor.moveToNext();

        //关闭游标，关闭数据库
        cursor.close();
        db.close();

        return  result;
    }


    /**
     *  查询数据库中的所有内容
     * @return list集合
     */
    public List<WordEntity> findAll(){

        //得到一个可操作的SQLiteDatabase实例
        SQLiteDatabase db = wordSqliteOpenHelper.getReadableDatabase();

        List<WordEntity> wordlist = new ArrayList<WordEntity>();

        //得到操作数据库的游标
        Cursor cursor = db.rawQuery("select * from word_table",null);

        //遍历数据库中的元素
        while(cursor.moveToNext()){

            String chinese = cursor.getString(cursor.getColumnIndex("word"));
            String english = cursor.getString(cursor.getColumnIndex("interrupt"));

            //定义一个单词跟翻译的实体包装类
            WordEntity wordEntity = new WordEntity(chinese,english);

            //把包装的实体类添加到list集合
            wordlist.add(wordEntity);
        }

        //关闭资源
        cursor.close();
        db.close();
        return wordlist;


    }
}
