package com.liqihao.commons;

public interface ConstantValue {
    /**
     * 包头
     */
    int FLAG=6617329;
    /**
     * 背包每个格子最大存储数量
     */
    int BAG_MAX_VALUE=99;
    /**
     * 心跳包
     */
    int HEART_BEAT=8888;
    /**
     * 场景模块
     */
    String SCENE_MODULE="sceneServiceImpl";
    /**
     *请求可以前往的场景
     */
    int ASK_CAN_REQUEST=1000;
    /**
     *请求当前场景的角色
     */
    int FIND_ALL_ROLES_REQUEST=1001;
    /**
     *请求前往下一个场景
     */
    int WENT_REQUEST=1002;
    /**
     *与npc对话
     */
    int TALK_NPC_REQUEST=1003;
    /**
     * 复活响应
     */
    int RESTART_RESPONSE=2506;
    /**
     *请求可以前往的场景的响应
     */
    int ASK_CAN_RESPONSE=1500;
    /**
     *请求可以前往的场景的响应
     */
    int FIND_ALL_ROLES_RESPONSE=1501;
    /**
     *请求前往下一个场景的响应
     */
    int WENT_RESPONSE=1502;
    /**
     *与npc对话响应
     */
    int TALK_NPC_RESPONSE=1503;
    /**
     * 场景出现新角色响应
     */
    int ROLE_RESPONSE=1504;
    /**
     *   玩家模块
     */
    String PLAY_MODULE="playServiceImpl";
    /**
     * 登陆请求
     */
    int LOGIN_REQUEST=2000;
    /**
     * 注册请求
     */
    int REGISTER_REQUEST=2001;
    /**
     * 退出登陆请求
     */
    int LOGOUT_REQUEST=2002;
    /**
     * 使用技能请求
     */
    int USE_SKILL_REQUEST =2003;
    //response
    //登陆响应
    /**
     * 使用技能请求
     */
    int LOGIN_RESPONSE=2500;
    /**
     * 注册响应
     */
    int REGISTER_RESPONSE=2501;
    /**
     * 退出登陆响应
     */
    int LOGOUT_RESPONSE=2502;
    /**
     * 使用技能响应
     */
    int USE_SKILL_RSPONSE =2503;
    /**
     * 伤害响应
     */
    int DAMAGES_NOTICE_RESPONSE=2504;
    /**
     * 升级响应
     */
    int UP_LEVEL_RESPONSE =2505;
    /**
     * 游戏系统模块
     */
    String GAME_SYSTEM_MODULE="gameSystemServiceImpl";
    /**
     * 客户端超时请求
     */
    int NET_IO_OUTTIME=3000;
    /**
     * 客户端超市响应
     */
    int OUT_RIME_RESPONSE=3500;
    /**
     * 背包模块
     */
    String BAKCPACK_MODULE="backpackServiceImpl";
    /**
     * 整理背包
     */
    int SORT_BACKPACK_REQUEST=4009;
    /**
     * 查看有多少钱
     */
    int CHECK_MONEY_NUMBER_REQUEST=4006;
    /**
     * 商品列表请求
     */
    int FIND_ALL_GOODS_REQUEST=4008;
    /**
     * 商品列表响应
     */
    int FIND_ALL_GOODS_RESPONSE=4508;
    /**
     * 购买商品 需id以及数量
     */
    int BUY_GOODS_REQUEST=4007;
    /**
     * 查看有多少钱
     */
    int CHECK_MONEY_NUMBER_RESPONSE=4506;
    /**
     * 购买商品 需id以及数量
     */
    int BUY_GOODS_RESPONSE=4507;

    /**
     * 背包信息请求
     */
    int BACKPACK_MSG_REQUEST=4000;
    /**
     * 使用物品请求
     */
    int USE_REQUEST=4001;
    /**
     * 丢弃物品请求
     */
    int ABANDON_REQUEST=4002;
    /**
     * 放入物品请求
     */
    int ADD_ARTICLE_REQUEST=4003;

    /**
     * 查看副本地面可拾取物品
     */
    int FIND_ALL_CAN_REQUEST=4004;
    /**
     * 拾取地面物品
     */
    int GET_ARTICLE_FROM_FLOOR_REQUEST=4005;

