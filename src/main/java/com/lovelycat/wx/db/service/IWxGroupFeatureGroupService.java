package com.lovelycat.wx.db.service;

import com.lovelycat.wx.db.entity.WxGroupFeatureGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mgg
 * @since 2020-07-31
 */
public interface IWxGroupFeatureGroupService extends IService<WxGroupFeatureGroup> {

    List<WxGroupFeatureGroup> getRegardsList();
}
