package com.lovelycat.wx.db.service;

import com.alibaba.fastjson.JSONArray;
import com.lovelycat.wx.db.entity.WxGroup;
import com.lovelycat.wx.db.entity.WxGroupFriend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mgg
 * @since 2020-07-24
 */
public interface IWxGroupFriendService extends IService<WxGroupFriend> {
    void addGroupFriend(List<WxGroupFriend> groupFriendList, WxGroup wxGroup, JSONArray JSONFriendArray, int i) throws UnsupportedEncodingException;
}
