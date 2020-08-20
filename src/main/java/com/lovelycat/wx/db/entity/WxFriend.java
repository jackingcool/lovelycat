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
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxFriend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 机器人外键id
     */
    private String robotId;

    /**
     * 好友微信id
     */

    private String wxId;

    /**
     * 好友昵称
     */
    private String nickname;

    /**
     * 是否使用
     */
    private Boolean useFlag;

}
