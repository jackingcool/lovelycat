package com.lovelycat.wx.db.entity;

import com.lovelycat.wx.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mgg
 * @since 2020-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxGroupFeatureFroupGroupMemberReply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 群功能提醒对照表主键id
     */
    private String id;

    /**
     * 群聊id
     */
    private String groupId;

    /**
     * 功能id
     */
    private String featureId;

    /**
     * 提醒内容
     */
    private String content;

    /**
     * 1进  2退
     */
    private Integer type;


}