    /**
     * 查看副本地面可拾取物品
     */
    int FIND_ALL_CAN_RESPONSE=4504;
    /**
     * 拾取地面物品
     */
    int GET_ARTICLE_FROM_FLOOR_RESPONSE=4505;

    /**
     * 背包信息响应
     */
    int BACKPACK_MSG_RESPONSE=4500;
    /**
     * 使用物品响应
     */
    int USE_RESPONSE=4501;
    /**
     * 丢弃物品响应
     */
    int ABANDON_RESPONSE=4502;
    /**
     * 放入物品响应
     */
    int ADD_ARTICLE_RESPONSE=4503;
    /**
     * 装备模块
     */
    String EQUIPMENT_MODULE="equipmentServiceImpl";
    /**
     * 穿装备请求
     */
    int ADD_EQUIPMENT_REQUEST=5000;
    /**
     * 装备栏信息请求
     */
    int EQUIPMENT_MSG_REQUEST=5001;
    /**
     * 脱装备请求
     */
    int REDUCE_EQUIPMENT_REQUEST=5002;
    /**
     * 修复装备请求
     */
    int FIX_EQUIPMENT_REQUEST=5003;
    /**
     * 穿装备响应
     */
    int ADD_EQUIPMENT_RESPONSE=5500;
    /**
     * 装备栏信息响应
     */
    int EQUIPMENT_MSG_RESPONSE=5501;
    /**
     * 脱装备响应
     */
    int REDUCE_EQUIPMENT_RESPONSE=5502;
    /**
     * 修复装备响应
     */
    int FIX_EQUIPMENT_RESPONSE=5503;
    /**
     * 组队模块
     */
    String TEAM_MODULE="teamServiceImpl";
    /**
     * 创建队伍请求
     */
    int CREATE_TEAM_REQUEST=6000;
    /**
     * 队伍信息请求
     */
    int TEAM_MESSAGE_REQUEST=6001;
    /**
     * 申请入队请求
     */
    int APPLY_FOR_TEAM_REQUEST=6002;
    /**
     * 邀请玩家入队请求
     */
    int INVITE_PEOPLE_REQUEST=6003;
    /**
     * 申请信息列表请求
     */
    int APPLY_MESSAGE_REQUEST=6004;
    /**
     * 邀请信息列表请求
     */
    int INVITE_MESSAGE_REQUEST=6005;
    /**
     * 拒绝申请请求
     */
    int REFUSE_APPLY_REQUEST=6006;
    /**
     * 拒绝邀请请求
     */
    int REFUSE_INVITE_REQUEST=6007;
    /**
     * 玩家入队请求 接受申请or接受邀请
     */
    int ENTRY_PEOPLE_REQUEST=6008;
    int ENTRY_PEOPLE_REQUEST_INVITE=60088;
    int ENTRY_PEOPLE_REQUEST_APPLY=60089;
    /**
     * 离开队伍请求
     */
    int EXIT_TEAM_REQUEST=6009;
    /**
     * 踢除玩家请求
     */
    int BAN_PEOPLE_REQUEST=6010;
    /**
     * 解散队伍请求
     */
    int DELETE_TEAM_REQUEST=6011;
    /**
     * 踢除玩家响应
     */
    int BAN_PEOPLE_RESPONSE=6511;
    /**
     * 解散队伍响应
     */
    int DELETE_TEAM_RESPONSE=6512;
    /**
     * 成为队长响应
     */
    int LEADER_TEAM_RESPONSE=6510;
    /**
     * 进入队伍响应
     */
    int ENTRY_PEOPLE_RESPONSE=6508;
    /**
     * 离开队伍响应
     */
    int EXIT_TEAM_RESPONSE=6509;
    /**
     * 拒绝申请响应
     */
    int REFUSE_APPLY_RESPONSE=6506;
    /**
     * 拒绝邀请响应
     */
    int REFUSE_INVITE_RESPONSE=6507;
    /**
     * 队伍信息响应
     */
    int TEAM_MESSAGE_RESPONSE=6500;
    /**
     * 申请入队响应
     */
    int APPLY_FOR_TEAM_RESPONSE=6502;

