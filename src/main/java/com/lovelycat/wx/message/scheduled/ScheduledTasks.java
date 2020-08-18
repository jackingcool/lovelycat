package com.lovelycat.wx.message.scheduled;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.xsshome.taip.nlp.TAipNlp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.constants.MessageContentConstants;
import com.lovelycat.wx.constants.SymbolicConstants;
import com.lovelycat.wx.utils.ApplicationContextUtil;
import com.lovelycat.wx.utils.ClassPathResourceReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;


@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务

/**
 * spring 定时任务
 *
 * @author mgg
 */
public class ScheduledTasks {
    @Value("${wx.api.regards.url}")
    private String regardsUrl;

    @Value("${wx.file.path}")
    private String filePath;

    @Value("${wx.use.wxIds}")
    private String wxIds;

    @Value("${wx.robot.wxId}")
    private String robotWxId;

    @Value("classpath:json/SendWeather.json")
    private Resource sendWeather;

    @Value("${wx.tencent.smart.chat.appId}")
    private String smartChatAppId;
    @Value("${wx.tencent.smart.chat.appKey}")
    private String smartChatAppKey;
    @Value("${wx.api.chickenSoup.url}")
    private String chickenSoupUrl;
    @Value("${wx.use.random.music}")
    private String randomMusicWxIds;

    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    public void netEaseCloud() {
        WxBaseService wxBaseService = (WxBaseService) ApplicationContextUtil.getBean("WxBaseService");
        StringBuffer sb = new StringBuffer();
        String path = filePath + "IMG_6013.JPG";

        String text = HttpUtil.get(chickenSoupUrl);
        List<Map<String, String>> musicList = new ArrayList<>();
        sb.append("网抑云今日时间语录：" + text + " 今日热歌TOP5：");
        for (int i = 0; i < 5; i++) {
            JSONObject musicObject = JSONObject.parseObject(HttpUtil.get("https://api.uomg.com/api/rand.music?sort=热歌榜&format=json")).getJSONObject("data");
            Map<String, String> map = new HashMap<>();
            map.put("title", musicObject.getString("name"));
            map.put("text", musicObject.getString("artistsname"));
            map.put("targetUrl", musicObject.getString("url"));
            map.put("picUrl", musicObject.getString("picurl"));
            musicList.add(map);
        }
        String[] randomMusicWxIdArr = randomMusicWxIds.split(",");
        for (int i = 0; i < randomMusicWxIdArr.length; i++) {
            wxBaseService.sendImageMsg(robotWxId, randomMusicWxIdArr[i], path, 0);
            try {
                Thread.sleep(3000);
                wxBaseService.sendTextMsg(robotWxId, randomMusicWxIdArr[i], sb.toString(), 3000);
                Thread.sleep(3000);
                for (int j = 0; j < musicList.size(); j++) {
                    Map<String, String> map = musicList.get(j);
                    wxBaseService.sendLinkMsg(robotWxId, randomMusicWxIdArr[i], map.get("title"), map.get("text"), map.get("targetUrl"), map.get("picUrl"));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }


    @Scheduled(cron = "0 0 6 * * ?")
    @Async
    public void GoodMorning() {
        regards(1, 100);
    }

    @Scheduled(cron = "0 1 6 * * ?")
    @Async
    public void weatherReply() throws Exception {
        JSONArray jsonArray = JSON.parseObject(new ClassPathResourceReader("json/SendWeather.json").getContent()).getJSONArray("data");
        TAipNlp tAipNlp = new TAipNlp(smartChatAppId, smartChatAppKey);
        for (int i = 0; i < jsonArray.size(); i++) {
            StringBuffer sb = new StringBuffer();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            //调用腾讯ai回复的都天气信息用于展示
            JSONObject aiObject = JSON.parseObject(tAipNlp.nlpTextchat(System.currentTimeMillis() / 1000 + "", jsonObject.getString("city") + MessageContentConstants.WEATHER));
            sb.append(aiObject.getJSONObject("data").getString("answer")).append("\\n\\n");
            sb.append("Have a good day ~");
            WxBaseService wxBaseService = (WxBaseService) ApplicationContextUtil.getBean("WxBaseService");
            wxBaseService.sendTextMsg(robotWxId, jsonArray.getJSONObject(i).getString("id"), sb.toString(), 0);
        }

    }


    @Scheduled(cron = "0 0 23 * * ?")
    public void GoodNight() {
        regards(3, 100);
    }


    public void regards(int type, int maxPage) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("min_id", RandomUtil.randomInt(0, maxPage));
        paramMap.put("category", type);
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get(regardsUrl, paramMap));
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray.size() > 0) {
            JSONObject dataObject = jsonArray.getJSONObject(RandomUtil.randomInt(0, jsonArray.size()));
            String fileUrl = dataObject.getString("imgsrc");
            String fileName = UUID.randomUUID().toString();
            File file = new File(filePath + fileName + ".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            HttpUtil.download(fileUrl, fos, true);
            WxBaseService wxBaseService = (WxBaseService) ApplicationContextUtil.getBean("WxBaseService");

            if (null != wxIds && "" != wxIds) {
                if (wxIds.indexOf(SymbolicConstants.ENGLISH_COMMA) != -1) {
                    String[] wxIdArr = wxIds.split(SymbolicConstants.ENGLISH_COMMA);
                    for (int i = 0; i < wxIdArr.length; i++) {
                        try {
                            Object o = wxBaseService.sendImageMsg(robotWxId, wxIdArr[i], file.getAbsolutePath(), 0);
                            Thread.sleep(5000);
                            wxBaseService.sendTextMsg(robotWxId, wxIdArr[i], dataObject.getString("content"), 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        wxBaseService.sendImageMsg(robotWxId, wxIds, file.getAbsolutePath(), 0);
                        Thread.sleep(5000);
                        wxBaseService.sendTextMsg(robotWxId, wxIds, dataObject.getString("content"), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
            FileUtil.del(file);
        } else {
            regards(type, maxPage);
        }
    }


}
