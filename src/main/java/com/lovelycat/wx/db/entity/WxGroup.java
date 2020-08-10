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
 * @since 2020-07-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 群聊id
     */
    private String groupId;

    /**
     * 机器人外键id
     */
    private String robotId;

    /**
     * 群昵称
     */
    private String nickname;

    /**
     * true 正在使用 false 停用
     */
    private Boolean useFlag;


}
