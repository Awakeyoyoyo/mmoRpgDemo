package com.liqihao.commons.enums;

/**
 * 角色类型
 * @author LQHAO
 */
public enum RoleTypeCode {
    //
    NPC(0,"NPC"),PLAYER(1,"玩家"),ENEMY(2,"怪物"),BOSS(3,"BOSS"),HELPER(4,"召唤兽");
    private  int code;
    private  String name;
    RoleTypeCode(int code,String name)
    {
        this.code=code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public static String getValue(int code) {
        for (RoleTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getName();
            }
        }
        return null;
    }
}
