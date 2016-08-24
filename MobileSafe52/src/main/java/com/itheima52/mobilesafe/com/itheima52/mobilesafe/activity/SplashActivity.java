package com.itheima52.mobilesafe.com.itheima52.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;

public class SplashActivity extends Activity {

    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号：" + getVersionName());
    }

    private String getVersionName(){

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            int versionCode = packageInfo.versionCode;

            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
