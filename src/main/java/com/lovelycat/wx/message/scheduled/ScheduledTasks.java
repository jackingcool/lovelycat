package com.lovelycat.wx.message.scheduled;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.xsshome.taip.nlp.TAipNlp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lovelycat.wx.annotation.FilterFeature;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.constants.MessageContentConstants;
import com.lovelycat.wx.constants.SymbolicConstants;
import com.lovelycat.wx.db.entity.WxFriend;
import com.lovelycat.wx.db.entity.WxGroupFeatureGroup;
import com.lovelycat.wx.db.service.IWxFriendService;
import com.lovelycat.wx.db.service.IWxGroupFeatureGroupService;
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
    @Value("${wx.robot.wxId}")
    private String robotWxId;
    @Value("${wx.tencent.smart.chat.appId}")
    private String smartChatAppId;
    @Value("${wx.tencent.smart.chat.appKey}")
    private String smartChatAppKey;
    @Value("${wx.api.chickenSoup.url}")
    private String chickenSoupUrl;


    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    public void netEaseCloud() {
        WxBaseService wxBaseService = (WxBaseService) ApplicationContextUtil.getBean("WxBaseService");
        IWxGroupFeatureGroupService iWxGroupFeatureGroupService = (IWxGroupFeatureGroupService) ApplicationContextUtil.getBean("IWxGroupFeatureGroupService");
        StringBuffer sb = new StringBuffer();
        String path = filePath + "IMG_6013.JPG";

        String text = JSONObject.parseObject(HttpUtil.get(chickenSoupUrl)).getString("data");
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
        List<WxGroupFeatureGroup> list = iWxGroupFeatureGroupService.list(new QueryWrapper<WxGroupFeatureGroup>().select("group_id").eq("use_flag", true).eq("feature_id", "2288311054294220805"));
        for (int i = 0; i < list.size(); i++) {
            wxBaseService.sendImageMsg(robotWxId, list.get(i).getGroupId(), path, 0);
            try {
                Thread.sleep(3000);
                wxBaseService.sendTextMsg(robotWxId, list.get(i).getGroupId(), sb.toString(), 3000);
                Thread.sleep(3000);
                for (int j = 0; j < musicList.size(); j++) {
                    Map<String, String> map = musicList.get(j);
                    wxBaseService.sendLinkMsg(robotWxId, list.get(i).getGroupId(), map.get("title"), map.get("text"), map.get("targetUrl"), map.get("picUrl"));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    @Scheduled(cron = "0 0 6 * * ?")
    @Async
    public void GoodMorning() {
        regards(1, 70);
    }

    @Scheduled(cron = "0 02 16 * * ?")
    @Async
    public void weatherReply() throws Exception {
        IWxFriendService iWxFriendService = (IWxFriendService) ApplicationContextUtil.getBean("IWxFriendService");
        List<WxFriend> wxFriendList = iWxFriendService.findFriendWeatherReply();
        TAipNlp tAipNlp = new TAipNlp(smartChatAppId, smartChatAppKey);
        for (int i = 0; i < wxFriendList.size(); i++) {
            StringBuffer sb = new StringBuffer();
            //调用腾讯ai回复的都天气信息用于展示
            JSONObject aiObject = JSON.parseObject(tAipNlp.nlpTextchat(System.currentTimeMillis() / 1000 + "", wxFriendList.get(i).getCity() + MessageContentConstants.WEATHER));
            sb.append(aiObject.getJSONObject("data").getString("answer")).append("\\n\\n");
            sb.append("Have a good day ~");
            WxBaseService wxBaseService = (WxBaseService) ApplicationContextUtil.getBean("WxBaseService");
            wxBaseService.sendTextMsg(robotWxId, wxFriendList.get(i).getWxId(), sb.toString(), 0);
        }

    }


    @Scheduled(cron = "0 0 23 * * ?")
    public void GoodNight() {
        regards(3, 70);
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
            IWxGroupFeatureGroupService iWxGroupFeatureGroupService = (IWxGroupFeatureGroupService) ApplicationContextUtil.getBean("IWxGroupFeatureGroupService");
            List<WxGroupFeatureGroup> list = iWxGroupFeatureGroupService.getRegardsList();
            if (CollectionUtil.isNotEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        Object o = wxBaseService.sendImageMsg(robotWxId, list.get(i).getGroupId(), file.getAbsolutePath(), 0);
                        Thread.sleep(5000);
                        wxBaseService.sendTextMsg(robotWxId, list.get(i).getGroupId(), dataObject.getString("content"), 0);
                    } catch (InterruptedException e) {
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
