package com.lovelycat.wx.utils;


import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * JSON文件转换
 * @author mgg
 */
public class JsonUtils {

    public static JSONObject readJSONFileToJSONObject(Resource fileContent) throws IOException {
        return JSONObject.parseObject(jsonRead( fileContent.getFile()));
    }

    private static String jsonRead(File file){
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (Exception e) {

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
    }

}
