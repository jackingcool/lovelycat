package com.lovelycat.wx.message.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.xsshome.taip.nlp.TAipNlp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.constants.MessageContentConstants;
import com.lovelycat.wx.constants.MessageTypeConstants;
import com.lovelycat.wx.constants.MusicContentsConstants;
import com.lovelycat.wx.constants.SymbolicConstants;
import com.lovelycat.wx.db.entity.*;
import com.lovelycat.wx.db.service.*;
import com.lovelycat.wx.message.serivce.WxMessageService;
import com.lovelycat.wx.utils.ClassPathResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 机器人服务功能实现类
 *
 * @author mgg
 */
@Service("WxRobotService")
@Transactional
public class WxMessageServiceImpl implements WxMessageService {
    @Value("${wx.use.wxIds}")
    private String wxIds;
    @Value("${wx.api.lickingDogDiary.url}")
    private String lickingDogDiaryUrl;
    @Value("${wx.api.weather.appId}")
    private String weatherAppId;
    @Value("${wx.api.weather.appSecret}")
    private String weatherSecret;
    @Value("${wx.api.weather.url}")
    private String weatherUrl;
    @Value("${wx.api.chickenSoup.url}")
    private String chickenSoupUrl;
    @Value("${wx.api.love.words.url}")
    private String loveWordsUrl;
    @Value("${wx.api.regards.sweetNothings}")
    private String sweetNothings;
    @Value("${wx.api.music.search.url}")
    private String musicSearchUrl;
    @Value("${wx.api.music.listen.url}")
    private String musicListenUrl;
    @Value("${wx.robot.wxId}")
    private String robotWxId;
    @Value("${wx.tencent.smart.chat.url}")
    private String smartChatUrl;
    @Value("${wx.tencent.smart.chat.appId}")
    private String smartChatAppId;
    @Value("${wx.tencent.smart.chat.appKey}")
    private String smartChatAppKey;
    @Value("${wx.file.path}")
    private String filePath;

    @Autowired
    private WxBaseService wxBaseService;
    @Autowired
    private WxMessageService wxMessageService;
    @Autowired
    private IWxFriendService iWxFriendService;
    @Autowired
    private IWxGroupService iWxGroupService;
    @Autowired
    private IWxRobotService iWxRobotService;
    @Autowired
    private IWxGroupFriendService iWxGroupFriendService;
    @Autowired
    private IWxGroupFeatureService iWxGroupFeatureService;
    @Autowired
    private IWxGroupFeatureGroupService iWxGroupFeatureGroupService;
    @Autowired
    private IWxFriendFeatureService iWxFriendFeatureService;
    @Autowired
    private IWxFriendFeatureFriendService iWxFriendFeatureFriendService;


    @Override
    public boolean filterMessages(WxMessage wxMessage, List<Object> filterList) {

        if (wxMessage.getType() == MessageTypeConstants.NEW_ACCOUNT_LOGIN_COMPLETE_TYPE || wxMessage.getType() == MessageTypeConstants.ACCOUNT_LOGIN_OUT_TYPE) {
            return true;
        }

        if (CollectionUtil.isNotEmpty(filterList)) {
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).toString().equals(wxMessage.getFromWxId())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void afterAtReply(WxMessage wxMessage) {
        boolean LickingDogDiaryFlag = wxMessage.getMsg().indexOf(MessageContentConstants.LICKING_DOG_DIARY) == -1;
        //boolean gameLinkReplyFlag = !(wxMessage.getMsgType() == MessageTypeConstants.MSG_TYPE_GAME_LINK && wxMessage.getMsg().indexOf("<msg>") != -1 && wxMessage.getType() == MessageTypeConstants.GROUP_CHAT_TYPE);
        boolean cpddFlag = wxMessage.getMsg().toLowerCase().indexOf(MessageContentConstants.CPDD.toLowerCase()) == -1;
        boolean songFlag = !wxMessage.getMsg().startsWith(MessageContentConstants.SONG);
        if (wxMessage.getMsg().indexOf(wxMessage.getRobotWxId()) != -1
                && wxMessage.getMsg().indexOf(SymbolicConstants.AT) != -1
                && LickingDogDiaryFlag
                //&& gameLinkReplyFlag
                && cpddFlag
                && songFlag) {
            StringBuffer sb = new StringBuffer();
            try {
                String answer = connectWithTencentSmartChat(wxMessage);
                if (StringUtils.isEmpty(answer)) {
                    JSONArray jsonArray = JSON.parseObject(new ClassPathResourceReader("json/AfterAtReply.json").getContent()).getJSONArray("data");
                    JSONObject jsonObject = (JSONObject) jsonArray.get(RandomUtil.randomInt(0, jsonArray.size()));
                    sb.append(jsonObject.get("value"));
                } else {
                    sb.append(answer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            wxBaseService.sendGroupAtMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), wxMessage.getFinalFromWxId(), wxMessage.getFinalNickname(), sb.toString(), 1000);
        }
    }

    /**
     * 调用腾讯智能ai
     *
     * @param wxMessage
     * @return ai智能回复
     * @throws Exception
     */
    private String connectWithTencentSmartChat(WxMessage wxMessage) throws Exception {
        TAipNlp tAipNlp = new TAipNlp(smartChatAppId, smartChatAppKey);
        //会话标识（应用内唯一）
        String session = System.currentTimeMillis() / 1000 + "";
        String question = wxMessage.getMsg();
        if (wxMessage.getType() == MessageTypeConstants.PRIVATE_CHAT_TYPE) {
            question = wxMessage.getMsg();
        }

        if (wxMessage.getType() == MessageTypeConstants.GROUP_CHAT_TYPE) {
            question = question.replace(question.substring(question.indexOf("@at") - 1, question.indexOf("wxid=") + 25), "").trim();
        }

        //基础闲聊
        return JSONObject.parseObject(tAipNlp.nlpTextchat(session, question)).getJSONObject("data").getString("answer");
    }

    @Override
    public void LickingDogDiary(WxMessage wxMessage) {
        if (wxMessage.getMsg().indexOf(MessageContentConstants.LICKING_DOG_DIARY) != -1) {
            String[] arr = HttpUtil.get(lickingDogDiaryUrl).split(" ");
            StringBuffer sb = new StringBuffer();
            sb.append("\\n");
            for (int i = 0; i < arr.length; i++) {
                if (i == 2) {
                    arr[i] += "\\n";
                }
                sb.append(arr[i]);
            }


            wxBaseService.sendGroupAtMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), wxMessage.getFinalFromWxId(), wxMessage.getFinalNickname(), sb.toString(), 1);
        }
    }

