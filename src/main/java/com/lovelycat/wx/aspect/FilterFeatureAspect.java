package com.lovelycat.wx.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lovelycat.wx.base.entity.WxMessage;
import com.lovelycat.wx.constants.MessageTypeConstants;
import com.lovelycat.wx.db.entity.WxFriendFeature;
import com.lovelycat.wx.db.entity.WxFriendFeatureFriend;
import com.lovelycat.wx.db.entity.WxGroupFeature;
import com.lovelycat.wx.db.entity.WxGroupFeatureGroup;
import com.lovelycat.wx.db.service.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 匹配功能权限的切面
 *
 * @author mgg
 */
@Component
@Aspect
public class FilterFeatureAspect {

    @Autowired
    private IWxGroupFeatureGroupService iWxGroupFeatureGroupService;
    @Autowired
    private IWxFriendFeatureFriendService iWxFriendFeatureFriendService;
    @Autowired
    private IWxGroupFeatureService iWxGroupFeatureService;
    @Autowired
    private IWxFriendFeatureService iWxFriendFeatureService;

    private static final Log log = LogFactory.get();

    @Pointcut("@annotation(com.lovelycat.wx.annotation.FilterFeature)")
    public void filterFeature() {

    }

    ;

    @Around("filterFeature()")
    public void AroundFilterFeature(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("start aop");
        String methodName = proceedingJoinPoint.getSignature().getName();
        log.info("----{}", methodName);
        Object[] args = proceedingJoinPoint.getArgs();
        if (ArrayUtil.isNotEmpty(args)) {
            WxMessage wxMessage = (WxMessage) args[0];
            String robotWxId = wxMessage.getRobotWxId();
            if (wxMessage.getType() == MessageTypeConstants.GROUP_CHAT_TYPE) {
                //群聊
                List<WxGroupFeatureGroup> wxGroupFeatureGroupList = iWxGroupFeatureGroupService.list(new QueryWrapper<WxGroupFeatureGroup>().eq("feature_id", iWxGroupFeatureService.getOne(new QueryWrapper<WxGroupFeature>().eq("feature_name", methodName)).getId()).eq("use_flag", true).eq("robot_id", robotWxId));
                if (CollectionUtil.isNotEmpty(wxGroupFeatureGroupList)) {
                    for (int i = 0; i < wxGroupFeatureGroupList.size(); i++) {
                        //数据库中群功能flag为true 并且群聊ID 和 message来源信息一致
                        if (wxGroupFeatureGroupList.get(i).getGroupId().equals(wxMessage.getFromWxId())) {
                            proceedingJoinPoint.proceed();
                        }
                    }
                }
            }

            if (wxMessage.getType() == MessageTypeConstants.PRIVATE_CHAT_TYPE) {
                //好友
                List<WxFriendFeatureFriend> wxFriendFeatureFriendList = iWxFriendFeatureFriendService.list(new QueryWrapper<WxFriendFeatureFriend>().eq("feature_id", iWxFriendFeatureService.getOne(new QueryWrapper<WxFriendFeature>().eq("feature_name", methodName)).getId()).eq("use_flag", true).eq("robot_id", robotWxId));
                if (CollectionUtil.isNotEmpty(wxFriendFeatureFriendList)) {
                    for (int i = 0; i < wxFriendFeatureFriendList.size(); i++) {
                        //数据库中群功能flag为true 并且群聊ID 和 message来源信息一致
                        if (wxFriendFeatureFriendList.get(i).getWxId().equals(wxMessage.getFromWxId())) {
                            proceedingJoinPoint.proceed();
                        }
                    }
                }
            }

            if (wxMessage.getType() == MessageTypeConstants.ADD_GROUP_MEMBER_TYPE || wxMessage.getType() == MessageTypeConstants.REMOVE_GROUP_MEMBER_TYPE) {
                proceedingJoinPoint.proceed();
            }
        }

    }

}
