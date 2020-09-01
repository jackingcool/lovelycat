package com.lovelycat.wx.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.db.entity.WxFriend;
import com.lovelycat.wx.db.mapper.WxFriendMapper;
import com.lovelycat.wx.db.service.IWxFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
