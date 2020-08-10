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
public class WxFriendFeature extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 功能表主键
     */

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 功能名称

     */
    private String featureName;

    /**
     * 关键字
     */
    private String featureKeyword;


}
