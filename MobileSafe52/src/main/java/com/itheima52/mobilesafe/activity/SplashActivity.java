package com.itheima52.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本名称：" + getVersionName());

        checkVersion();
    }

    /**
     * 获取版本名称
     * @return 版本名称
     */
    private String getVersionName(){

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            System.out.println("versionName = " + versionName + "; versionCode = " + versionCode);

            return versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 从服务器获取版本信息并进行校验
     * 获取信息不能在主线程中进行，会阻塞主线程，需要新建一个子线程
     */
    private void checkVersion() {

        new Thread() {
            @Override
            public void run() {
                try {
            /*  本机地址用localhost，但是如果用模拟器加载本机地址时（另外一个系统），
                会访问模拟器（另一系统）的localhost，此时可以用ip（10.0.2.2）来替换
                */
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // 设置请求方法
                    conn.setRequestMethod("GET");
                    // 设置超过5秒请求超时
                    conn.setConnectTimeout(5000);
                    // 设置响应超时，连接上了，但是服务器迟迟没有响应
                    conn.setReadTimeout(5000);
                    // 连接服务器
                    conn.connect();

                    // 获取响应码
                    int respondCode = conn.getResponseCode();
                    // 响应码为200时才是正常响应
                    if (200 == respondCode) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);

                        // 打印结果
                        Log.d("SplashActivity", "网络返回：" + result);
                        System.out.println("网络返回：" + result);
                    }

                } catch (MalformedURLException e) {
                    // URL地址错误
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络异常
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
