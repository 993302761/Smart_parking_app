package com.example.myapplication.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.basic_class.user;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign_in_Activity extends Activity {
    private Button register;
    private Button sign;
    private EditText username;
    private EditText password;
    private Button btn_zhuce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        register = (Button)findViewById(R.id.register);
        sign=(Button)findViewById(R.id.Sign0);
        btn_zhuce=(Button)findViewById(R.id.btn_zhuce);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        butset();
        code();
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


    public void butset()
    {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Sign_in_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btn_zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Sign_in_Activity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Sign_in_Activity.this, MainActivity.class);
                startActivity(intent);

//                new Thread(new Runnable() {
//                    String id=username.getText().toString();
//                    String passwd=password.getText().toString();
//                    @Override
//                    public void run() {
//                        try {
//                            String json = "{\n" +
//                                    "\"id\":\""+
//                                    id+"\",\n" +
//                                    "\"password\":\""+
//                                    passwd+"\"\n" +
//                                    "}";
////                            String json = "{\n" +
////                                    "\"id\":\"18153301670\",\n" +
////                                    "\"password\":\"001124\"\n" +
////                                    "}";
//                            MediaType js = MediaType.parse("application/json; charset=utf-8");
//
//                            OkHttpClient client = new OkHttpClient();       //创建HTTP客户端
//                            Request request = new Request.Builder()
//                                    .url("http://192.168.75.252:8080/App/find")
//                                    .post(RequestBody.create(js,json))
//                                    .build();        //创建HTTP请求
//                            Response response=client.newCall(request).execute();  //执行发送的指令
//
//                            if(response.isSuccessful()){
//                                        try {
//                                            String res = response.body().string();
////                                            Log.i("数据：",res);
//                                            Gson gson = new Gson();
//                                            user use = gson.fromJson(res,user.class);
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if(use!=null) {
//                                                        Intent intent = new Intent();
//                                                        intent.setClass(Sign_in_Activity.this, MainActivity.class);
//                                                        startActivity(intent);
//                                                    }
//                                                    else
//                                                        Toast.makeText(Sign_in_Activity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                            }else {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(Sign_in_Activity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(Sign_in_Activity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();

            }
        });

    }



}
