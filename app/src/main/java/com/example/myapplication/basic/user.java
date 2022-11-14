package com.example.myapplication.basic;

import com.example.myapplication.basic_class.client;

import java.io.IOException;
import java.sql.SQLException;

public interface user {

    boolean adduser(String id,String password,String name);
    boolean deleuser(String id);
    boolean updateuser(String id,String password);
    client finduser(String id) throws SQLException, IOException, ClassNotFoundException, InterruptedException;
}
