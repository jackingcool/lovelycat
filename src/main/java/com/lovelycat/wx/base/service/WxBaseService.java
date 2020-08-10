package com.lovelycat.wx.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.base.entity.WxMessage;

import java.io.UnsupportedEncodingException;

/**
 * 微信api
 *
 * @author mgg
 */
public interface WxBaseService {
    /**
     * 发送文字消息(好友或者群)
     *
     * @param robotWxId 登录账号id，用哪个账号去发送这条消息
     * @param toWxId    对方的id，可以是群或者好友id
     * @param msg       消息内容
     * @param timeout   设置延迟
     */
    void sendTextMsg(String robotWxId, String toWxId, String msg, int timeout);

    /**
     * 发送群消息并艾特某人
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param toWxId    对方的id，可以是群或者好友id
     * @param atWxId    艾特的id，群成员的id
     * @param atName    艾特的昵称，群成员的昵称
     * @param msg       消息内容
     * @param timeout   设置延迟
     * @return any
     * @access public
     */
    Object sendGroupAtMsg(String robotWxId, String toWxId, String atWxId, String atName, String msg, int timeout);

    /**
     * 发送图片消息
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param toWxId    对方的id，可以是群或者好友id
     * @param path      图片的绝对路径
     * @param timeout   设置延迟
     * @return 通过返回值删除绝对路径的图片
     */
    Object sendImageMsg(String robotWxId, String toWxId, String path, int timeout);

    /**
     * 获取好友列表
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param isRefresh 0 缓存读取 1 刷新
     * @return
     */
    JSONObject getFriendList(String robotWxId, int isRefresh) throws UnsupportedEncodingException;

    /**
     * 艾特所有人(修改群公告)
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param groupId   群ID
     * @param notice    群公共内容
     * @return
     */
    Object modifyGroupNotice(String robotWxId, String groupId, String notice);

    /**
     * 点歌
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param toWxId    对方的id，可以是群或者好友id
     * @param name      歌名
     * @return
     */
    Object sendMusicMsg(String robotWxId, String toWxId, String name);

    /**
     * 发送链接消息
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param toWxId    对方的id，可以是群或者好友id
     * @param title     链接标题
     * @param text      链接内容
     * @param targetUrl 跳转链接
     * @param picUrl    图片链接
     * @return
     */
    Object sendLinkMsg(String robotWxId, String toWxId, String title, String text, String targetUrl, String picUrl);

    /**
     * 获取群聊列表
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param isRefresh 0 缓存读取 1 刷新
     * @return
     */
    JSONObject getGroupList(String robotWxId, int isRefresh) throws UnsupportedEncodingException;


    /**
     * 获取群成员列表
     *
     * @param robotWxId 账户id，用哪个账号去发送这条消息
     * @param groupWxId 群id
     * @param isRefresh 0 缓存读取 1 刷新
     * @return JSONObject
     */
    JSONObject getGroupMemberList(String robotWxId, String groupWxId, int isRefresh) throws UnsupportedEncodingException;

    /**
     * 获取群聊好友信息
     *
     * @param robotWxId  机器人id
     * @param groupWxId  群Id
     * @param memberWxId wxid
     * @return JSONObject
     */
    JSONObject getGroupMember(String robotWxId, String groupWxId, String memberWxId) throws UnsupportedEncodingException;

    /**
     * 获取登录列表
     * @return
     */
    JSONObject getLoggedAccountList() throws UnsupportedEncodingException;
}
