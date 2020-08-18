package com.lovelycat.wx.db.mapper;

import com.lovelycat.wx.db.entity.WxFriendFeature;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovelycat.wx.db.entity.WxFriendFeatureDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mgg
 * @since 2020-07-31
 */
public interface WxFriendFeatureMapper extends BaseMapper<WxFriendFeature> {
    /**
     * 查询好友功能列表信息
     * @param fromWxId
     * @param robotWxId
     * @return
     */
    List<WxFriendFeatureDo> selectFriendFeatureList(@Param("fromWxId") String fromWxId, @Param("robotWxId") String robotWxId);
}
