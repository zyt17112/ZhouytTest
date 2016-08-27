package com.itheima52.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERR = 1;
    private static final int CODE_NET_ERR = 2;
    private static final int CODE_JSON_ERR = 3;

    private TextView tv_version;

    // 版本名
    private String mVersionName;

    // 版本号
    private int mVersionCode;

    // 版本描述
    private String mDes;

    // 下载链接
    private String mDownloadURL;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERR:
                    Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_NET_ERR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_JSON_ERR:
                    Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };

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
     * 获取本地APP本号
     * @return 版本名称
     */
    private int getVersionCode(){

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            int versionCode = packageInfo.versionCode;

            return versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 从服务器获取版本信息并进行校验
     * 获取信息不能在主线程中进行，会阻塞主线程，需要新建一个子线程
     */
    private void checkVersion() {

        new Thread() {
            @Override
            public void run() {

                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
            /*  本机地址用localhost，但是如果用模拟器加载本机地址时（另外一个系统），
                会访问模拟器（另一系统）的localhost，此时可以用ip（10.0.2.2）来替换
                */
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();

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

                        // 解析json
                        JSONObject jo = new JSONObject(result);

                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDes = jo.getString("description");
                        mDownloadURL = jo.getString("downloadURL");

//                        System.out.println("版本描述：" + mDes);

                        if (mVersionCode > getVersionCode()) {
                            // 判断是否有更新
                            /*
                            有更新，弹出升级对话框
                             */
                            msg.what = CODE_UPDATE_DIALOG;
                        }
                    }

                } catch (MalformedURLException e) {
                    // URL地址错误
                    msg.what = CODE_URL_ERR;
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络异常
                    msg.what = CODE_NET_ERR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    // 非JSON格式，JSON解析失败
                    msg.what = CODE_JSON_ERR;
                    e.printStackTrace();
                } finally {
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 弹出升级对话框
     */
    protected void showUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage("版本描述：" + mDes);

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("立即更新");
            }
        });

        builder.setNegativeButton("以后再说", null);

        builder.show();
    }
}