    @Override
    public void test(WxMessage wxMessage) throws UnsupportedEncodingException {

    }

    @Override
    public void weatherReply(WxMessage wxMessage) {
        if (wxMessage.getMsg().endsWith(MessageContentConstants.WEATHER)) {
            StringBuffer sb = new StringBuffer();
            Map<String, Object> paramsMap = new HashMap<>(16);
            paramsMap.put("appid", weatherAppId);
            paramsMap.put("appsecret", weatherSecret);
            paramsMap.put("city", wxMessage.getMsg().substring(0, wxMessage.getMsg().indexOf("天气")));
            JSONObject weatherObject = JSON.parseObject(HttpUtil.get(weatherUrl, paramsMap));
            JSONObject today = weatherObject.getJSONArray("data").getJSONObject(0);
            //第一行日期
            sb.append(today.getString("date")).append(" ").append(today.getString("week")).append("\\n\\n");
            //播报内容
            sb.append("今日").append(weatherObject.get("city")).append(today.getString("wea")).append("，");
            //解析风速数组
            JSONArray winJSONArray = today.getJSONArray("win");
            sb.append(winJSONArray.getString(0)).append(today.getString("win_speed")).append("，");
            //温度
            sb.append("白天温度").append(today.getString("tem1")).append("，").append("晚上温度").append(today.getString("tem1")).append("，当前温度").append(today.getString("tem")).append("。");
            //空气质量
            sb.append("空气质量").append(today.getString("air_level")).append("。\\n");
            JSONArray tipsArray = today.getJSONArray("index");
            for (int i = 0; i < tipsArray.size(); i++) {
                sb.append(tipsArray.getJSONObject(i).getString("desc")).append("\\n");
            }
            sb.append("Have a good day ~");
            if (wxMessage.getType() == MessageTypeConstants.PRIVATE_CHAT_TYPE) {
                wxBaseService.sendTextMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), sb.toString(), 0);
            }

