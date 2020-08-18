package com.lovelycat.wx.db.mapper;

import com.lovelycat.wx.db.entity.WxGroupFeature;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovelycat.wx.db.entity.WxGroupFeatureDo;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mgg
 * @since 2020-07-31
 */
public interface WxGroupFeatureMapper extends BaseMapper<WxGroupFeature> {

    /**
     * 查询群功能信息(带名称)
     *
     * @param fromWxId
     * @param robotWxId
     * @return
     */
    List<WxGroupFeatureDo> selectGroupFeatureList(String fromWxId, String robotWxId);
}