    /**
     * 申请入队响应
     */
    int INVITE_PEOPLE_RESPONSE=6503;
    /**
     * 申请入队列表响应
     */
    int APPLY_MESSAGE_RESPONSE=6004;
    /**
     * 邀请入队列表响应
     */
    int INVITE_MESSAGE_RESPONSE=6005;

    /**
     * 副本模块
     */
    String COPY_MODULE="copySceneServiceImpl";
    /**
     * 那些副本可以挑战
     */
    int ASK_CAN_COPY_SCENE_REQUEST=7000;
    /**
     * 副本信息请求
     */
    int COPY_SCENE_MESSAGE_REQUEST =7001;
    /**
     * 创建副本请求
     */
    int CREATE_COPY_SCENE_REQUEST =7002;
    /**
     * 进入副本请求
     */
    int ENTER_COPY_SCENE_REQUEST =7003;
    /**
     * 离开副本请求
     */
    int EXIT_COPY_SCENE_REQUEST =7004;
    /**
     * 挑战成功响应
     */
    int CHANGE_FAIL_RESPONSE=7506;
    /**
     * 挑战失败响应
     */
    int CHANGE_SUCCESS_RESPONSE=7507;

    /**
     * 副本信息响应
     */
    int COPY_SCENE_MESSAGE_RESPONSE =7501;
    /**
     * 创建副本响应
     */
    int CREATE_COPY_SCENE_RESPONSE =7502;
    /**
     * 进入副本响应
     */
    int ENTER_COPY_SCENE_RESPONSE =7503;
    /**
     * 离开副本响应
     */
    int EXIT_COPY_SCENE_RESPONSE =7504;
    /**
     * 副本解散响应
     */
    int COPY_SCENE_FINISH_RESPONSE =7505;

    /**
     * 聊天模块
     */
    String CHAT_MODULE = "chatServiceImpl";
    /**
     * 全服频道信息请求
     */
    int SEND_TO_ALL_REQUEST=8000;
    /**
     * 私聊频道信息请求
     */
    int SEND_TO_ONE_REQUEST=8001;
    /**
     * 接受信息响应
     */
    int ACCEPT_MESSAGE_RESPONSE=8500;
    /**
     * 队伍频道信息请求
     */
    int SEND_TO_TEAM_REQUEST=8002;
    /**
     * 场景or副本信息请求
     */
    int SEND_TO_SCENE_REQUEST=8003;

    /**
     * 邮箱模块
     */
    String EMAIL_MODULE = "emailServiceImpl";
    /**
     * 邮件详情请求
     */
    int GET_EMAIL_MESSAGE_REQUEST=9000;
    /**
     * 获取邮件物品请求
     */
    int GET_EMAIL_ARTICLE_REQUEST=9001;
    /**
     * 已接收邮件列表请求
     */
    int ACCEPT_EMAIL_LIST_REQUEST=9002;
    /**
     * 已发送的邮件列表请求
     */
    int IS_SEND_EMAIL_LIST_REQUEST=9003;
    /**
     * 发送邮件请求
     */
    int SEND_EMAIL_REQUEST=9004;
    /**
     * 删除已接收邮件请求
     */
    int DELETE_ACCEPT_EMAIL_REQUEST=9005;
    /**
     * 删除已发送邮件请求
     */
    int DELETE_SEND_EMAIL_REQUEST=9006;
    /**
     * 获取邮件金币请求
     */
    int GET_EMAIL_MONEY_RESPONSE=9507;
    /**
     * 获取邮件金币请求
     */
    int GET_EMAIL_MONEY_REQUEST=9007;


    /**
     * 邮件详情响应
     */
    int GET_EMAIL_MESSAGE_RESPONSE=9500;
    /**
     * 获取邮件物品响应
     */
    int GET_EMAIL_ARTICLE_RESPONSE=9501;
    /**
     * 已接收邮件列表响应
     */
    int ACCEPT_EMAIL_LIST_RESPONSE=9502;
    /**
     * 已发送的邮件列表响应
     */
    int IS_SEND_EMAIL_LIST_RESPONSE=9503;
    /**
     * 发送邮件响应
     */
    int SEND_EMAIL_RESPONSE=9504;
    /**
     * 删除已接收邮件响应
     */
    int DELETE_ACCEPT_EMAIL_RESPONSE=9505;
    /**
     * 删除已发送邮件响应
     */
    int DELETE_SEND_EMAIL_RESPONSE=9506;

