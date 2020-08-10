package com.lovelycat.wx.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lovelycat.wx.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author mgg
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxGroupFriend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 群聊好友表主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 微信id
     */
    private String wxId;

    /**
     * 群聊id
     */
    private String groupId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 1男2女
     */
    private Integer sex;

    /**
     * 城市 不存在为空
     */
    private String city;

    /**
     * 头像
     */
    private String headimgurl;

    /**
     * 机器人id
     */

    private String robotId;

}
