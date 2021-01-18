package com.liqihao.commons;

public enum  CmdCode {
    //
    ASK_CAN_REQUEST_CMD(ConstantValue.ASK_CAN_REQUEST,ConstantValue.ASK_CAN_REQUEST_CMD),
    FIND_ALL_ROLES_REQUEST_CMD(ConstantValue.FIND_ALL_ROLES_REQUEST,ConstantValue.FIND_ALL_ROLES_REQUEST_CMD),
    WENT_REQUEST_CMD(ConstantValue.WENT_REQUEST,ConstantValue.WENT_REQUEST_CMD),
    LOGIN_REQUEST_CMD(ConstantValue.LOGIN_REQUEST,ConstantValue.LOGIN_REQUEST_CMD),
    REGISTER_REQUEST_CMD(ConstantValue.REGISTER_REQUEST,ConstantValue.REGISTER_REQUEST_CMD),
    LOGOUT_REQUEST_CMD(ConstantValue.LOGOUT_REQUEST,ConstantValue.LOGOUT_REQUEST_CMD),
    USE_SKILL_REQUEST_CMD(ConstantValue.USE_SKILL_REQUEST,ConstantValue.USE_SKILL_REQUEST_CMD),
    TALK_NPC_CMD(ConstantValue.TALK_NPC_REQUEST,ConstantValue.TALK_NPC_REQUEST_CMD),
    BACKPACK_MSG_REQUEST_CMD(ConstantValue.BACKPACK_MSG_REQUEST,ConstantValue.BACKPACK_MSG_REQUEST_CMD),
    USE_REQUEST_CMD(ConstantValue.USE_REQUEST,ConstantValue.USE_REQUEST_CMD),
    ADD_ARTICLE_REQUEST_CMD(ConstantValue.ADD_ARTICLE_REQUEST,ConstantValue.ADD_ARTICLE_REQUEST_CMD),
    ADD_EQUIPMENT_REQUEST_CMD(ConstantValue.ADD_EQUIPMENT_REQUEST,ConstantValue.ADD_EQUIPMENT_REQUEST_CMD),
    REDUCE_EQUIPMENT_REQUEST_CMD(ConstantValue.REDUCE_EQUIPMENT_REQUEST,ConstantValue.REDUCE_EQUIPMENT_REQUEST_CMD),
    EQUIPMENT_MSG_REQUEST_CMD(ConstantValue.EQUIPMENT_MSG_REQUEST,ConstantValue.EQUIPMENT_MSG_REQUEST_CMD),
    FIX_EQUIPMENT_REQUEST_CMD(ConstantValue.FIX_EQUIPMENT_REQUEST,ConstantValue.FIX_EQUIPMENT_REQUEST_CMD),
    CREATE_TEAM_REQUEST_CMD(ConstantValue.CREATE_TEAM_REQUEST,ConstantValue.CREATE_TEAM_REQUEST_CMD),
    TEAM_MESSAGE_REQUEST_CMD(ConstantValue.TEAM_MESSAGE_REQUEST,ConstantValue.TEAM_MESSAGE_REQUEST_CMD),
    APPLY_FOR_TEAM_REQUEST_CMD(ConstantValue.APPLY_FOR_TEAM_REQUEST,ConstantValue.APPLY_FOR_TEAM_REQUEST_CMD),
    INVITE_PEOPLE_REQUEST_CMD(ConstantValue.INVITE_PEOPLE_REQUEST,ConstantValue.INVITE_PEOPLE_REQUEST_CMD),
    APPLY_MESSAGE_REQUEST_CMD(ConstantValue.APPLY_MESSAGE_REQUEST,ConstantValue.APPLY_MESSAGE_REQUEST_CMD),
    REFUSE_APPLY_REQUEST_CMD(ConstantValue.REFUSE_APPLY_REQUEST,ConstantValue.REFUSE_APPLY_REQUEST_CMD),
    REFUSE_INVITE_REQUEST_CMD(ConstantValue.REFUSE_INVITE_REQUEST,ConstantValue.REFUSE_INVITE_REQUEST_CMD),
    INVITE_MESSAGE_REQUEST_CMD(ConstantValue.INVITE_MESSAGE_REQUEST,ConstantValue.INVITE_MESSAGE_REQUEST_CMD),
    BAN_PEOPLE_REQUEST_CMD(ConstantValue.BAN_PEOPLE_REQUEST,ConstantValue.BAN_PEOPLE_REQUEST_CMD),
    DELETE_TEAM_REQUEST_CMD(ConstantValue.DELETE_TEAM_REQUEST,ConstantValue.DELETE_TEAM_REQUEST_CMD),
    ENTRY_INVITE_PEOPLE_REQUEST_CMD(ConstantValue.ENTRY_PEOPLE_REQUEST_INVITE,ConstantValue.ENTRY_INVITE_PEOPLE_REQUEST_CMD),
    ENTRY_APPLY_PEOPLE_REQUEST_CMD(ConstantValue.ENTRY_PEOPLE_REQUEST_APPLY,ConstantValue.ENTRY_APPLY_PEOPLE_REQUEST_CMD),
    EXIT_TEAM_REQUEST_CMD(ConstantValue.EXIT_TEAM_REQUEST,ConstantValue.EXIT_TEAM_REQUEST_CMD),
    ASK_CAN_COPY_SCENE_REQUEST_CMD(ConstantValue.ASK_CAN_COPY_SCENE_REQUEST,ConstantValue.ASK_CAN_COPY_SCENE_REQUEST_CMD),
    COPY_SCENE_MESSAGE_REQUEST_CMD(ConstantValue.COPY_SCENE_MESSAGE_REQUEST,ConstantValue.COPY_SCENE_MESSAGE_REQUEST_CMD),
    CREATE_COPY_SCENE_REQUEST_CMD(ConstantValue.CREATE_COPY_SCENE_REQUEST,ConstantValue.CREATE_COPY_SCENE_REQUEST_CMD),
    ENTER_COPY_SCENE_REQUEST_CMD(ConstantValue.ENTER_COPY_SCENE_REQUEST,ConstantValue.ENTER_COPYSCENE_REQUEST_CMD),
    EXIT_COPY_SCENE_REQUEST_CMD(ConstantValue.EXIT_COPY_SCENE_REQUEST,ConstantValue.EXIT_COPYSCENE_REQUEST_CMD),
    SEND_TO_ALL_REQUEST_CMD(ConstantValue.SEND_TO_ALL_REQUEST,ConstantValue.SEND_TO_ALL_REQUEST_CMD),
    SEND_TO_ONE_REQUEST_CMD(ConstantValue.SEND_TO_ONE_REQUEST,ConstantValue.SEND_TO_ONE_REQUEST_CMD),
    SEND_TO_TEAM_REQUEST_CMD(ConstantValue.SEND_TO_TEAM_REQUEST,ConstantValue.SEND_TO_TEAM_REQUEST_CMD),
    SEND_TO_SCENE_REQUEST_CMD(ConstantValue.SEND_TO_SCENE_REQUEST,ConstantValue.SEND_TO_SCENE_REQUEST_CMD),
    SORT_BACKPACK_REQUEST_CMD(ConstantValue.SORT_BACKPACK_REQUEST,ConstantValue.SORT_BACKPACK_REQUEST_CMD),

