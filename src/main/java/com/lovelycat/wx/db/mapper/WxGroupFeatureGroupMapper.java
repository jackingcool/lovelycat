package com.lovelycat.wx.db.mapper;

import com.lovelycat.wx.db.entity.WxGroupFeatureGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mgg
 * @since 2020-07-31
 */
public interface WxGroupFeatureGroupMapper extends BaseMapper<WxGroupFeatureGroup> {

    /**
     *
     * @return
     *
     */
    List<WxGroupFeatureGroup> getRegardsList();
}