    /**
     * 公会模块
     */
    String GUILD_MODULE = "guildServiceImpl";

    int CREATE_GUILD_REQUEST=10000;
    int JOIN_GUILD_REQUEST=10001;
    int SET_GUILD_POSITION_REQUEST=10002;
    int OUT_GUILD_REQUEST=10003;
    int CONTRIBUTE_MONEY_REQUEST=10004;
    int CONTRIBUTE_ARTICLE_REQUEST=10005;
    int GET_GUILD_MONEY_REQUEST=10006;
    int GET_GUILD_ARTICLE_REQUEST=10007;
    int GET_GUILD_APPLY_LIST_REQUEST=10008;
    int AGREE_GUILD_APPLY_REQUEST=10009;
    int REFUSE_GUILD_APPLY_REQUEST=10010;
    int GET_GUILD_MESSAGE_REQUEST=10011;
    int GET_GUILD_WAREHOUSE_REQUEST=10013;

    int CREATE_GUILD_RESPONSE=10500;
    int JOIN_GUILD_RESPONSE=10501;
    int SET_GUILD_POSITION_RESPONSE=10502;
    int OUT_GUILD_RESPONSE=10503;
    int CONTRIBUTE_MONEY_RESPONSE=10504;
    int CONTRIBUTE_ARTICLE_RESPONSE=10505;
    int GET_GUILD_MONEY_RESPONSE=10506;
    int GET_GUILD_ARTICLE_RESPONSE=10507;
    int GET_GUILD_APPLY_LIST_RESPONSE=10508;
    int AGREE_GUILD_APPLY_RESPONSE=10509;
    int REFUSE_GUILD_APPLY_RESPONSE=10510;
    int GET_GUILD_MESSAGE_RESPONSE=15011;
    int GUILD_APPLY_RESPONSE=15012;
    int GET_GUILD_WAREHOUSE_RESPONSE=15013;

    /**
     * 交易模块
     */
    String DEAL_MODULE = "dealServiceImpl";
    int ASK_DEAL_REQUEST=11000;
    int AGREE_DEAL_REQUEST=11001;
    int REFUSE_DEAL_REQUEST=11002;
    int CONFIRM_DEAL_REQUEST=11003;
    int CANCEL_DEAL_REQUEST=11004;
    int GET_DEAL_MESSAGE_REQUEST=11005;
    int SET_DEAL_MONEY_REQUEST=11006;
    int ADD_DEAL_ARTICLE_REQUEST=11007;
    int ABANDON_DEAL_ARTICLE_REQUEST=11008;

    int ASK_DEAL_RESPONSE=11500;
    int AGREE_DEAL_RESPONSE=11501;
    int REFUSE_DEAL_RESPONSE=11502;
    int CONFIRM_DEAL_RESPONSE=11503;
    int CANCEL_DEAL_RESPONSE=11504;
    int GET_DEAL_MESSAGE_RESPONSE=11505;
    int SET_DEAL_MONEY_RESPONSE=11506;
    int ADD_DEAL_ARTICLE_RESPONSE=11507;
    int ABANDON_DEAL_ARTICLE_RESPONSE=11508;
    int DEAL_SUCCESS_RESPONSE=11509;
    /**
     * 交易行模块
     */
    String DEAL_BANK_MODULE = "dealBankServiceImpl";
    int ADD_SELL_ARTICLE_REQUEST=12000;
    int REDUCE_SELL_ARTICLE_REQUEST=12002;
    int REDUCE_AUCTION_ARTICLE_REQUEST=12003;
    int BUY_ARTICLE_REQUEST=12004;
    int AUCTION_ARTICLE_REQUEST=12005;
    int GET_ARTICLE_REQUEST=12006;

    int ADD_SELL_ARTICLE_RESPONSE=12500;
    int REDUCE_SELL_ARTICLE_RESPONSE=12502;
    int REDUCE_AUCTION_ARTICLE_RESPONSE=12503;
    int BUY_ARTICLE_RESPONSE=12504;
    int AUCTION_ARTICLE_RESPONSE=12505;
    int GET_ARTICLE_RESPONSE=12506;

