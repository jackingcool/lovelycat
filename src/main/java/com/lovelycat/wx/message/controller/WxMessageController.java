package com.lovelycat.wx.message.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.constants.MessageContentConstants;
import com.lovelycat.wx.constants.MessageTypeConstants;
import com.lovelycat.wx.db.entity.WxFriendFeature;
import com.lovelycat.wx.db.entity.WxGroupFeature;
import com.lovelycat.wx.message.serivce.WxMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * wx消息接收主入口
 * 通过可爱猫转发的HTTP请求接收所有微信消息
 *
 * @author mgg
 */

@RestController
public class WxMessageController {

    @Autowired
    WxMessageService wxMessageService;

    private static final Log log = LogFactory.get();

    /**
     * 当前机器人微信Id
     */
    private String robotWxId = "";

    /**
     * 当前可使用的私聊和群聊消息
     *
     * @param request
     */
    private List<Object> filterList = new ArrayList<>();

    /**
     * 查询好友功能
     */
    List<WxFriendFeature> wxFriendFeatureList = new ArrayList<>();
    /**
     * 查询群聊功能
     */
    List<WxGroupFeature> wxGroupFeatureList = new ArrayList<>();


    @PostMapping("/getMsg")
    public void getMessages(HttpServletRequest request) {

        //获取每条wx消息
        WxMessage wxMessage = new WxMessage();
        wxMessage.setType(Integer.parseInt(request.getParameter("type")));
        wxMessage.setMsgType(Integer.parseInt(request.getParameter("msg_type")));
        wxMessage.setFromWxId(request.getParameter("from_wxid"));
        wxMessage.setFinalFromWxId(request.getParameter("final_from_wxid"));
        wxMessage.setRobotWxId(request.getParameter("robot_wxid"));
        wxMessage.setFileUrl(request.getParameter("file_url"));
        wxMessage.setTime(Integer.parseInt(request.getParameter("time")));
        try {
            wxMessage.setFromName(URLDecoder.decode(request.getParameter("from_name").replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8"));
            wxMessage.setFinalNickname(URLDecoder.decode(request.getParameter("final_from_name").replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8"));
            wxMessage.setMsg(URLDecoder.decode(request.getParameter("msg").replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //过滤信息 use_flag为true的w才可以使用功能
        if (CollectionUtil.isEmpty(filterList)) {
            filterList = wxMessageService.findFilterList(robotWxId);
        }
        if (wxMessageService.filterMessages(wxMessage, filterList)) {
            switch (wxMessage.getType()) {
                //私聊消息
                case MessageTypeConstants.PRIVATE_CHAT_TYPE:
                    //获取好友功能列表
                    if (CollectionUtil.isNotEmpty(wxFriendFeatureList)) {
                        //修改好友功能权限  输入 开启XX or 关闭XX
                        wxFriendFeatureList.forEach(wxFriendFeature -> {
                            //开启和关闭功能
                            if (wxMessage.getMsg().equals(MessageContentConstants.START_FEATURE + wxFriendFeature.getFeatureName()) && robotWxId.equals(wxMessage.getRobotWxId())
                                    || wxMessage.getMsg().equals(MessageContentConstants.END_FEATURE + wxFriendFeature.getFeatureName()) && robotWxId.equals(wxMessage.getRobotWxId())) {
                                try {
                                    wxMessageService.updateFeature(MessageTypeConstants.GROUP_CHAT_TYPE, wxMessage, wxFriendFeature.getId(), wxFriendFeature.getFeatureName());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                        });
                    }
                    break;
                //群聊消息
                case MessageTypeConstants.GROUP_CHAT_TYPE:
                    //修改群聊权限 群主列表第一人修改
                    if (CollectionUtil.isNotEmpty(wxGroupFeatureList)) {
                        wxGroupFeatureList.forEach(wxGroupFeature -> {
                            if (wxMessage.getMsg().equals(MessageContentConstants.START_FEATURE + wxGroupFeature.getFeatureName()) && robotWxId.equals(wxMessage.getRobotWxId())
                                    || wxMessage.getMsg().equals(MessageContentConstants.END_FEATURE + wxGroupFeature.getFeatureName()) && robotWxId.equals(wxMessage.getRobotWxId())) {
                                try {
                                    wxMessageService.updateFeature(MessageTypeConstants.GROUP_CHAT_TYPE, wxMessage, wxGroupFeature.getId(), wxGroupFeature.getFeatureName());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                        });
                    }
                    //艾特后自动回复
                    //TODO:部署db后用腾讯ai自动回复

                    wxMessageService.afterAtReply(wxMessage);
                    wxMessageService.LickingDogDiary(wxMessage);
                    //wxMessageService.gameLinkReply(wxMessage);
                    wxMessageService.cpdd(wxMessage);
                    wxMessageService.song(wxMessage);
                    break;
                //暂无
                case MessageTypeConstants.NO_USE_TYPE:
                    break;
                //群成员增加
                case MessageTypeConstants.ADD_GROUP_MEMBER_TYPE:
                    try {
                        wxMessageService.addGroupMemberReply(wxMessage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                //群成员减少
                case MessageTypeConstants.REMOVE_GROUP_MEMBER_TYPE:
                    try {
                        wxMessageService.removeGroupMemberReply(wxMessage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                //收到好友请求
                case MessageTypeConstants.ADD_FRIEND_REQUEST_TYPE:
                    break;
                //二维码收款
                case MessageTypeConstants.QR_CODE_COLLECTION_TYPE:
                    break;
                //收到转账
                case MessageTypeConstants.RECEIVE_TRANSFER_TYPE:
                    break;
                //软件开始启动
                case MessageTypeConstants.THE_SOFTWARE_STARTS_TYPE:
                    log.info("------------软件开始启动---------------");
                    break;
                //新的账号登录完成
                case MessageTypeConstants.NEW_ACCOUNT_LOGIN_COMPLETE_TYPE:
                    //可爱猫登录后第一步 刷新当前机器人的好友和群聊并修改DB
                    //当前机器人wxId
                    robotWxId = wxMessageService.refreshRobot(wxMessage);
                    log.info("------------当前机器人{}---------------", robotWxId);
                    log.info("------------开始刷新好友---------------");
                    wxMessageService.refreshFriend(wxMessage);
                    log.info("------------结束刷新好友---------------");
                    log.info("------------开始刷新群聊---------------");
                    wxMessageService.refreshGroup(wxMessage);
                    log.info("------------结束刷新群聊---------------");
                    log.info("------------开始刷新群聊列表---------------");
                    wxMessageService.refreshGroupFriend(wxMessage);
                    log.info("------------结束刷新群聊列表---------------");
                    log.info("------------开始过滤微信消息------------");
                    filterList = wxMessageService.findFilterList(robotWxId);
                    log.info("------------结束过滤微信消息------------");
                    //获取好友功能列表
                    log.info("------------开始获取好友功能权限------------");
                    wxFriendFeatureList = wxMessageService.findFriendFeatureList(wxMessage);
                    log.info("------------结束获取好友功能权限------------");
                    log.info("------------开始获取群聊功能权限------------");
                    wxGroupFeatureList = wxMessageService.findGroupFeatureList(wxMessage);
                    log.info("------------结束获取群聊功能权限------------");
                    break;
                //账号下线
                case MessageTypeConstants.ACCOUNT_LOGIN_OUT_TYPE:
                    break;
                default:

            }

        }
    }

    @GetMapping("/test")
    public void test(WxMessage wxMessage) throws Exception {
        wxMessageService.test(wxMessage);
    }

}


