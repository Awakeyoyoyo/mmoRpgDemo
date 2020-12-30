package com.liqihao.pojo.bean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * boss bean
 * @author lqhao
 */
public class BossBean extends Role{
    private String skillIds;
    private String medicines;
    private String equipmentIds;
    private Integer money;
    /**
     * 仇恨
     */
    private ConcurrentHashMap<Role,Integer> hatredMap;
    /**
     * 技能&cd
     */
    private List<SkillBean> skillBeans;
    private HashMap<Integer, Long> cdMap;
    private Integer BossBeanId;
    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();
    private Integer copySceneBeanId;

    @Override
    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    @Override
    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public Integer getMoney() {
        return money;
    }
    public void setMoney(Integer money) {
        this.money = money;
    }
    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(String equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public Integer getBossBeanId() {
        return BossBeanId;
    }

    public void setBossBeanId(Integer bossBeanId) {
        BossBeanId = bossBeanId;
    }

    public ConcurrentHashMap<Role, Integer> getHatredMap() {
        return hatredMap;
    }

    public void setHatredMap(ConcurrentHashMap<Role, Integer> hatredMap) {
        this.hatredMap = hatredMap;
    }

    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }



    @Override
    public void beAttack(SkillBean skillBean,Role fromRole) {
        BossBean bossBean=this;
        Integer reduce = 0;
        try {
            hpRwLock.writeLock().lock();
            Integer hp = bossBean.getNowHp();
            if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())) {
                //固伤 只有技能伤害
                reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + fromRole.getDamageAdd()));
                hp -= reduce;
            }
            if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                //百分比 按照攻击力比例增加
                Integer damage = skillBean.getBaseDamage();
                damage = (int) Math.ceil(damage + fromRole.getAttack() * skillBean.getAddPercon());
                hp = hp - damage;
                reduce = damage;
            }
            if (hp <= 0) {
                reduce = reduce + hp;
                hp = 0;
                bossBean.setStatus(RoleStatusCode.DIE.getCode());
                // 挑战成功
                CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
                TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(copySceneBean.getTeamId());
                copySceneBean.changeResult(teamBean);
            }
            bossBean.setNowHp(hp);
        }finally {
            hpRwLock.writeLock().unlock();
        }
        // 扣血伤害
        PlayModel.RoleIdDamage.Builder damageR = PlayModel.RoleIdDamage.newBuilder();
        damageR.setFromRoleId(fromRole.getId());
        damageR.setToRoleType(fromRole.getType());
        damageR.setToRoleId(bossBean.getId());
        damageR.setToRoleType(getType());
        damageR.setAttackStyle(AttackStyleCode.ATTACK.getCode());
        damageR.setBufferId(-1);
        damageR.setDamage(reduce);
        damageR.setDamageType(DamageTypeCode.HP.getCode());
        damageR.setMp(bossBean.getNowMp());
        damageR.setNowblood(bossBean.getNowHp());
        damageR.setSkillId(skillBean.getId());
        damageR.setState(bossBean.getStatus());
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesBuilder=PlayModel.DamagesNoticeResponse.newBuilder();
        damagesBuilder.setRoleIdDamage(damageR);
        myMessageBuilder.setDamagesNoticeResponse(damagesBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播给所有当前场景
        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(this.getMmosceneid());
        List<Integer> roles=sceneBean.getRoles();
        for (Integer id: roles) {
            MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(id);
            if (role!=null){
                Channel c=ChannelMessageCache.getInstance().get(role.getId());
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }
        }
        //怪物攻击本人
        if (!bossBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
            bossBean.bossAttack();
        }
    }

    public void bossAttack() {
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
        if (t != null) {
            //代表着该npc已经启动攻击线程
            return;
        } else {
            CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            ScheduledThreadPoolUtil.BossAttackTask bossAttackTask = new ScheduledThreadPoolUtil.BossAttackTask(this,copySceneBean,getSkillBeans());
            t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bossAttackTask, 0, 5, TimeUnit.SECONDS);
            ScheduledThreadPoolUtil.getNpcTaskMap().put(getId(), t);
        }
    }


    @Override
    public void effectByBuffer(BufferBean bufferBean) {
        //根据buffer类型扣血扣蓝
        if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEHP.getCode())) {
            hpRwLock.writeLock().lock();
            try {
                Integer hp = getNowHp() - bufferBean.getBuffNum();
                if (hp <= 0) {
                    hp = 0;
                    setStatus(RoleStatusCode.DIE.getCode());
                }
                setNowHp(hp);
            } finally {
                hpRwLock.writeLock().unlock();
            }

        } else if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEMP.getCode())) {
            mpRwLock.writeLock().lock();
            try {
                Integer mp = getNowMp() - bufferBean.getBuffNum();
                if (mp <= 0) {
                    mp = 0;
                }
                setNowMp(mp);
            } finally {
                mpRwLock.writeLock().unlock();
            }
        }
        //广播信息
        Integer sceneId = OnlineRoleMessageCache.getInstance().get(bufferBean.getFromRoleId()).getMmosceneid();
        List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
        //生成数据包
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setDamageType(DamageTypeCode.HP.getCode()).setAttackStyle(AttackStyleCode.BUFFER.getCode())
                .setDamage(bufferBean.getBuffNum()).setFromRoleId(bufferBean.getFromRoleId()).setToRoleId(bufferBean.getToRoleId())
                .setState(getStatus()).setMp(getNowMp()).setBufferId(bufferBean.getId()).setNowblood(getNowHp());
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        for (Integer playerId : players) {
            Channel c = ChannelMessageCache.getInstance().get(playerId);
            if (c != null) {
                c.writeAndFlush(nettyResponse);
            }

        }

    }

    public Role getTarget() {
        ConcurrentHashMap<Role,Integer> hatredMap=getHatredMap();
        if (hatredMap.size()>0) {
            Role target=null;
            Integer max=0;
            for (Role role:hatredMap.keySet()){
                if (role.getStatus().equals(RoleStatusCode.DIE.getCode())){
                    //如果以及死了就消除仇恨了
                    removeHatred(role);
                    continue;
                }
                if (hatredMap.get(role)>max){
                    target=role;
                    max=hatredMap.get(role);
                }
            }
            return target;
        }else{
            return null;
        }
    }
    public void addHarted(Integer roleId,Integer number){

    }
    //消除仇恨
    public void removeHatred(Role role){
        if (getHatredMap().containsKey(role)){
            getHatredMap().remove(role);
        }
    }
    //根据skillI获取技能
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        for (SkillBean b : getSkillBeans()) {
            if (b.getId().equals(skillId)) {
                return b;
            }
        }
        return null;
    }
    //使用技能
    public  void useSkill(List<Role> target, Integer skillId) {
        SkillBean skillBean = getSkillBeanBySkillId(skillId);

        if (skillBean.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())) {
            //扣血
            setNowHp(getNowHp() - skillBean.getConsumeNum());
        } else {
            //扣篮
            setNowMp(getNowMp() - skillBean.getConsumeNum());
        }
        List<PlayModel.RoleIdDamage> list = new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getId());
        damageU.setToRoleId(getId());
        damageU.setToRoleType(RoleTypeCode.BOSS.getCode());
        damageU.setFromRoleType(RoleTypeCode.BOSS.getCode());
        damageU.setArticleId(-1);
        damageU.setArticleType(-1);
        damageU.setAttackStyle(AttackStyleCode.USESKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(getNowMp());
        damageU.setNowblood(getNowHp());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(getStatus());
        list.add(damageU.build());
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
        PlayModel.UseSkillResponse.Builder useSkillBuilder=PlayModel.UseSkillResponse.newBuilder();
        useSkillBuilder.addAllRoleIdDamages(list);
        myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        List<Integer> players=SceneBeanMessageCache.getInstance().get(this.getMmosceneid()).getRoles();
        for (Integer playerId:players){
            Channel c= ChannelMessageCache.getInstance().get(playerId);
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }

        //  被攻击怪物or人物orBoss
        for (Role r :target) {
            r.beAttack(skillBean,this);
            //buffer
            for (Integer bufferId:skillBean.getBufferIds()) {
                BufferMessage bufferMessage= BufferMessageCache.getInstance().get(bufferId);
                skillBean.bufferToPeople(bufferMessage, this,r);
            }
        }
//        //cd
//        Map<Integer, Long> map = getCdMap();
//        Long time = System.currentTimeMillis();
//        int addTime = skillBean.getCd() * 1000;
//        map.put(skillBean.getId(), time + addTime);
        //buffer
    }

}
