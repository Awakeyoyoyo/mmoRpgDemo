package com.liqihao.pojo.bean;

import com.liqihao.Cache.NpcMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 所有角色的父类
 * @author lqhao
 */
public class Role {
    private Integer id;
    private String name;
    private Integer hp;
    private Integer mp;
    private Integer attack;
    private double damageAdd;
    private Integer type;
    private Integer status;
    private Integer onStatus;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private volatile Integer nowHp;
    private volatile Integer nowMp;
    private Integer copySceneBeanId;
    private Integer mmosceneid;

    public Integer getMmosceneid() {
        return mmosceneid;
    }

    public void setMmosceneid(Integer mmosceneid) {
        this.mmosceneid = mmosceneid;
    }
    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public Integer getId() {
        return id;
    }
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
        return;
    }
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {
        return;
    }
    public void effectByBuffer(BufferBean bufferBean){
        return;
    }
    public void beAttack(SkillBean skillBean,Role fromRole) {
        return ;
    }

    public List<MmoSimpleRole> getAllRoles(){
        List<MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
        if (getMmosceneid()!=null){
            //场景中
            SceneBean sceneBean= SceneBeanMessageCache.getInstance().get(getMmosceneid());
            for (Integer id:sceneBean.getRoles()) {
                MmoSimpleRole role= OnlineRoleMessageCache.getInstance().get(id);
                if (role!=null){
                    mmoSimpleRoles.add(role);
                }
            }
        }else {
            //副本中
            CopySceneBean copySceneBean= CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            mmoSimpleRoles.addAll(copySceneBean.getMmoSimpleRoles());
        }
        return mmoSimpleRoles;
    }
    public List<MmoSimpleNPC> getAllNpcs(){
        List<MmoSimpleNPC> npcs=new ArrayList<>();
        if (getMmosceneid()!=null){
            //场景中
            SceneBean sceneBean= SceneBeanMessageCache.getInstance().get(getMmosceneid());
            for (Integer id:sceneBean.getNpcs()) {
                MmoSimpleNPC role= NpcMessageCache.getInstance().get(id);
                if (role!=null){
                    npcs.add(role);
                }
            }
        }else {
            //副本中

        }
        return npcs;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOnStatus() {
        return onStatus;
    }

    public void setOnStatus(Integer onStatus) {
        this.onStatus = onStatus;
    }

    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

    public Integer getNowHp() {
        return nowHp;
    }

    public void setNowHp(Integer nowHp) {
        this.nowHp = nowHp;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }
}
