package com.zhuandian.fanyi;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuandian.fanyi.db.DAO.WordListDAO;
import com.zhuandian.fanyi.db.Entity.WordEntity;
import com.zhuandian.fanyi.db.WordSqliteOpenHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by 谢栋 on 2016/10/3.
 */
public class WordListFragment extends Fragment{
    private View view;
    //操作数据库相关数据的声明
    private WordSqliteOpenHelper helper=null;
    private WordListDAO dao =null;
    private SQLiteDatabase db=null;

    private List<WordEntity> wordslist=null;
    private ListView listview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.word_list,null);
        ((TextView)getActivity().findViewById(R.id.toolbar_text)).setText("单词记录");

        helper = new WordSqliteOpenHelper(getActivity());
        db=helper.getReadableDatabase();
        dao = new WordListDAO(getActivity());

        wordslist =dao.findAll();  //初始化数据

       Collections.reverse(wordslist);    //将原有list逆序排列
//        当然，如果想复制list集合，也是一句一代码
//        List list1= Collections.copy(list);

        listview = (ListView) view.findViewById(R.id.listview);
        WordAdapter wordAdapter = new WordAdapter(getActivity(),wordslist);

        listview.setAdapter(wordAdapter);  //为listView装载数据




        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Fragment消亡时重新改写导航条上的标题
        ((TextView)getActivity().findViewById(R.id.toolbar_text)).setText("点点翻译");
    }
}
