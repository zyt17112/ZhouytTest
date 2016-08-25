package com.itheima52.mobilesafe.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by new on 2016/8/25.
 * 读取流的工具类
 */
public class StreamUtils {
    /**
     * 将输入流读取成String后返回
     *
     * @param in
     * @return
     */
    public static String readFromStream(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];

        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        String result = out.toString();

        // 如果在try-catch中写，在finally里close
        in.close();
        out.close();

        return result;

    }
}
