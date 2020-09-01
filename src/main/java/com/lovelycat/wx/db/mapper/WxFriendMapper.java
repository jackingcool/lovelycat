package com.lovelycat.wx.db.mapper;

import com.lovelycat.wx.db.entity.WxFriend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mgg
 * @since 2020-07-16
 */

@Mapper
public interface WxFriendMapper extends BaseMapper<WxFriend> {

    List<WxFriend> selectFriendWeatherReply();
}
