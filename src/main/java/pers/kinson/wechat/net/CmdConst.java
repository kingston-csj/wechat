package pers.kinson.wechat.net;

public interface CmdConst {

    //业务上行数据包

    //----------------------模块号申明------------------------------
    //----------------请求协议id格式为 模块号_000 起--------------------
    //----------------推送协议id格式为 模块号_200 起--------------------
    //------------------基础服务1-----------------------------------
    //------------------http协议2----------------------------------
    //------------------用户3----------------------------------
    //------------------聊天4----------------------------------
    //------------------好友5----------------------------------

    /**
     * 请求--链接心跳包
     */
    int ReqHeartBeat = 1_000;

    /**
     * 推送--新用户注册
     */
    int RespHeartBeat = 1_200;

    /**
     * ·
     * 请求--新用户注册
     */
    int ReqUserRegister = 3_000;
    /**
     * 请求--请求--用户登陆
     */
    int ReqUserLogin = 3_001;

    /**
     * 推送--新用户注册
     */
    int ResUserRegister = 3_200;
    /**
     * 推送--用户登录
     */
    int ResUserLogin = 3_201;
    /**
     * 推送--玩家信息
     */
    int ResUserInfo = 3_202;

    /**
     * 请求--单聊
     */
    int ReqChatToUser = 4_000;
    /**
     * 请求--群聊
     */
    int ReqChatToGroup = 4_001;

    /**
     * 推送--单聊
     */
    int ResChatToUser = 4_200;
    /**
     * 推送--群聊
     */
    int ResChatToGroup = 4_201;

    /**
     * 请求－好友查询
     */
    int ReqSearchFriends = 5_000;
    /**
     * 推送--好友列表
     */
    int ResFriendList = 5_200;
    /**
     * 推送--好友登录
     */
    int ResFriendLogin = 5_201;
    /**
     * 推送--好友下线
     */
    int ResFriendLogout = 5_202;

    int ResSearchFriends = 5_203;

    int FriendVo = 5_204;
    int RecommendFriendVO = 5_205;
}