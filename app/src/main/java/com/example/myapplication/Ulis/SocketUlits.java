package com.example.myapplication.Ulis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketUlits {

    public static Socket socket = null;
    private static OutputStream os = null;
    private static InputStream is = null;

    public static void Output(String str) {
        try {
            os = socket.getOutputStream();
            os.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String Input1()
    {
        String line=".";
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            line = br.readLine();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }

    public static String Input()
    {
        String string = null;
        try {
            is = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len = is.read(bytes);
            if(len>0)
                string = new String(bytes,0,len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }
}