    /**
     * 任务系统
     */
    String TASK_MODULE = "taskServiceImpl";
    int GET_PEOPLE_TASK_REQUEST=13000;
    int GET_CAN_ACCEPT_TASK_REQUEST=13001;
    int ACCEPT_TASK_REQUEST=13002;
    int ABANDON_TASK_REQUEST=13003;
    int FINISH_TASK_REQUEST=13004;

    int GET_PEOPLE_TASK_RESPONSE=13500;
    int GET_CAN_ACCEPT_TASK_RESPONSE=13501;
    int ACCEPT_TASK_RESPONSE=13502;
    int ABANDON_TASK_RESPONSE=13503;
    int FINISH_TASK_RESPONSE=13504;
    /**
     * 好友模块
     */
    String FRIEND_MODULE = "friendServiceImpl";
    int APPLY_FRIEND_REQUEST=14000;
    int AGREE_FRIEND_REQUEST=14001;
    int REFUSE_FRIEND_REQUEST=14002;
    int GET_FRIENDS_REQUEST=14003;
    int FRIEND_APPLY_LIST_REQUEST=14004;
    int APPLY_FRIEND_RESPONSE =14500;
    int AGREE_FRIEND_RESPONSE =14501;
    int REFUSE_FRIEND_RESPONSE=14502;
    int HAS_NEW_FRIENDS_RESPONSE=14503;
    int BE_REFUSE_RESPONSE =14505;
    int GET_FRIENDS_RESPONSE=14506;
    int FRIEND_APPLY_LIST_RESPONSE=14504;
    int REDUCE_FRIEND_REQUEST=14007;
    int REDUCE_FRIEND_RESPONSE=14507;
    /**
     * 参数错误响应
     */
    int FAIL_RESPONSE=9999;


    /**
     * 指令
     */
    /**
     * 好友模块
     */
    /** 删除好友*/
    String REDUCE_FRIEND_REQUEST_CMD="reduceFriend";
    /** 申请好友*/
    String APPLY_FRIEND_REQUEST_CMD="applyFriend";
    /** 通过好友申请*/
    String AGREE_FRIEND_REQUEST_CMD="agreeFriendApply";
    /** 拒绝好友申请*/
    String REFUSE_FRIEND_REQUEST_CMD="refuseFriendApply";
    /** 好友列表*/
    String GET_FRIENDS_REQUEST_CMD="friendList";
    /** 好友申请列表*/
    String FRIEND_APPLY_LIST_REQUEST_CMD="friendApplyList";

    /**
     * 任务模块
     */
    /** 完成任务*/
    String FINISH_TASK_REQUEST_CMD="finishTask";
    /** 任务列表*/
    String GET_PEOPLE_TASK_REQUEST_CMD="tasks";
    /** 可接受任务列表*/
    String GET_CAN_ACCEPT_TASK_REQUEST_CMD="getCanAcceptTask";
    /** 接收任务*/
    String ACCEPT_TASK_REQUEST_CMD="acceptTask";
    /** 放弃任务*/
    String ABANDON_TASK_REQUEST_CMD="abandonTask";

    /**
     * 交易行模块
     */
    /** 上架商品*/
    String ADD_SELL_ARTICLE_REQUEST_CMD="addDealBankArticle";
    /** 下架商品列表*/
    String REDUCE_SELL_ARTICLE_REQUEST_CMD="reduceSellArticle";
    /** 购买一口价商品*/
    String BUY_ARTICLE_REQUEST_CMD="buyArticle";
    /** 竞拍商品*/
    String AUCTION_ARTICLE_REQUEST_CMD="auctionArticle";
    /** 拍卖行信息列表*/
    String GET_ARTICLE_REQUEST_CMD="dealBankMsg";

