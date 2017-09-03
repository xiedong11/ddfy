package com.zhuandian.fanyi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.zhuandian.fanyi.GlobalVariable.GlobalVariable;
import com.zhuandian.fanyi.db.DAO.WordListDAO;
import com.zhuandian.fanyi.db.WordSqliteOpenHelper;
import com.zhuandian.fanyi.utils.MyUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends Activity {

    private SpeechSynthesizer mTts;  //声明讯飞语音云配置
    private String speak_info;      //语音读出的字符

    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;
    private String TAG = "xiedong";
    private SweetAlertDialog pDialog;
    private String word;  //要翻译的字符串

    //操作数据库相关数据的声明
    private WordSqliteOpenHelper helper = null;
    private WordListDAO dao = null;
    private SQLiteDatabase db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.toolbar_text)).setText("点点翻译");

        //初始化数据库
        helper = new WordSqliteOpenHelper(this);
        db = helper.getWritableDatabase();

        dao = new WordListDAO(this);
//        dao.addValue("aa","bb");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //使用溢出菜单（三个小圆点）
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.clear:
//                        Toast.makeText(MainActivity.this,item+"被点击",Toast.LENGTH_SHORT).show();

                        dao.deleteAll();   //删除数据库中的所有元素
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("好的嘛！")
                                .setContentText("已清除所有历史记录！！")
                                .show();
                        break;

                    case R.id.add:
                        Toast.makeText(MainActivity.this, "已成功添加到单词本", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.wordtable:
//                        Toast.makeText(MainActivity.this,item+"被点击",Toast.LENGTH_SHORT).show();
                        if (getFragmentManager().getBackStackEntryCount() < 1) {
                            changeFragment(new WordListFragment());
                        }
                        break;
                }
                return false;
            }
        });

        initXunFei();  //初始化讯飞语音云配置

        initView();





    }

    /**
     * 讯飞语音云相关配置初始化
     */
    private void initXunFei() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58185c41");

        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
//        mTts.startSpeaking("good moring", mSynListener);
    }

    /**
     * 用于fragment之间切换
     *
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.

        Log.i(TAG, "fragment插入成功");
        ft.add(R.id.container, fragment, "fragment")
                .addToBackStack("fragment")
                .commit();

    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.result);
        mEditText = (EditText) findViewById(R.id.content);
        mButton = (Button) findViewById(R.id.search);

        findViewById(R.id.voice_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("xiedong",speak_info);
                //开始语音合成
                mTts.startSpeaking(speak_info, mSynListener);
            }
        });


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                word = mEditText.getText().toString();

                if ("".equals(word)) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("请输入字符 ！！")
                            .show();
//                    Toast.makeText(MainActivity.this,"请输入字符",Toast.LENGTH_SHORT).show();
                } else if (!MyUtils.isNetworkAvailable(MainActivity.this)) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("网络不可用")
                            .setContentText("请确保当前网络可用！！")
                            .show();

                } else {
                    pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("请稍后...");
                    pDialog.setCancelable(true);
                    pDialog.show();


                }

                String url = GlobalVariable.URL + word;
                Log.i(TAG, url);
                MyAsyncTask task = new MyAsyncTask();
                task.execute(url);
            }
        });
    }


    class MyAsyncTask extends AsyncTask<String, Void, String> {

        String jsonStr = "";

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject obj = new JSONObject(s);

                String wordResult = obj.getString("translation");

                //用正则替换掉[""]t

                int cnt = wordResult.length();

                String info = wordResult.replace("[", "").replace("]", "").replace("\"", "");


//                wordResult = wordResult.replace("[\"([a-zA-Z]+[ ]*[a-zA-Z]+)\"]","$1");

                Log.i(TAG, info);


                speak_info = info;  //要语音播放的字符
                //如果数据库中不存在该数据，则加入到数据库
                if (!dao.findValues(word)) {

                    //加入字符判断，总是遵循中文字符在前的原则，方便在listview中展示
                    if (MyUtils.isChinese(word)) {
                        dao.addValue(word, info);  //把要翻译的字符跟结果插入数据库中
                    } else {
                        dao.addValue(info, word);
                    }
                }

                Log.i("xiedong", dao.findAll().size() + "");
                pDialog.cancel();

                mTextView.setText(info);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            try {
                HttpResponse response = client.execute(get);

                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream is = response.getEntity().getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String str = "";
                    while ((str = reader.readLine()) != null) {
                        jsonStr += str;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return jsonStr;
        }


    }


    //讯飞的合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };


    }
