package com.lovelycat.wx.db.entity;

import com.lovelycat.wx.base.entity.BaseEntity;
import lombok.Data;

/**
 * 好友功能列表DO
 *
 * @author Administrator
 */
@Data
public class WxFriendFeatureDo extends BaseEntity {
    private String id;
    private String featureKeyword;
    private String wxId;
    private Boolean useFlag;

}