    /**
     * 交易模块
     */
    /** 发送交易申请*/
    String ASK_DEAL_REQUEST_CMD="askDeal";
    /** 同意交易*/
    String AGREE_DEAL_REQUEST_CMD="agreeDeal";
    /** 拒绝交易*/
    String REFUSE_DEAL_REQUEST_CMD="refuseDeal";
    /** 确认交易*/
    String CONFIRM_DEAL_REQUEST_CMD="confirmDeal";
    /** 取消交易*/
    String CANCEL_DEAL_REQUEST_CMD="cancelDeal";
    /** 交易栏信息*/
    String GET_DEAL_MESSAGE_REQUEST_CMD="dealMessage";
    /** 交易栏修改放入金币*/
    String SET_DEAL_MONEY_REQUEST_CMD="setDealMoney";
    /** 交易栏放入物品*/
    String ADD_DEAL_ARTICLE_REQUEST_CMD="addDealArticle";
    /** 拿出交易栏物品*/
    String ABANDON_DEAL_ARTICLE_REQUEST_CMD="abandonDealArticle";

    /**
     * 公会模块
     */
    /** 公会仓库捐赠金币*/
    String CONTRIBUTE_MONEY_REQUEST_CMD="contributeMoney";
    /** 公会仓库捐赠物品*/
    String CONTRIBUTE_ARTICLE_REQUEST_CMD="contributeArticle";
    /** 拿出公会仓库金币*/
    String GET_GUILD_MONEY_REQUEST_CMD="getGuildMoney";
    /** 拿出公会仓库物品*/
    String GET_GUILD_ARTICLE_REQUEST_CMD="getGuildArticle";
    /** 公会仓库信息*/
    String GET_GUILD_WAREHOUSE_REQUEST_CMD="guildWareHouseMsg";
    /** 创建公会*/
    String CREATE_GUILD_REQUEST_CMD="createGuild";
    /** 加入公会*/
    String JOIN_GUILD_REQUEST_CMD="joinGuild";
    /** 设置公会成员职业*/
    String SET_GUILD_POSITION_REQUEST_CMD="setGuildPosition";
    /** 离开公会*/
    String OUT_GUILD_REQUEST_CMD="outGuild";
    /** 同意公会申请*/
    String AGREE_GUILD_APPLY_REQUEST_CMD="agreeGuildApply";
    /** 拒绝公会申请*/
    String REFUSE_GUILD_APPLY_REQUEST_CMD="refuseGuildApply";
    /** 公会信息*/
    String GET_GUILD_MESSAGE_REQUEST_CMD="guildMsg";
    /** 公会申请列表*/
    String GET_GUILD_APPLY_LIST_REQUEST_CMD="getGuildApplyList";

    /**
     * 商店模块
     */
    /** 商店商品*/
    String FIND_ALL_GOODS_REQUEST_CMD="shopGoods";
    /** 人物金币*/
    String CHECK_MONEY_NUMBER_REQUEST_CMD="moneyMsg";
    /** 购买商品*/
    String BUY_GOODS_REQUEST_CMD="buyGoods";

    /**
     * 邮件模块
     */
    /** 获取邮件金币*/
    String GET_EMAIL_MONEY_REQUEST_CMD="getEmailMoney";
    /** 邮件信息*/
    String GET_EMAIL_MESSAGE_REQUEST_CMD="emailMsg";
    /** 获取邮件内物品*/
    String GET_EMAIL_ARTICLE_REQUEST_CMD="getEmailArticle";
    /** 接收邮件列表*/
    String ACCEPT_EMAIL_LIST_REQUEST_CMD="acceptEmailList";
    /** 已发送邮件列表*/
    String IS_SEND_EMAIL_LIST_REQUEST_CMD="isSendEmailList";
    /** 发送邮件*/
    String SEND_EMAIL_REQUEST_CMD="sendEmail";
    /** 删除接收邮件*/
    String DELETE_ACCEPT_EMAIL_REQUEST_CMD="deleteAcceptEmail";
    /** 删除已发送邮件*/
    String DELETE_SEND_EMAIL_REQUEST_CMD="deleteSendEmail";

    /**
     * 队伍模块
     */
    /** 发送队伍信息*/
    String SEND_TO_TEAM_REQUEST_CMD="sendMsgTeam";
    /** 发送场景信息*/
    String SEND_TO_SCENE_REQUEST_CMD="sendMsgScene";
    /** 发送全服信息*/
    String SEND_TO_ALL_REQUEST_CMD="sendMsgAll";
    /** 私聊*/
    String SEND_TO_ONE_REQUEST_CMD="sendMsgOne";

