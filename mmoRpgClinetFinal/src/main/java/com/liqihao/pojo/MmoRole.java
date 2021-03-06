package com.liqihao.pojo;

import java.util.HashMap;
import java.util.List;

public class MmoRole {
    private Integer id;

    private Integer status;

    private String name;

    private Integer type;

    private Integer mmosceneid;

    private Integer onstatus;

    private Integer Blood;

    private Integer nowBlood;

    private Integer mp;

    private Integer nowMp;

    private Integer professionId;

    private Integer money;

    private HashMap<Integer,Long> cdMap;

    private List<Integer> skillIdList;
    private Integer teamId;

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMmosceneid() {
        return mmosceneid;
    }

    public void setMmosceneid(Integer mmosceneid) {
        this.mmosceneid = mmosceneid;
    }

    public Integer getOnstatus() {
        return onstatus;
    }

    public void setOnstatus(Integer onstatus) {
        this.onstatus = onstatus;
    }

    public Integer getBlood() {
        return Blood;
    }

    public void setBlood(Integer blood) {
        Blood = blood;
    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }

    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }
}
