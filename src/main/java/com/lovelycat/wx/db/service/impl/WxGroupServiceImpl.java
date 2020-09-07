package com.lovelycat.wx.db.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.db.entity.WxGroup;
import com.lovelycat.wx.db.entity.WxGroupFriend;
import com.lovelycat.wx.db.mapper.WxGroupMapper;
import com.lovelycat.wx.db.service.IWxGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mgg
 * @since 2020-07-21
 */
@Service
public class WxGroupServiceImpl extends ServiceImpl<WxGroupMapper, WxGroup> implements IWxGroupService {


    @Override
    public WxGroup addGroup(JSONObject groupValueObject, String jsonGroupId) {
        WxGroup wxGroup = new WxGroup();
        wxGroup.setGroupId(jsonGroupId);
        wxGroup.setNickname(groupValueObject.getString("nickname"));
        wxGroup.setRobotId(groupValueObject.getString("robot_wxid"));
        wxGroup.setCreateDate(LocalDateTime.now());
        wxGroup.setUpdateDate(LocalDateTime.now());
        wxGroup.setCreateBy("mgg");
        wxGroup.setUpdateBy("mgg");
        wxGroup.setUseFlag(false);
        return wxGroup;
    }
}
