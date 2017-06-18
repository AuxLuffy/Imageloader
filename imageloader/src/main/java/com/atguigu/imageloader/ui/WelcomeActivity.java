package com.atguigu.imageloader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.atguigu.imageloader.R;

public class WelcomeActivity extends Activity {

    private RelativeLayout rl_welcome;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();//关闭当前Activity
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //指定全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示

        setContentView(R.layout.activity_welcome);

        rl_welcome = (RelativeLayout) findViewById(R.id.rl_welcome);

        //加载动画
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_welcome);

        //动画结束，启动新的Activity：方式一：使用动画的监听
//        loadAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });//提示方法的参数：ctrl + alt + /

        //启动动画
        rl_welcome.startAnimation(loadAnimation);

        //动画结束，启动新的Activity：方式二：发送消息
        handler.sendEmptyMessageDelayed(1,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除所有未被执行的消息
        handler.removeCallbacksAndMessages(null);

    }
}
