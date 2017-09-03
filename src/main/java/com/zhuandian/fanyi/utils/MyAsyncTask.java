package com.zhuandian.fanyi.utils;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 谢栋 on 2016/7/23.
 */
public class MyAsyncTask extends AsyncTask<String ,Void ,String>{

    String jsonStr="";
    @Override
    protected String doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get=  new HttpGet(params[0]);
        try {
            HttpResponse response = client.execute(get);

            if(response.getStatusLine().getStatusCode()==200)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader =new BufferedReader( new InputStreamReader(is));

                String str="";
                while((str=reader.readLine())!=null){
                    jsonStr+=str;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStr;
    }
}
