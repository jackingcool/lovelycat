package com.lovelycat.wx.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * 暂用JSON文件写入数据
 */
public class FileUtil {

    public static void write(String filePath, String str) throws IOException {
        File file = new ClassPathResource(filePath).getFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bufferedWriter.write(str);
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
