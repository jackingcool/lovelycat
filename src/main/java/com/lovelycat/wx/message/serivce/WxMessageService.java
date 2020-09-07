package com.lovelycat.wx.message.serivce;

import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.db.entity.WxFriendFeature;
import com.lovelycat.wx.db.entity.WxGroupFeature;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 机器人服务功能
 *
 * @author mgg
 */
public interface WxMessageService {
    /**
     * 只对配置文件中设置的wxId使用机器人
     *
     * @param wxMessage  接收的消息内容
     * @param filterList 可使用的微信好友和群聊信息集合
     * @return true 允许使用机器人机器人回复
     */
    boolean filterMessages(WxMessage wxMessage, List<Object> filterList);

    /**
     * 艾特后的回复
     *
     * @param wxMessage 接收的消息内容
     */
    void afterAtReply(WxMessage wxMessage) throws Exception;

    /**
     * 舔狗日记
     *
     * @param wxMessage 接收的消息内容
     */
    void LickingDogDiary(WxMessage wxMessage);

    /**
     * 测试用
     */
    void test(WxMessage wxMessage) throws Exception;

    /**
     * 通过输入XX(城市名)天气 回复 天气预报
     *
     * @param wxMessage 接收的消息内容
     */
    void weatherReply(WxMessage wxMessage);

    /**
     * 游戏链接回复
     *
     * @param wxMessage
     */
    void gameLinkReply(WxMessage wxMessage);

    /**
     * cpdd
     *
     * @param wxMessage
     */
    void cpdd(WxMessage wxMessage);

    /**
     * 点歌
     *
     * @param wxMessage
     */
    void song(WxMessage wxMessage);

    /**
     * 新增成员回复
     *
     * @param wxMessage
     */
    void addGroupMemberReply(WxMessage wxMessage) throws UnsupportedEncodingException, FileNotFoundException;

    /**
     * 成员加紧少回复
     *
     * @param wxMessage
     */
    void removeGroupMemberReply(WxMessage wxMessage) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException;


    /**
     * 刷新好友列表 并修改db
     *
     * @param wxMessage
     */
    void refreshFriend(WxMessage wxMessage) throws UnsupportedEncodingException;

    /**
     * 刷新群聊列表 并修改db
     *
     * @param wxMessage
     */
    void refreshGroup(WxMessage wxMessage) throws UnsupportedEncodingException;

    /**
     * 刷新当前机器人
     * 查询登陆过的机器人列表 无则删除
     *
     * @param wxMessage
     */
    String refreshRobot(WxMessage wxMessage);

    /**
     * 刷新当前机器人群聊好友
     *
     * @param wxMessage
     */
    void refreshGroupFriend(WxMessage wxMessage);

    /**
     * 刷新群功能权限
     *
     * @param wxMessage
     */
    void refreshGroupFeature(WxMessage wxMessage) throws UnsupportedEncodingException;

    /**
     * 刷新好友功能权限
     *
     * @param wxMessage
     */
    void refreshFriendFeature(WxMessage wxMessage) throws UnsupportedEncodingException;

    /**
     * 修改功能权限
     *
     * @param wxMessage      消息信息
     * @param featureId      功能Id
     * @param featureKeyWord 关键字
     */
    void updateFeature(WxMessage wxMessage, String featureId, String featureKeyWord) throws UnsupportedEncodingException;

    /**
     * 查询好友功能列表
     *
     * @param wxMessage
     * @return
     */
    List<WxFriendFeature> findFriendFeatureList(WxMessage wxMessage);

    /**
     * 查询可使用机器人功能的好友和群聊集合
     *
     * @param robotWxId 当前机器人WxId
     * @return FilterList
     */
    List<Object> findFilterList(String robotWxId);

    /**
     * 查询群聊功能集合
     *
     * @param wxMessage
     * @return
     */
    List<WxGroupFeature> findGroupFeatureList(WxMessage wxMessage);

    /**
     * 查询当前正在使用的机器人
     *
     * @param wxMessage
     * @return
     */
    String findIsUseRobot(WxMessage wxMessage);



    ;

    /**
     * 功能列表
     *
     * @param wxMessage
     */
    void sendFeatureList(WxMessage wxMessage);

    /**
     * 管理员条件下直接删除广告
     *
     * @param wxMessage
     */
    void removeAdvert(WxMessage wxMessage);

    /**
     * 保安日记
     *
     * @param wxMessage
     */
    void securityDiary(WxMessage wxMessage);

    /**
     * 好友自动回复
     *
     * @param wxMessage
     */
    void autoReplyByFriend(WxMessage wxMessage) throws Exception;
}
