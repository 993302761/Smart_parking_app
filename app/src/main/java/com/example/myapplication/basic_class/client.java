package com.example.myapplication.basic_class;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.myapplication.basic.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class client {
    private String id;
    private String password;
    private String name;

    public static Connection connection;

    private static client connectionUtil = new client();


    public client(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public static client getInstance() {
        if (connectionUtil == null) {
            synchronized (client.class) {
                if (connectionUtil == null) {
                    connectionUtil = new client();
                }
            }
        }
        return connectionUtil;
    }


    public client() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //连接数据库
    public Connection start() throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {


                    //从配置文件里把相应的配置读出来
                    //        Properties properties = new Properties();
                    //        properties.load(this.getClass().getResourceAsStream("/mysql.properties"));
                    String jdbcDriver = "org.mariadb.jdbc.Driver";
                    String jdbcUrl = "jdbc:mysql://10.0.2.2:3306/project?characterEncoding=utf8&useSSL=true&serverTimezone=UTC&rewriteBatchedStatements=true";
                    String userName = "root";
                    String passwd = "001124";
                    // 1.加载JDBC驱动
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Log.v(TAG, "加载JDBC驱动成功");
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "加载JDBC驱动失败");
                    }
                    try {
                        connection = DriverManager.getConnection(jdbcUrl, userName, passwd);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
        });
        thread.start();
        thread.join();
        return connection;
    }
}
