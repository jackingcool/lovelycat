package com.lovelycat.wx.db.service;

import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.db.entity.WxFriend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mgg
 * @since 2020-07-16
 */
public interface IWxFriendService extends IService<WxFriend> {

    /**
     * 查询发送定时和天气预报的好友
     * @return
     */
    List<WxFriend> findFriendWeatherReply();
}

