package com.lovelycat.wx.base.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.base.entity.Results;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信api实现
 *
 * @author mgg
 */
@Service("WxBaseService")
public class WxBaseServiceImpl implements WxBaseService {

    @Value("${wx.send.url}")
    private String url;


    @Override
    public void sendTextMsg(String robotWxId, String toWxId, String msg, int timeout) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("type", 100);
        paramsMap.put("msg", URLEncoder.encode(msg));
        paramsMap.put("to_wxid", toWxId);
        paramsMap.put("robot_wxid", robotWxId);
        sendSGHttp(url, paramsMap, "", timeout);
    }

    @Override
    public Object sendGroupAtMsg(String robotWxId, String toWxId, String atWxId, String atName, String msg, int timeout) {
        // 调用Api组件
        Map<String, Object> map = null;
        try {
            map = Results.of().put("type", 102).put("msg", URLEncoder.encode(msg, "utf-8")).put("to_wxid", toWxId).put("at_wxid", atWxId).put("robot_wxid", robotWxId).put("at_name", atName).toMap();
        } catch (UnsupportedEncodingException ignored) {
        }
        return sendSGHttp(url, map, "", timeout);
    }

    @Override
    public Object sendImageMsg(String robotWxId, String toWxId, String path, int timeout) {

        Map<String, Object> map = null;
        map = Results.of().put("type", 103).put("msg", path).put("to_wxid", toWxId).put("robot_wxid", robotWxId).toMap();
        return sendSGHttp(url, map, "", timeout);
    }

    @Override
    public JSONObject getFriendList(String robotWxId, int isRefresh) throws UnsupportedEncodingException {
        Map<String, Object> map = null;
        map = Results.of().put("type", 204).put("is_refresh", isRefresh).put("robot_wxid", robotWxId).toMap();
        StringBuffer sb = new StringBuffer(URLDecoder.decode(sendSGHttp(url, map, "", 0), "UTF-8"));
        sb.deleteCharAt(sb.indexOf("[") - 1);
        sb.deleteCharAt(sb.lastIndexOf("]") + 1);
        return JSONObject.parseObject(sb.toString());
    }

    @Override
    public Object modifyGroupNotice(String robotWxId, String fromWxId, String notice) {
        Map<String, Object> map = null;
        map = Results.of().put("type", 308).put("robot_wxid", robotWxId).put("group_wxid", fromWxId).put("notice", notice).toMap();
        return sendSGHttp(url, map, "", 0);
    }

    @Override
    public Object sendMusicMsg(String robotWxId, String toWxId, String name) {
        Map<String, Object> map = null;
        map = Results.of().put("type", 108).put("robot_wxid", robotWxId).put("to_wxid", toWxId).put("msg", name).toMap();
        return sendSGHttp(url, map, "", 1000);
    }

    @Override
    public Object sendLinkMsg(String robotWxId, String toWxId, String title, String text, String targetUrl, String picUrl) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("title", title);
        map.put("text", text);
        map.put("url", targetUrl);
        map.put("pic", picUrl);

        Map<String, Object> resultMap = null;
        resultMap = Results.of().put("type", 107).put("msg", map).put("to_wxid", toWxId).put("robot_wxid", robotWxId).toMap();
        return sendSGHttp(url, resultMap, "", 1000);
    }

    @Override
    public JSONObject getGroupList(String robotWxId, int isRefresh) throws UnsupportedEncodingException {
        Map<String, Object> map = null;
        map = Results.of().put("type", 205).put("is_refresh", isRefresh).put("robot_wxid", robotWxId).toMap();
        StringBuffer sb = new StringBuffer(URLDecoder.decode(sendSGHttp(url, map, "", 0), "UTF-8"));
        sb.deleteCharAt(sb.indexOf("[") - 1);
        sb.deleteCharAt(sb.lastIndexOf("]") + 1);
        return JSONObject.parseObject(sb.toString());
    }

    @Override
    public JSONObject getGroupMemberList(String robotWxId, String groupWxId, int isRefresh) throws UnsupportedEncodingException {
        Map<String, Object> map = null;
        map = Results.of().put("type", 206).put("is_refresh", isRefresh).put("robot_wxid", robotWxId).put("group_wxid", groupWxId).toMap();
        StringBuffer sb = new StringBuffer(URLDecoder.decode(sendSGHttp(url, map, "", 0), "UTF-8"));
        sb.deleteCharAt(sb.indexOf("[") - 1);
        sb.deleteCharAt(sb.lastIndexOf("]") + 1);
        return JSONObject.parseObject(sb.toString());
    }

    @Override
    public JSONObject getGroupMember(String robotWxId, String groupWxId, String memberWxId) throws UnsupportedEncodingException {
        Map<String, Object> map = null;
        map = Results.of().put("type", 207).put("member_wxid", memberWxId).put("robot_wxid", robotWxId).put("group_wxid", groupWxId).toMap();
        StringBuffer sb = new StringBuffer(URLDecoder.decode(sendSGHttp(url, map, "", 0), "UTF-8"));
        sb.deleteCharAt(sb.indexOf("{") + 17);
        sb.deleteCharAt(sb.lastIndexOf("}") - 1);
        return JSONObject.parseObject(sb.toString());
    }

    @Override
    public JSONObject getLoggedAccountList() throws UnsupportedEncodingException {
        Map<String, Object> map = null;
        map = Results.of().put("type", 203).toMap();
        StringBuffer sb = new StringBuffer(URLDecoder.decode(sendSGHttp(url, map, "", 0), "UTF-8"));
        sb.deleteCharAt(sb.indexOf("{") + 17);
        sb.deleteCharAt(sb.lastIndexOf("}") - 1);
        System.out.println(sb.toString());
        return JSONObject.parseObject(sb.toString());
    }

    @Override
    public void removeGroupMember(String robotWxId, String groupId, String memberWxId) {
        Map<String, Object> map = null;
        map = Results.of().put("type", 306).put("robot_wxid", robotWxId).put("to_wxid", groupId).put("member_wxid", memberWxId).toMap();
        sendSGHttp(url, map, "", 0);
    }

    /**
     * 执行一个 HTTP 请求，仅仅是post组件，其他语言请自行替换即可
     */
    public String sendSGHttp(String url, Map<String, Object> map, String method, int timeout) {
        Map<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("data", JSON.toJSONString(map));
        return HttpUtil.post(url, requestMap, timeout);
    }
}