            if (wxMessage.getType() == MessageTypeConstants.GROUP_CHAT_TYPE) {
                wxBaseService.sendGroupAtMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), wxMessage.getFinalFromWxId(), wxMessage.getFinalNickname(), sb.toString(), 0);
            }

        }

    }

    @Override
    public void gameLinkReply(WxMessage wxMessage) {
        if (wxMessage.getMsgType() == MessageTypeConstants.MSG_TYPE_GAME_LINK && wxMessage.getMsg().indexOf("<msg>") != -1 && wxMessage.getType() == MessageTypeConstants.GROUP_CHAT_TYPE) {
            Document document = XmlUtil.parseXml(wxMessage.getMsg());
            String gameName = XmlUtil.getByXPath("//msg/appinfo/appname", document, XPathConstants.STRING).toString();
            String chatRoom = XmlUtil.getByXPath("//msg/appmsg/appattach/filekey", document, XPathConstants.STRING).toString();
            if (chatRoom.indexOf(SymbolicConstants.UNDERLINE) != -1) {
                chatRoom = chatRoom.substring(0, chatRoom.indexOf("_") - 2);
                StringBuffer sb = new StringBuffer();
                String title = XmlUtil.getByXPath("//msg/appmsg/title", document, XPathConstants.STRING).toString();
                sb.append(gameName).append("滴滴！！！\n");
                sb.append(title);
                wxBaseService.modifyGroupNotice(wxMessage.getRobotWxId(), chatRoom, sb.toString());
            }
        }
    }

    @Override
    public void cpdd(WxMessage wxMessage) {
        StringBuffer sb = new StringBuffer();
        if (wxMessage.getMsg().toLowerCase().indexOf(MessageContentConstants.CPDD.toLowerCase()) != -1) {
            int i = RandomUtil.randomInt(1, 100);
            if (i % 2 != 0) {
                sb.append(HttpUtil.get(loveWordsUrl)).append(" Cpdd");
            } else {
                sb.append(HttpUtil.get(sweetNothings)).append(" Cpdd");
            }

            wxBaseService.sendTextMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), sb.toString(), 0);
        }

    }

    @Override
    public void song(WxMessage wxMessage) {
        if (wxMessage.getMsg().startsWith(MessageContentConstants.SONG)) {
            String name = wxMessage.getMsg();
            name = name.substring(name.indexOf(MessageContentConstants.SONG) + 2, name.length());
            JSONObject searchMusicJSONObject = JSONObject.parseObject(HttpUtil.get(musicSearchUrl + name, 1000));

            if (searchMusicJSONObject.getInteger(MusicContentsConstants.CODE) == MusicContentsConstants.SUCCESS_CODE) {
                //查询到music解析JSON
                JSONArray songsJSONArray = searchMusicJSONObject.getJSONObject("result").getJSONArray("songs");
                //点歌只分享一首歌 默认第一首
                //获取music唯一标识
                String musicId = songsJSONArray.getJSONObject(0).getString("id");
                //获取歌名
                String musicName = songsJSONArray.getJSONObject(0).getString("name");
                //作者
                JSONObject artistJSONObject = songsJSONArray.getJSONObject(0).getJSONArray("artists").getJSONObject(0);
                // 链接名称 = musicName+artist
                String text = artistJSONObject.getString("name");
                // 图片地址
                String imgUrl = artistJSONObject.getString("img1v1Url");
                //调用播放接口获取url 用于播放转发的链接
                JSONObject musicUrlJSONObject = JSONObject.parseObject(HttpUtil.get(musicListenUrl + musicId, 1000));
                if (musicUrlJSONObject.getInteger(MusicContentsConstants.CODE) == MusicContentsConstants.SUCCESS_CODE) {
                    String musicUrl = musicUrlJSONObject.getJSONArray("data").getJSONObject(0).getString("url");
                    wxBaseService.sendLinkMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), musicName, text, musicUrl, imgUrl);
                }
            }


        }
    }

    @Override
    public void addGroupMemberReply(WxMessage wxMessage) throws UnsupportedEncodingException {

        JSONObject jsonObject = JSONObject.parseObject(wxMessage.getMsg());
        JSONArray jsonArray = jsonObject.getJSONArray("guest");
        for (int i = 0; i < jsonArray.size(); i++) {
            StringBuffer sb = new StringBuffer();
            JSONObject guestObject = jsonArray.getJSONObject(i);
            sb.append("\\n网络一线牵，珍惜这段缘\\n");
            sb.append("欢迎 ");
            sb.append(guestObject.getString("nickname")).append(" ");
            sb.append("大驾光临");
            //TODO:不知是否存在robotWxId 暂用配置文件配置的wx.robot.wxId
            wxBaseService.sendGroupAtMsg(robotWxId, jsonObject.getString("group_wxid"), guestObject.getString("wxid"), guestObject.getString("nickname"), sb.toString(), 1000);
            //发送
            //查询群成员信息
            JSONObject groupMemberJSON = wxBaseService.getGroupMember(robotWxId, jsonObject.getString("group_wxid"), guestObject.getString("wxid")).getJSONObject("data");
            //下载图片发送
            String fileUrl = groupMemberJSON.getString("headimgurl");
            String fileName = UUID.randomUUID().toString();
            File file = new File(filePath + fileName + ".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            HttpUtil.download(fileUrl, fos, true);
            wxBaseService.sendImageMsg(robotWxId, jsonObject.getString("group_wxid"), file.getAbsolutePath(), 0);
            //数据库同步信息
            WxGroupFriend wxJSONGroupFriend = new WxGroupFriend();
            wxJSONGroupFriend.setGroupId(jsonObject.getString("group_wxid"));
            wxJSONGroupFriend.setNickname(groupMemberJSON.getString("nickname"));
            wxJSONGroupFriend.setWxId(groupMemberJSON.getString("wxid"));
            wxJSONGroupFriend.setCity(groupMemberJSON.getString("city"));
            wxJSONGroupFriend.setSex(groupMemberJSON.getInteger("sex"));
            wxJSONGroupFriend.setHeadimgurl(groupMemberJSON.getString("headimgurl"));
            wxJSONGroupFriend.setCreateDate(LocalDateTime.now());
            wxJSONGroupFriend.setUpdateDate(LocalDateTime.now());
            wxJSONGroupFriend.setCreateBy("mgg");
            wxJSONGroupFriend.setUpdateBy("mgg");
            if (CollectionUtil.isEmpty(iWxGroupFriendService.listObjs(new QueryWrapper<WxGroupFriend>().eq("wx_id", wxJSONGroupFriend.getWxId()).eq("group_id", wxJSONGroupFriend.getGroupId())))) {
                iWxGroupFriendService.save(wxJSONGroupFriend);
            } else {
                iWxGroupFriendService.update(wxJSONGroupFriend, new UpdateWrapper<WxGroupFriend>().eq("robot_id", robotWxId).eq("wx_id", wxJSONGroupFriend.getWxId()).eq("group_id", wxJSONGroupFriend.getGroupId()));
            }
            //删除图片
            try {
                Thread.sleep(5000);
                FileUtil.del(file);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void removeGroupMemberReply(WxMessage wxMessage) throws UnsupportedEncodingException {
        JSONObject jsonObject = JSONObject.parseObject(wxMessage.getMsg());
        StringBuffer sb = new StringBuffer();
        sb.append("江湖有缘再见，").append(jsonObject.getString("member_nickname")).append("退出群聊");
        wxBaseService.sendTextMsg(robotWxId, jsonObject.getString("group_wxid"), sb.toString(), 0);
        JSONObject groupMemberJSON = wxBaseService.getGroupMember(robotWxId, jsonObject.getString("group_wxid"), jsonObject.getString("member_wxid")).getJSONObject("data");
        //下载图片发送
        String fileUrl = groupMemberJSON.getString("headimgurl");
        String fileName = UUID.randomUUID().toString();
        File file = new File(filePath + fileName + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HttpUtil.download(fileUrl, fos, true);
        wxBaseService.sendImageMsg(robotWxId, jsonObject.getString("group_wxid"), file.getAbsolutePath(), 0);
        //数据库同步信息
        iWxGroupFriendService.remove(new QueryWrapper<WxGroupFriend>().eq("group_id", jsonObject.getString("group_wxid")).eq("wx_id", jsonObject.getString("member_wxid")));
        //删除图片
        try {
            Thread.sleep(5000);
            FileUtil.del(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshFriend(WxMessage wxMessage) {
        //wx下所有好友的集合
        JSONObject friendObject = null;

        try {
            friendObject = wxBaseService.getFriendList(wxMessage.getRobotWxId(), 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //猫获取的好友集合
        JSONArray friendArray = friendObject.getJSONArray("data");
        //db批量修改的数据
        List<WxFriend> dbFriendList = new ArrayList<>();
        //功能集合
        List<WxFriendFeature> wxFriendFeatureList = iWxFriendFeatureService.list();
        //db批量修改的数据
        List<WxFriendFeatureFriend> dbFriendFeatureFriendList = new ArrayList<>();

        if (!JSONUtil.isNull(friendArray)) {
            //删除未使用的旧数据 添加新数据
            iWxFriendService.remove(new QueryWrapper<WxFriend>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", false));
            iWxFriendFeatureFriendService.remove(new QueryWrapper<WxFriendFeatureFriend>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", false));
            List<WxFriend> wxFriendList = iWxFriendService.list(new QueryWrapper<WxFriend>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", true));
            List<WxFriendFeatureFriend> wxFriendFeatureFriendList = iWxFriendFeatureFriendService.list(new QueryWrapper<WxFriendFeatureFriend>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", true));
            friendArray.forEach(jsonFriendObject -> {
                JSONObject friendValueObject = (JSONObject) jsonFriendObject;
                String jsonWxId = friendValueObject.getString("wxid");

                //重复为true的wxId不插入
                boolean isTrueWxIdFlag = true;
                if (CollectionUtil.isNotEmpty(wxFriendList)) {
                    for (int i = 0; i < wxFriendList.size(); i++) {
                        if (wxFriendList.get(i).getWxId().equals(jsonWxId)
                                && wxFriendList.get(i).getRobotId().equals(wxMessage.getRobotWxId())
                                && wxFriendList.get(i).isUseFlag()) {
                            isTrueWxIdFlag = false;
                            break;
                        }
                    }
                }

                if (isTrueWxIdFlag) {
                    dbFriendList.add(addFriend(friendValueObject, jsonWxId));
                }
                wxFriendFeatureList.forEach(wxFriendFeature -> {
                    boolean isWxFriendFeatureFriendFlag = true;
                    if (CollectionUtil.isNotEmpty(wxFriendFeatureFriendList)) {
                        for (int i = 0; i < wxFriendFeatureFriendList.size(); i++) {
                            if (wxFriendFeatureFriendList.get(i).getWxId().equals(jsonWxId)
                                    && wxFriendFeatureFriendList.get(i).getRobotId().equals(wxMessage.getRobotWxId())
                                    && wxFriendFeatureFriendList.get(i).getFeatureId().equals(wxFriendFeature.getId())) {
                                isWxFriendFeatureFriendFlag = false;
                                break;
                            }
                        }
                    }

                    if (isWxFriendFeatureFriendFlag) {
                        WxFriendFeatureFriend wxFriendFeatureFriend = new WxFriendFeatureFriend();
                        wxFriendFeatureFriend.setUseFlag(false);
                        wxFriendFeatureFriend.setFeatureId(wxFriendFeature.getId());
                        wxFriendFeatureFriend.setCreateBy("mgg");
                        wxFriendFeatureFriend.setCreateDate(LocalDateTime.now());
                        wxFriendFeatureFriend.setUpdateDate(LocalDateTime.now());
                        wxFriendFeatureFriend.setUpdateBy("mgg");
                        wxFriendFeatureFriend.setWxId(jsonWxId);
                        wxFriendFeatureFriend.setRobotId(wxMessage.getRobotWxId());
                        dbFriendFeatureFriendList.add(wxFriendFeatureFriend);
                    }
                });
            });
            iWxFriendFeatureFriendService.saveBatch(dbFriendFeatureFriendList);
            iWxFriendService.saveBatch(dbFriendList);

        }

    }


    @Override
    public void refreshGroup(WxMessage wxMessage) {
        //wx下所有群聊的集合
        JSONObject groupObject = null;
        //DB下当前robot所有群聊的集合
        try {
            groupObject = wxBaseService.getGroupList(wxMessage.getRobotWxId(), 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //猫获取的群聊集合
        JSONArray groupArray = groupObject.getJSONArray("data");
        //功能集合
        List<WxGroupFeature> wxGroupFeatureList = iWxGroupFeatureService.list();
        //db批量修改的数据
        List<WxGroup> dbGroupList = new ArrayList<>();
        List<WxGroupFeatureGroup> dbGroupFeatureGroupList = new ArrayList<>();

        if (!JSONUtil.isNull(groupArray)) {
            //删除未使用的旧数据 添加新数据
            iWxGroupService.remove(new QueryWrapper<WxGroup>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", false));
            iWxGroupFeatureGroupService.remove(new QueryWrapper<WxGroupFeatureGroup>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", false));
            List<WxGroup> wxGroupList = iWxGroupService.list(new QueryWrapper<WxGroup>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", true));
            List<WxGroupFeatureGroup> wxGroupFeatureGroupList = iWxGroupFeatureGroupService.list(new QueryWrapper<WxGroupFeatureGroup>().eq("robot_id", wxMessage.getRobotWxId()).eq("use_flag", true));
            groupArray.forEach(jsonGroupObject -> {
                JSONObject groupValueObject = (JSONObject) jsonGroupObject;
                String jsonWxId = groupValueObject.getString("wxid");
                //重复为true的wxId不插入
                boolean isTrueGroupIdFlag = true;
                if (CollectionUtil.isNotEmpty(wxGroupList)) {
                    for (int i = 0; i < wxGroupList.size(); i++) {
                        if (wxGroupList.get(i).getGroupId().equals(jsonWxId)
                                && wxGroupList.get(i).getRobotId().equals(wxMessage.getRobotWxId())
                                && wxGroupList.get(i).getUseFlag()) {
                            isTrueGroupIdFlag = false;
                            break;
                        }
                    }
                }

                if (isTrueGroupIdFlag) {
                    dbGroupList.add(addGroup(groupValueObject, jsonWxId));
                }


                wxGroupFeatureList.forEach(wxGroupFeature -> {
                    boolean isWxGroupFeatureGroup = true;
                    if (CollectionUtil.isNotEmpty(wxGroupFeatureGroupList)) {
                        for (int i = 0; i < wxGroupFeatureGroupList.size(); i++) {
                            if (wxGroupFeatureGroupList.get(i).getGroupId().equals(jsonWxId)
                                    && wxGroupFeatureGroupList.get(i).getRobotId().equals(wxMessage.getRobotWxId())
                                    && wxGroupFeatureGroupList.get(i).getFeatureId().equals(wxGroupFeature.getId())) {
                                isWxGroupFeatureGroup = false;
                                break;
                            }
                        }
                    }

                    if (isWxGroupFeatureGroup) {
                        WxGroupFeatureGroup wxGroupFeatureGroup = new WxGroupFeatureGroup();
                        wxGroupFeatureGroup.setUseFlag(false);
                        wxGroupFeatureGroup.setFeatureId(wxGroupFeature.getId());
                        wxGroupFeatureGroup.setCreateBy("mgg");
                        wxGroupFeatureGroup.setCreateDate(LocalDateTime.now());
                        wxGroupFeatureGroup.setUpdateDate(LocalDateTime.now());
                        wxGroupFeatureGroup.setUpdateBy("mgg");
                        wxGroupFeatureGroup.setGroupId(jsonWxId);
                        wxGroupFeatureGroup.setRobotId(wxMessage.getRobotWxId());
                        dbGroupFeatureGroupList.add(wxGroupFeatureGroup);
                    }

                });
            });
            iWxGroupFeatureGroupService.saveBatch(dbGroupFeatureGroupList);
            iWxGroupService.saveBatch(dbGroupList);
        }
    }

    @Override
    public String refreshRobot(WxMessage wxMessage) {
// 查询机器人列表 多个机器人使用
//        try {
//            wxBaseService.getLoggedAccountList();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        JSONObject robotJSONObject = JSONObject.parseObject(wxMessage.getMsg());
        WxRobot wxRobot = new WxRobot();
        wxRobot.setWxId(robotJSONObject.getString("wxid"));
        wxRobot.setWxNum(robotJSONObject.getString("wx_num"));
        wxRobot.setNickname(robotJSONObject.getString("nickname"));
        wxRobot.setHeadimgurl(robotJSONObject.getString("headimgurl"));
        wxRobot.setCountry(robotJSONObject.getString("country"));
        wxRobot.setProvince(robotJSONObject.getString("province"));
        wxRobot.setCity(robotJSONObject.getString("city"));
        wxRobot.setSex(robotJSONObject.getInteger("sex"));
        wxRobot.setScene(robotJSONObject.getInteger("scene"));
        wxRobot.setSignature(robotJSONObject.getString("signature"));
        wxRobot.setBackgroundimgurl(robotJSONObject.getString("backgroundimgurl"));
        wxRobot.setWxWindHandle(robotJSONObject.getString("wx_wind_handle"));
        wxRobot.setWxPid(robotJSONObject.getString("wx_pid"));
        wxRobot.setStatus(robotJSONObject.getInteger("status"));
        wxRobot.setCreateDate(LocalDateTime.now());
        wxRobot.setCreateBy("mgg");
        wxRobot.setUpdateDate(LocalDateTime.now());
        wxRobot.setUpdateBy("mgg");
        iWxRobotService.saveOrUpdate(wxRobot);
        return wxRobot.getWxId();
    }

    @Override
    public void refreshGroupFriend(WxMessage wxMessage) {
        //遍历出所有groupId 遍历微信返回的群聊人员列表
        List<WxGroup> groupList = iWxGroupService.list(new QueryWrapper<WxGroup>().eq("use_flag", true).eq("robot_id", wxMessage.getRobotWxId()));
        List<WxGroupFriend> groupFriendList = new ArrayList<>();
        iWxGroupFriendService.remove(new QueryWrapper<WxGroupFriend>().eq("robot_id", wxMessage.getRobotWxId()));
        if (CollectionUtil.isNotEmpty(groupList)) {
            //遍历groupList 调用baseservice查询各个群的好友信息
            groupList.forEach(wxGroup -> {
                try {
                    JSONArray JSONFriendArray = wxBaseService.getGroupMemberList(wxGroup.getRobotId(), wxGroup.getGroupId(), 1).getJSONArray("data");
                    for (int i = 0; i < JSONFriendArray.size(); i++) {
                        addGroupFriend(groupFriendList, wxGroup, JSONFriendArray, i);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            iWxGroupFriendService.saveOrUpdateBatch(groupFriendList);
        }
    }

    @Override
    public void refreshGroupFeature(WxMessage wxMessage) throws UnsupportedEncodingException {
        List<WxGroup> list = iWxGroupService.list(new QueryWrapper<WxGroup>().eq("robot_id", robotWxId));
        List<WxGroupFeature> list1 = iWxGroupFeatureService.list();
        List<WxGroupFeatureGroup> batchList = new ArrayList();
        List<WxGroupFeatureGroup> wxGroupFeatureGroupList = iWxGroupFeatureGroupService.list(new QueryWrapper<WxGroupFeatureGroup>().eq("robot_id", robotWxId));

        list.forEach(wxGroup -> {
            list1.forEach(wxGroupFeature -> {
                if (CollectionUtil.isEmpty(wxGroupFeatureGroupList)) {
                    WxGroupFeatureGroup wxGroupFeatureGroup = new WxGroupFeatureGroup();
                    wxGroupFeatureGroup.setUseFlag(false);
                    wxGroupFeatureGroup.setFeatureId(wxGroupFeature.getId());
                    wxGroupFeatureGroup.setCreateBy("mgg");
                    wxGroupFeatureGroup.setCreateDate(LocalDateTime.now());
                    wxGroupFeatureGroup.setUpdateDate(LocalDateTime.now());
                    wxGroupFeatureGroup.setUpdateBy("mgg");
                    wxGroupFeatureGroup.setGroupId(wxGroup.getGroupId());
                    wxGroupFeatureGroup.setRobotId(robotWxId);
                    batchList.add(wxGroupFeatureGroup);
                } else {

                }


            });
        });
        iWxGroupFeatureGroupService.saveOrUpdateBatch(batchList);
    }

    @Override
    public void refreshFriendFeature(WxMessage wxMessage) throws UnsupportedEncodingException {
        List<WxFriend> list = iWxFriendService.list(new QueryWrapper<WxFriend>().eq("robot_id", robotWxId));
        List<WxFriendFeature> list1 = iWxFriendFeatureService.list();
        List<WxFriendFeatureFriend> batchList = new ArrayList();

        list.forEach(wxFriend -> {
            list1.forEach(wxFriendFeature -> {
                WxFriendFeatureFriend wxFriendFeatureFriend = new WxFriendFeatureFriend();
                wxFriendFeatureFriend.setUseFlag(false);
                wxFriendFeatureFriend.setFeatureId(wxFriendFeature.getId());
                wxFriendFeatureFriend.setCreateBy("mgg");
                wxFriendFeatureFriend.setCreateDate(LocalDateTime.now());
                wxFriendFeatureFriend.setUpdateDate(LocalDateTime.now());
                wxFriendFeatureFriend.setUpdateBy("mgg");
                wxFriendFeatureFriend.setWxId(wxFriend.getWxId());
                wxFriendFeatureFriend.setRobotId(robotWxId);
                batchList.add(wxFriendFeatureFriend);
            });
        });
        iWxFriendFeatureFriendService.saveBatch(batchList);
    }

    @Override
    public void updateFeature(int chatType, WxMessage wxMessage, String featureId, String featureName) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        //私聊对方输入开启XX或者关闭XX后普通消息回复提示
        if (chatType == MessageTypeConstants.PRIVATE_CHAT_TYPE) {

            if (wxMessage.getMsg().contains(MessageContentConstants.START_FEATURE)) {
                iWxFriendFeatureFriendService.update(new UpdateWrapper<WxFriendFeatureFriend>().set("use_flag", true).eq("robot_id", wxMessage.getRobotWxId()).eq("wx_id", wxMessage.getFromWxId()).eq("feature_id", featureId));
                sb.append("已开启").append(featureName).append("功能");
            }

            if (wxMessage.getMsg().contains(MessageContentConstants.END_FEATURE)) {
                iWxFriendFeatureFriendService.update(new UpdateWrapper<WxFriendFeatureFriend>().set("use_flag", false).eq("robot_id", wxMessage.getRobotWxId()).eq("wx_id", wxMessage.getFromWxId()).eq("feature_id", featureId));
                sb.append("已关闭").append(featureName).append("功能");
            }


            wxBaseService.sendTextMsg(wxMessage.getRobotWxId(), wxMessage.getFromWxId(), sb.toString(), 1);
        }
        //群聊中只有群主输入开启XX或者修改XX后艾特回复群主
        if (chatType == MessageTypeConstants.GROUP_CHAT_TYPE) {
            JSONObject jsonObject = wxBaseService.getGroupMemberList(robotWxId, wxMessage.getFromWxId(), 1);

            String lordWxId = jsonObject.getJSONArray("data").getJSONObject(0).getString("wxid");
            String lordNickName = jsonObject.getJSONArray("data").getJSONObject(0).getString("nickname");
            if (wxMessage.getMsg().contains(MessageContentConstants.START_FEATURE) && wxMessage.getFinalFromWxId().equals(lordWxId)) {
                iWxGroupFeatureGroupService.update(new UpdateWrapper<WxGroupFeatureGroup>().set("use_flag", true).eq("robot_id", wxMessage.getRobotWxId()).eq("group_id", wxMessage.getFromWxId()).eq("feature_id", featureId));
                sb.append("已开启").append(featureName).append("功能");
            }

            if (wxMessage.getMsg().contains(MessageContentConstants.END_FEATURE)) {
                iWxGroupFeatureGroupService.update(new UpdateWrapper<WxGroupFeatureGroup>().set("use_flag", false).eq("robot_id", wxMessage.getRobotWxId()).eq("group_id", wxMessage.getFromWxId()).eq("feature_id", featureId));
                sb.append("已关闭").append(featureName).append("功能");
            }
            wxBaseService.sendGroupAtMsg(wxMessage.getRobotWxId(), wxMessage.getFinalFromWxId(), lordWxId, lordNickName, sb.toString(), 1);
        }
    }


    @Override
    public List<WxFriendFeature> findFriendFeatureList(WxMessage wxMessage) {
        return iWxFriendFeatureService.list();
    }

    @Override
    public List<Object> findFilterList(String robotWxId) {
        List<Object> resultList = iWxFriendService.listObjs(new QueryWrapper<WxFriend>().select("wx_id").eq("robot_id", robotWxId).eq("use_flag", true));
        List<Object> groupList = iWxGroupService.listObjs(new QueryWrapper<WxGroup>().select("group_id").eq("robot_id", robotWxId).eq("use_flag", true));
        resultList.addAll(groupList);
        return resultList;
    }

    @Override
    public List<WxGroupFeature> findGroupFeatureList(WxMessage wxMessage) {
        return iWxGroupFeatureService.list();
    }

    /**
     * 保存群聊好友相信信息
     *
     * @param groupFriendList 群聊好友详细信息集合
     * @param wxGroup         群聊集合
     * @param JSONFriendArray 群聊好友json
     * @param i               群聊好友集合的下标
     * @throws UnsupportedEncodingException
     */
    private void addGroupFriend(List<WxGroupFriend> groupFriendList, WxGroup wxGroup, JSONArray JSONFriendArray, int i) throws UnsupportedEncodingException {
        JSONObject JSONGroupMember = wxBaseService.getGroupMember(wxGroup.getRobotId(), wxGroup.getGroupId(), JSONFriendArray.getJSONObject(i).getString("wxid")).getJSONObject("data");
        WxGroupFriend wxJSONGroupFriend = new WxGroupFriend();
        wxJSONGroupFriend.setGroupId(wxGroup.getGroupId());
        wxJSONGroupFriend.setNickname(JSONGroupMember.getString("nickname"));
        wxJSONGroupFriend.setWxId(JSONGroupMember.getString("wxid"));
        wxJSONGroupFriend.setCity(JSONGroupMember.getString("city"));
        wxJSONGroupFriend.setSex(JSONGroupMember.getInteger("sex"));
        wxJSONGroupFriend.setHeadimgurl(JSONGroupMember.getString("headimgurl"));
        wxJSONGroupFriend.setCreateDate(LocalDateTime.now());
        wxJSONGroupFriend.setUpdateDate(LocalDateTime.now());
        wxJSONGroupFriend.setCreateBy("mgg");
        wxJSONGroupFriend.setUpdateBy("mgg");
        wxJSONGroupFriend.setRobotId(wxGroup.getRobotId());
        groupFriendList.add(wxJSONGroupFriend);
    }

    private WxGroup addGroup(JSONObject groupValueObject, String jsonGroupId) {
        WxGroup wxGroup = new WxGroup();
        wxGroup.setGroupId(jsonGroupId);
        wxGroup.setNickname(groupValueObject.getString("nickname"));
        wxGroup.setRobotId(groupValueObject.getString("robot_wxid"));
        wxGroup.setCreateDate(LocalDateTime.now());
        wxGroup.setUpdateDate(LocalDateTime.now());
        wxGroup.setCreateBy("mgg");
        wxGroup.setUpdateBy("mgg");
        wxGroup.setUseFlag(false);
        return wxGroup;
    }

    private WxFriend addFriend(JSONObject friendValueObject, String jsonWxId) {
        WxFriend wxFriend = new WxFriend();
        wxFriend.setWxId(jsonWxId);
        wxFriend.setNickname(friendValueObject.getString("nickname"));
        wxFriend.setRobotId(friendValueObject.getString("robot_wxid"));
        wxFriend.setCreateDate(LocalDateTime.now());
        wxFriend.setUpdateDate(LocalDateTime.now());
        wxFriend.setCreateBy("mgg");
        wxFriend.setUpdateBy("mgg");
        wxFriend.setUseFlag(false);
        return wxFriend;
    }


}
