package com.zhuandian.fanyi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhuandian.fanyi.db.Entity.WordEntity;

import java.util.List;

/**
 * Created by 谢栋 on 2016/10/29.
 */
public class WordAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<WordEntity>  mdatas;

    public WordAdapter(Context context, List<WordEntity> mdatas) {
        this.inflater =LayoutInflater.from(context);
        this.mdatas = mdatas;

    }

    @Override
    public int getCount() {
        return mdatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mdatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;

        if(convertView==null){
            convertView = inflater.inflate(R.layout.word_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.chinese = (TextView) convertView.findViewById(R.id.chinese);
            viewHolder.english = (TextView) convertView.findViewById(R.id.english);

         convertView.setTag(viewHolder);
        }else{
           viewHolder = (ViewHolder) convertView.getTag();  //如果convertView不为空，复用viewholder
        }


        viewHolder.chinese.setText(mdatas.get(position).getChinese()+"");  //设置要翻译的字符
        viewHolder.english.setText(mdatas.get(position).getEnglish()+"");  //设置翻译的结果

        return convertView;
    }

    //复用组件
    class ViewHolder{

        private TextView chinese;
        private TextView english;
    }
}