    GET_EMAIL_MESSAGE_REQUEST_CMD(ConstantValue.GET_EMAIL_MESSAGE_REQUEST,ConstantValue.GET_EMAIL_MESSAGE_REQUEST_CMD),
    GET_EMAIL_ARTICLE_REQUEST_CMD(ConstantValue.GET_EMAIL_ARTICLE_REQUEST,ConstantValue.GET_EMAIL_ARTICLE_REQUEST_CMD),
    ACCEPT_EMAIL_LIST_REQUEST_CMD(ConstantValue.ACCEPT_EMAIL_LIST_REQUEST,ConstantValue.ACCEPT_EMAIL_LIST_REQUEST_CMD),
    IS_SEND_EMAIL_LIST_REQUEST_CMD(ConstantValue.IS_SEND_EMAIL_LIST_REQUEST,ConstantValue.IS_SEND_EMAIL_LIST_REQUEST_CMD),
    SEND_EMAIL_REQUEST_CMD(ConstantValue.SEND_EMAIL_REQUEST,ConstantValue.SEND_EMAIL_REQUEST_CMD),
    DELETE_ACCEPT_EMAIL_REQUEST_CMD(ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST,ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST_CMD),
    DELETE_SEND_EMAIL_REQUEST_CMD(ConstantValue.DELETE_SEND_EMAIL_REQUEST,ConstantValue.DELETE_SEND_EMAIL_REQUEST_CMD),
    FIND_ALL_CAN_REQUEST_CMD(ConstantValue.FIND_ALL_CAN_REQUEST,ConstantValue.FIND_ALL_CAN_REQUEST_CMD),
    GET_ARTICLE_FROM_FLOOR_REQUEST_CMD(ConstantValue.GET_ARTICLE_FROM_FLOOR_REQUEST,ConstantValue.GET_ARTICLE_FROM_FLOOR_REQUEST_CMD),
    CHECK_MONEY_NUMBER_REQUEST_CMD(ConstantValue.CHECK_MONEY_NUMBER_REQUEST,ConstantValue.CHECK_MONEY_NUMBER_REQUEST_CMD),
    BUY_GOODS_REQUEST_CMD(ConstantValue.BUY_GOODS_REQUEST,ConstantValue.BUY_GOODS_REQUEST_CMD),
    FIND_ALL_GOODS_REQUEST_CMD(ConstantValue.FIND_ALL_GOODS_REQUEST,ConstantValue.FIND_ALL_GOODS_REQUEST_CMD),
    CREATE_GUILD_REQUEST_CMD(ConstantValue.CREATE_GUILD_REQUEST,ConstantValue.CREATE_GUILD_REQUEST_CMD),
    SET_GUILD_POSITION_REQUEST_CMD(ConstantValue.SET_GUILD_POSITION_REQUEST,ConstantValue.SET_GUILD_POSITION_REQUEST_CMD),
    JOIN_GUILD_REQUEST_CMD(ConstantValue.JOIN_GUILD_REQUEST,ConstantValue.JOIN_GUILD_REQUEST_CMD),
    OUT_GUILD_REQUEST_CMD(ConstantValue.OUT_GUILD_REQUEST,ConstantValue.OUT_GUILD_REQUEST_CMD),
    AGREE_GUILD_APPLY_REQUEST_CMD(ConstantValue.AGREE_GUILD_APPLY_REQUEST,ConstantValue.AGREE_GUILD_APPLY_REQUEST_CMD),
    REFUSE_GUILD_APPLY_REQUEST_CMD(ConstantValue.REFUSE_GUILD_APPLY_REQUEST,ConstantValue.REFUSE_GUILD_APPLY_REQUEST_CMD),
    GET_GUILD_MESSAGE_REQUEST_CMD(ConstantValue.GET_GUILD_MESSAGE_REQUEST,ConstantValue.GET_GUILD_MESSAGE_REQUEST_CMD),
    GET_GUILD_APPLY_LIST_REQUEST_CMD(ConstantValue.GET_GUILD_APPLY_LIST_REQUEST,ConstantValue.GET_GUILD_APPLY_LIST_REQUEST_CMD),
    GET_GUILD_WAREHOUSE_REQUEST_CMD(ConstantValue.GET_GUILD_WAREHOUSE_REQUEST,ConstantValue.GET_GUILD_WAREHOUSE_REQUEST_CMD),
    CONTRIBUTE_MONEY_REQUEST_CMD(ConstantValue.CONTRIBUTE_MONEY_REQUEST,ConstantValue.CONTRIBUTE_MONEY_REQUEST_CMD),
    CONTRIBUTE_ARTICLE_REQUEST_CMD(ConstantValue.CONTRIBUTE_ARTICLE_REQUEST,ConstantValue.CONTRIBUTE_ARTICLE_REQUEST_CMD),
    GET_GUILD_MONEY_REQUEST_CMD(ConstantValue.GET_GUILD_MONEY_REQUEST,ConstantValue.GET_GUILD_MONEY_REQUEST_CMD),
    GET_GUILD_ARTICLE_REQUEST_CMD(ConstantValue.GET_GUILD_ARTICLE_REQUEST,ConstantValue.GET_GUILD_ARTICLE_REQUEST_CMD),
    ASK_DEAL_REQUEST_CMD(ConstantValue.ASK_DEAL_REQUEST,ConstantValue.ASK_DEAL_REQUEST_CMD),
    AGREE_DEAL_REQUEST_CMD(ConstantValue.AGREE_DEAL_REQUEST,ConstantValue.AGREE_DEAL_REQUEST_CMD),
    REFUSE_DEAL_REQUEST_CMD(ConstantValue.REFUSE_DEAL_REQUEST,ConstantValue.REFUSE_DEAL_REQUEST_CMD),
    CONFIRM_DEAL_REQUEST_CMD(ConstantValue.CONFIRM_DEAL_REQUEST,ConstantValue.CONFIRM_DEAL_REQUEST_CMD),
    CANCEL_DEAL_REQUEST_CMD(ConstantValue.CANCEL_DEAL_REQUEST,ConstantValue.CANCEL_DEAL_REQUEST_CMD),
    GET_DEAL_MESSAGE_REQUEST_CMD(ConstantValue.GET_DEAL_MESSAGE_REQUEST,ConstantValue.GET_DEAL_MESSAGE_REQUEST_CMD),
    SET_DEAL_MONEY_REQUEST_CMD(ConstantValue.GET_DEAL_MESSAGE_REQUEST,ConstantValue.SET_DEAL_MONEY_REQUEST_CMD),
    ADD_DEAL_ARTICLE_REQUEST_CMD(ConstantValue.ADD_DEAL_ARTICLE_REQUEST,ConstantValue.ADD_DEAL_ARTICLE_REQUEST_CMD),
    ABANDON_DEAL_ARTICLE_REQUEST_CMD(ConstantValue.ABANDON_DEAL_ARTICLE_REQUEST,ConstantValue.ABANDON_DEAL_ARTICLE_REQUEST_CMD),
    ABANDON_REQUEST_CMD(ConstantValue.ABANDON_REQUEST,ConstantValue.ABANDON_REQUEST_CMD);



    private  int cmd;
    private  String option;

    CmdCode(int cmd, String option) {
        this.cmd = cmd;
        this.option = option;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }


    public static CmdCode getValue(String option) {
        for (CmdCode ele : values()) {
            if(ele.getOption().equals(option)){
                return ele;
            }
        }
        return null;
    }
}
