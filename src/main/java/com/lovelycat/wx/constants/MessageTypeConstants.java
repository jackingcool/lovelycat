package com.lovelycat.wx.constants;

/**
 * 消息类型常量
 *
 * @author mgg
 */
public class MessageTypeConstants {
    /**
     * 100                       私聊消息
     * <p>
     * 200                       群聊消息
     * <p>
     * 300                       暂无
     * <p>
     * 400                       群成员增加
     * <p>
     * 410                       群成员减少
     * <p>
     * 500                       收到好友请求
     * <p>
     * 600                       二维码收款
     * <p>
     * 700                       收到转账
     * <p>
     * 800                       软件开始启动
     * <p>
     * 900                       新的账号登录完成
     * <p>
     * 910                       账号下线
     */

    public static final int PRIVATE_CHAT_TYPE = 100;
    public static final int GROUP_CHAT_TYPE = 200;
    public static final int NO_USE_TYPE = 300;
    public static final int ADD_GROUP_MEMBER_TYPE = 400;
    public static final int REMOVE_GROUP_MEMBER_TYPE = 410;
    public static final int ADD_FRIEND_REQUEST_TYPE = 500;
    public static final int QR_CODE_COLLECTION_TYPE = 600;
    public static final int RECEIVE_TRANSFER_TYPE = 700;
    public static final int THE_SOFTWARE_STARTS_TYPE = 800;
    public static final int NEW_ACCOUNT_LOGIN_COMPLETE_TYPE = 900;
    public static final int ACCOUNT_LOGIN_OUT_TYPE = 910;

    public static final int MSG_TYPE_GAME_LINK = 49;
}
