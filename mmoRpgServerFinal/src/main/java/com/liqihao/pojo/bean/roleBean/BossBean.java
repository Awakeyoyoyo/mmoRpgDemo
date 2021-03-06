package com.liqihao.pojo.bean.roleBean;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.pojo.bean.buffBean.BaseBuffBean;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.NotificationUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * boss bean
 * @author lqhao
 */
public class BossBean extends Role {
    /**
     * boss基本信息id
     */
    private Integer BossMessageId;
    /**
     * 仇恨
     */
    private ConcurrentHashMap<Role,Integer> hatredMap;
    private HashMap<Integer, Long> cdMap;
    private Integer BossBeanId;
    private Integer copySceneBeanId;

    public Integer getBossMessageId() {
        return BossMessageId;
    }

    public void setBossMessageId(Integer bossMessageId) {
        BossMessageId = bossMessageId;
    }

    @Override
    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    @Override
    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
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

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }

    /**
     * 被攻击
     * @param skillBean
     * @param fromRole
     */
    @Override
    public void beAttack(SkillBean skillBean, Role fromRole) {
        BossBean bossBean=this;
        Integer reduce = 0;
        try {
            hpRwLock.writeLock().lock();
            Integer hp = bossBean.getNowHp();
            if (skillBean.getSkillType().equals(SkillTypeCode.FIX.getCode())) {
                //固伤 只有技能伤害
                reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + fromRole.getDamageAdd()));
                hp -= reduce;
            }
            if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                //百分比 按照攻击力比例增加
                Integer damage = skillBean.getBaseDamage();
                damage = (int) Math.ceil(damage + fromRole.getAttack() * skillBean.getAddPerson());
                hp = hp - damage;
                reduce = damage;
            }
            if (hp <= 0) {
                reduce = reduce + hp;
                hp = 0;
                die(fromRole);
            }
            bossBean.setNowHp(hp);
        }finally {
            hpRwLock.writeLock().unlock();
        }
        //增加仇恨
        addHatred(fromRole,reduce);
        // 扣血伤害
        PlayModel.RoleIdDamage.Builder damageR = PlayModel.RoleIdDamage.newBuilder();
        damageR.setFromRoleId(fromRole.getId());
        damageR.setFromRoleType(fromRole.getType());
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
        NotificationUtil.notificationSceneRole(nettyResponse,this,myMessageBuilder.build());
        //怪物攻击本人
        if (!bossBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
            bossBean.bossAttack();
        }
    }

    /**
     * 死亡
     * @param fromRole
     */
    @Override
    public void die(Role fromRole) {
        setStatus(RoleStatusCode.DIE.getCode());
        // 挑战成功or出现下一个boss
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        copySceneBean.bossComeOrFinish();
        //移除boss攻击线程
        Integer bossAttackId=getId()+hashCode();
        synchronized (ScheduledThreadPoolUtil.getBossTaskMap()) {
            ScheduledFuture<?> t = ScheduledThreadPoolUtil.getBossTaskMap().get(bossAttackId);
            if (t != null) {
                ScheduledThreadPoolUtil.getBossTaskMap().remove(bossAttackId);
                t.cancel(false);
            }
        }
        //经验
        if (fromRole.getType().equals(RoleTypeCode.PLAYER.getCode())||fromRole.getType().equals(RoleTypeCode.HELPER.getCode())) {
            MmoSimpleRole role;
            if (fromRole.getType().equals(RoleTypeCode.HELPER.getCode())){
                MmoHelperBean mmoHelperBean= (MmoHelperBean) fromRole;
                Integer roleId=mmoHelperBean.getMasterId();
                role=OnlineRoleMessageCache.getInstance().get(roleId);
                //停止召唤兽攻击
                mmoHelperBean.setTarget(null);
                Integer helperAttackId=fromRole.getId()+fromRole.hashCode();
                synchronized (ScheduledThreadPoolUtil.getHelperTaskMap()) {
                    ScheduledThreadPoolUtil.getHelperTaskMap().get(helperAttackId).cancel(false);
                    ScheduledThreadPoolUtil.getHelperTaskMap().remove(helperAttackId);
                }
            }else {
                role = (MmoSimpleRole) fromRole;
                MmoHelperBean mmoHelperBean = role.getMmoHelperBean();
                if (mmoHelperBean!=null) {
                    mmoHelperBean.setTarget(null);
                    Integer helperAttackId = mmoHelperBean.getId() + mmoHelperBean.hashCode();
                    synchronized (ScheduledThreadPoolUtil.getHelperTaskMap()) {
                        ScheduledThreadPoolUtil.getHelperTaskMap().get(helperAttackId).cancel(false);
                        ScheduledThreadPoolUtil.getHelperTaskMap().remove(helperAttackId);
                    }
                }
            }
            Integer teamId=role.getTeamId();
            TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(teamId);
            List<MmoSimpleRole> teamRoles=new ArrayList<>(teamBean.getMmoSimpleRoles());
            BossMessage bossMessage=BossMessageCache.getInstance().get(getBossMessageId());
            //整个队伍人物增加经验
            for (MmoSimpleRole teamRole : teamRoles) {
                if (teamRole.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    //放入各自玩家所在的线程仲执行
                    teamRole.execute(() -> teamRole.addExp(bossMessage.getAddExp()));

                }
            }

        }
    }

    /**
     * boss攻击
     */
    public void bossAttack() {
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
        Integer bossAttackId=getId()+hashCode();
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getBossTaskMap().get(bossAttackId);
        if (t != null) {
            //代表着该boss已启动攻击线程 不同副本的玩家同时攻击boss，操作boss线程任务集合
        } else {
            synchronized (ScheduledThreadPoolUtil.getBossTaskMap()) {
                t = ScheduledThreadPoolUtil.getBossTaskMap().get(bossAttackId);
                if (t==null) {
                    List<SkillBean> skillBeans;
                    BossMessage bossMessage= BossMessageCache.getInstance().get(getBossMessageId());
                    skillBeans= CommonsUtil.skillIdsToSkillBeans(CommonsUtil.split(bossMessage.getSkillIds()));
                    ScheduledThreadPoolUtil.BossAttackTask bossAttackTask = new ScheduledThreadPoolUtil.BossAttackTask(this, copySceneBean, skillBeans);
                    t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bossAttackTask, 0, 3, TimeUnit.SECONDS);
                    ScheduledThreadPoolUtil.getBossTaskMap().put(bossAttackId, t);
                }
            }
        }
    }

    /**
     * buff影响
     * @param bufferBean
     * @param fromRole
     */
    @Override
    public void effectByBuffer(BaseBuffBean bufferBean,Role fromRole) {
        //根据buffer类型扣血扣蓝
        bufferBean.effectToRole(this,fromRole);
    }

    /**
     * 寻找目标
     * @return
     */
    public Role getTarget() {
        if (getStatus().equals(RoleStatusCode.DIE.getCode())){
            return null;
        }
        synchronized (hatredMap) {
            ConcurrentHashMap<Role, Integer> hatredMap = getHatredMap();
            //判断是否有嘲讽buffer,则直接攻击嘲讽对象
            Iterator<BaseBuffBean> buffers=getBufferBeans().iterator();
            while(buffers.hasNext()){
                BaseBuffBean bufferBean=buffers.next();
                BufferMessage bufferMessage=BufferMessageCache.getInstance().get(bufferBean.getBufferMessageId());
               if (bufferMessage.getBuffType().equals(BufferTypeCode.GG_ATTACK.getCode())){
                   Role role= OnlineRoleMessageCache.getInstance().get(bufferBean.getFromRoleId());
                   if(role.getStatus().equals(RoleStatusCode.ALIVE.getCode())) {
                       return role;
                   }
               }
            }
            if (hatredMap.size() > 0) {
                Role target = null;
                Integer max = 0;
                for (Role role : hatredMap.keySet()) {
                    if (role.getStatus().equals(RoleStatusCode.DIE.getCode())||role.getMmoSceneId()!=null) {
                        //如果以及死了就消除仇恨了
                        removeHatred(role);
                        continue;
                    }
                    if (hatredMap.get(role) > max) {
                        target = role;
                        max = hatredMap.get(role);
                    }
                }
                if (max!=0) {
                    return target;
                }
            }
            CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            for (Role role : copySceneBean.getRoles()) {
                if (role.getStatus().equals(RoleStatusCode.ALIVE.getCode())&&role.getCopySceneBeanId()!=null&&role.getCopySceneBeanId().equals(copySceneBean.getCopySceneBeanId()))
                {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * 增加仇恨
     * @param role
     * @param number
     */
    public void addHatred(Role role,Integer number){
        synchronized (hatredMap) {
            ConcurrentHashMap<Role, Integer> hatredMap = getHatredMap();
            if (hatredMap.containsKey(role)) {
                Integer hart = hatredMap.get(role);
                hart += number;
                hatredMap.put(role, hart);
            } else {
                hatredMap.put(role, number);
            }
        }
    }

    /**
     * 消除仇恨
     */
    public void removeHatred(Role role){
        if (getHatredMap().containsKey(role)){
            getHatredMap().remove(role);
        }
    }

    /**
     * 根据skillI获取技能
     */
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        List<SkillBean> skillBeans;
        BossMessage bossMessage= BossMessageCache.getInstance().get(getBossMessageId());
        skillBeans= CommonsUtil.skillIdsToSkillBeans(CommonsUtil.split(bossMessage.getSkillIds()));
        for (SkillBean b : skillBeans) {
            if (b.getId().equals(skillId)) {
                return b;
            }
        }
        return null;
    }

    /**
     * 使用技能
     */
    public  void useSkill(List<Role> target, Integer skillId) {
        if (getStatus().equals(RoleStatusCode.DIE.getCode())){
            return;
        }
        SkillBean skillBean = getSkillBeanBySkillId(skillId);

        if (skillBean.getConsumeType().equals(ConsumeTypeCode.HP.getCode())) {
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
        damageU.setAttackStyle(AttackStyleCode.USE_SKILL.getCode());
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
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        NotificationUtil.notificationSceneRole(nettyResponse,this,myMessageBuilder.build());

        //  被攻击怪物or人物orBoss
        for (Role r :target) {
            r.beAttack(skillBean,this);
            //buffer
            for (Integer bufferId:skillBean.getBufferIds()) {
                BufferMessage bufferMessage= BufferMessageCache.getInstance().get(bufferId);
                skillBean.bufferToPeople(bufferMessage, this,r);
            }
        }
    }

    /**
     * 更改蓝量
     * @param number
     * @param damageU
     */
    @Override
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
    }

    /**
     * 更改血量
     * @param number
     * @param damageU
     * @param type
     */
    @Override
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {

    }

    @Override
    public void updateItem(Integer id) {
        return;
    }
}
