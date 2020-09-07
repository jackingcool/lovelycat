package com.lovelycat.wx.db.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.db.entity.WxGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lovelycat.wx.db.entity.WxGroupFriend;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mgg
 * @since 2020-07-21
 */
public interface IWxGroupService extends IService<WxGroup> {

    WxGroup addGroup(JSONObject groupValueObject, String jsonGroupId);
}
