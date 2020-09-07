package com.lovelycat.wx.db.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovelycat.wx.base.service.WxBaseService;
import com.lovelycat.wx.db.entity.WxGroup;
import com.lovelycat.wx.db.entity.WxGroupFriend;
import com.lovelycat.wx.db.mapper.WxGroupFriendMapper;
import com.lovelycat.wx.db.service.IWxGroupFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mgg
 * @since 2020-07-24
 */
@Service
public class WxGroupFriendServiceImpl extends ServiceImpl<WxGroupFriendMapper, WxGroupFriend> implements IWxGroupFriendService {
    
    @Autowired
    WxBaseService wxBaseService;

    @Override
    public void addGroupFriend(List<WxGroupFriend> groupFriendList, WxGroup wxGroup, JSONArray JSONFriendArray, int i) throws UnsupportedEncodingException {
        JSONObject JSONGroupMember = wxBaseService.getGroupMember(wxGroup.getRobotId(), wxGroup.getGroupId(), JSONFriendArray.getJSONObject(i).getString("wxid")).getJSONObject("data");
        WxGroupFriend wxJSONGroupFriend = new WxGroupFriend();
        wxJSONGroupFriend.setGroupId(wxGroup.getGroupId());
        wxJSONGroupFriend.setNickname(JSONGroupMember.getString("nickname"));
        wxJSONGroupFriend.setWxId(JSONGroupMember.getString("wxid"));
        wxJSONGroupFriend.setCity(JSONGroupMember.getString("city"));
        wxJSONGroupFriend.setSex(JSONGroupMember.getInteger("sex"));
        wxJSONGroupFriend.setHeadimgurl(JSONGroupMember.getString("headimgurl"));
        wxJSONGroupFriend.setCreateDate(LocalDateTime.now());
        wxJSONGroupFriend.setUpdateDate(LocalDateTime.now());
        wxJSONGroupFriend.setCreateBy("mgg");
        wxJSONGroupFriend.setUpdateBy("mgg");
        wxJSONGroupFriend.setRobotId(wxGroup.getRobotId());
        groupFriendList.add(wxJSONGroupFriend);
    }
}
