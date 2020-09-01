package com.lovelycat.wx.db.service.impl;

import com.lovelycat.wx.db.entity.WxGroupFeatureGroup;
import com.lovelycat.wx.db.mapper.WxGroupFeatureGroupMapper;
import com.lovelycat.wx.db.service.IWxGroupFeatureGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mgg
 * @since 2020-07-31
 */
@Service("IWxGroupFeatureGroupService")
public class WxGroupFeatureGroupServiceImpl extends ServiceImpl<WxGroupFeatureGroupMapper, WxGroupFeatureGroup> implements IWxGroupFeatureGroupService {

    @Autowired
    WxGroupFeatureGroupMapper wxGroupFeatureGroupMapper;


    @Override
    public List<WxGroupFeatureGroup> getRegardsList() {
        return wxGroupFeatureGroupMapper.getRegardsList();
    }
}
