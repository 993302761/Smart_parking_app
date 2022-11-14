package com.example.myapplication.home;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.R;


public class RegisterMessageActivity extends Activity {
    TextView text_choosephoto,text_choosephoto2,text_choosephoto3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        text_choosephoto=(TextView)findViewById(R.id.text_choosephoto);
        text_choosephoto2=(TextView)findViewById(R.id.text_choosephoto2);
        text_choosephoto3=(TextView)findViewById(R.id.text_choosephoto3);
        code();
//        text_choosephoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RegisterMessageActivityPermissionsDispatcher.startFaceActivityWithPermissionCheck(RegisterMessageActivity.this);
//            }
//        });
//        text_choosephoto2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RegisterMessageActivityPermissionsDispatcher.startFaceActivityWithPermissionCheck(RegisterMessageActivity.this);
//            }
//        });
//        text_choosephoto3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RegisterMessageActivityPermissionsDispatcher.startFaceActivityWithPermissionCheck(RegisterMessageActivity.this);
//            }
//        });
    }
    private void code()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            // 部分机型的statusbar会有半透明的黑色背景
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);// SDK21
        }
    }
}
