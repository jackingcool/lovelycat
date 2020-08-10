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
public class WxRobot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 机器人主键
     */
    @TableId(value = "wx_id", type = IdType.INPUT)
    private String wxId;

    /**
     * 机器人昵称
     */
    private String nickname;

    /**
     * 微信号
     */
    private String wxNum;

    /**
     * 头像
     */
    private String headimgurl;

    /**
     * 国家
     */
    private String country;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 0：男 1：女
     */
    private Integer sex;

    /**
     * 现场
     */
    private Integer scene;

    /**
     * 备注
     */
    private String signature;

    /**
     * 背景图片
     */
    private String backgroundimgurl;

    /**
     * ？？？
     */
    private String wxWindHandle;

    /**
     * pid
     */
    private String wxPid;

    /**
     * 1：使用
     */
    private Integer status;


}
