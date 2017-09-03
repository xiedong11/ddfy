package com.zhuandian.fanyi.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.zhuandian.fanyi.MainActivity;
import com.zhuandian.fanyi.R;

/**
 * Created by 谢栋 on 2016/7/24.
 */
public class Splash extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 1200; //延迟1秒
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去掉标题栏（一定要在setContentView()之后）
        setContentView(R.layout.splash);

        //设置为全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //利用延时实现loading效果
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent =new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                Splash.this.finish();
            }
        },SPLASH_DISPLAY_LENGHT);
    }
}