    /**
     * 副本模块
     */
    /** 查看可进入副本*/
    String ASK_CAN_COPY_SCENE_REQUEST_CMD ="askCanCopyScene";
    /** 当前副本信息*/
    String COPY_SCENE_MESSAGE_REQUEST_CMD ="copySceneMsg";
    /** 创建副本*/
    String CREATE_COPY_SCENE_REQUEST_CMD ="createCopyScene";
    /** 进入副本*/
    String ENTER_COPY_SCENE_REQUEST_CMD ="enterCopyScene";
    /** 离开副本*/
    String EXIT_COPY_SCENE_REQUEST_CMD ="exitCopyScene";
    /** 查看副本地面可拾取物品*/
    String FIND_ALL_CAN_REQUEST_CMD="findArticleFromFloor";
    /** 拾取地面物品*/
    String GET_ARTICLE_FROM_FLOOR_REQUEST_CMD="getArticleFromFloor";

    /**
     * 队伍模块
     */
    /** 将人踢除队伍*/
    String BAN_PEOPLE_REQUEST_CMD="banPeople";
    /** 解散队伍*/
    String DELETE_TEAM_REQUEST_CMD="deleteTeam";
    /** 同意队伍邀请*/
    String ENTRY_INVITE_PEOPLE_REQUEST_CMD="agreeTeamInvite";
    /** 同意队伍申请*/
    String ENTRY_APPLY_PEOPLE_REQUEST_CMD="agreeTeamApply";
    /** 离开队伍*/
    String EXIT_TEAM_REQUEST_CMD="exitTeam";
    /** 拒绝申请*/
    String REFUSE_APPLY_REQUEST_CMD="refuseApply";
    /** 拒绝邀请*/
    String REFUSE_INVITE_REQUEST_CMD="refuseInvite";
    /** 申请入队*/
    String APPLY_FOR_TEAM_REQUEST_CMD="applyTeam";
    /** 邀请玩家入队*/
    String INVITE_PEOPLE_REQUEST_CMD="invitePeople";
    /** 队伍申请列表*/
    String APPLY_MESSAGE_REQUEST_CMD="applyMsg";
    /** 队伍邀请列表*/
    String INVITE_MESSAGE_REQUEST_CMD="inviteMsg";
    /** 创建队伍*/
    String CREATE_TEAM_REQUEST_CMD="createTeam";
    /** 队伍信息*/
    String TEAM_MESSAGE_REQUEST_CMD="teamMsg";

    /**
     * 场景模块
     */
    /** 查看可进入场景*/
    String ASK_CAN_REQUEST_CMD="askCan";
    /** 查找当前场景角色*/
    String FIND_ALL_ROLES_REQUEST_CMD="findAllRoles";
    /** 前往场景*/
    String WENT_REQUEST_CMD="move";

    /**
     * 角色模块
     */
    /** 登陆*/
    String LOGIN_REQUEST_CMD="login";
    /** 注册*/
    String REGISTER_REQUEST_CMD="register";
    /** 退出登陆*/
    String LOGOUT_REQUEST_CMD="logout";
    /** 使用技能*/
    String USE_SKILL_REQUEST_CMD="skill";
    /** 与npc聊天*/
    String TALK_NPC_REQUEST_CMD="talk";

    /**
     * 背包模块
     */

    /** 整理背包*/
    String SORT_BACKPACK_REQUEST_CMD="sortBag";
    /** 背包信息*/
    String BACKPACK_MSG_REQUEST_CMD="bag";
    /** 使用物品*/
    String USE_REQUEST_CMD="useArticle";
    /** 丢弃物品*/
    String ABANDON_REQUEST_CMD="abandonArticle";
    /** 增加物品*/
    String ADD_ARTICLE_REQUEST_CMD="addArticle";
    /** 修复装备*/
    String FIX_EQUIPMENT_REQUEST_CMD="fixEquipment";
    /** 穿装备*/
    String ADD_EQUIPMENT_REQUEST_CMD="addEquipment";
    /** 装备信息*/
    String EQUIPMENT_MSG_REQUEST_CMD="equipmentMsg";
    /** 脱装备*/
    String REDUCE_EQUIPMENT_REQUEST_CMD="reduceEquipment";
}
