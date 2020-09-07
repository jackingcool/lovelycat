package com.lovelycat.wx.db.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.db.entity.WxFriend;
import com.lovelycat.wx.db.mapper.WxFriendMapper;
import com.lovelycat.wx.db.service.IWxFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mgg
 * @since 2020-07-16
 */
@Service("IWxFriendService")
public class WxFriendServiceImpl extends ServiceImpl<WxFriendMapper, WxFriend> implements IWxFriendService {

    @Autowired
    WxFriendMapper wxFriendMapper;


    @Override
    public List<WxFriend> findFriendWeatherReply() {
        return wxFriendMapper.selectFriendWeatherReply();
    }

    @Override
    public WxFriend addFriend(JSONObject friendValueObject, String jsonWxId) {
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
