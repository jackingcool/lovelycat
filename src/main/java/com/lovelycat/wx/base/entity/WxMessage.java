package com.lovelycat.wx.base.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 微信消息实体类
 *
 * @author mgg
 */
@Data
public class WxMessage {
    /**
     * 事件类型（事件列表可参考 - 事件列表demo）
     */
    private int type;
    /**
     * 消息类型（仅在私聊和群消息事件中，代表消息的表现形式，如文字消息、语音、等等）
     */
    private int msgType;
    /**
     * 1级来源id（比如发消息的人的id）
     */
    private String fromWxId;
    /**
     * 1级来源昵称（比如发消息的人昵称）
     */
    private String fromName;
    /**
     * 2级来源id（群消息事件下，1级来源为群id，2级来源为发消息的成员id，私聊事件下都一样）
     */
    private String finalFromWxId;
    /**
     * 2级来源昵称
     */
    private String finalNickname;
    /**
     * 当前登录的账号（机器人）标识id
     */
    private String robotWxId;
    /**
     * 如果是文件消息（图片、语音、视频、动态表情），这里则是可直接访问的网络地址，非文件消息时为空
     */
    private String fileUrl;
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 请求时间(时间戳10位版本)
     */
    private int time;

}
