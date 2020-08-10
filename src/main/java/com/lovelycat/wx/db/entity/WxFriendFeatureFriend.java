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
 * @since 2020-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxFriendFeatureFriend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 好友外键id
     */
    private String wxId;

    /**
     * 功能外键id
     */
    private String featureId;

    /**
     * 使用状态 默认false
     */
    private Boolean useFlag;

    /**
     * 机器人id
     */
    private String robotId;


}
