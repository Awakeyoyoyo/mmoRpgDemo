package com.liqihao.pojo.bean;


import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.MyObserver;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.LogicThreadPool;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 缓存中存储的人物类
 *
 * @author lqhao
 */
public class MmoSimpleRole extends Role implements MyObserver {
    private volatile HashMap<Integer, Long> cdMap;
    private List<Integer> skillIdList;
    private List<SkillBean> skillBeans;
    private BackPackManager backpackManager;
    private List<Integer> needDeleteEquipmentIds = new ArrayList<>();
    private Integer lastSceneId;
    private Integer teamApplyOrInviteSize;
    /**
     * 装备栏
     */
    private HashMap<Integer, EquipmentBean> equipmentBeanHashMap;
    /**
     * 非副本beanId 而是副本基本信息id
     */
    private Integer copySceneId;
    /**
     * 队伍邀请函
     */
    private ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans = new ConcurrentLinkedQueue<>();
    /**
     * 已发送邮件
     */
    private ConcurrentHashMap<Integer,MmoEmailBean> fromMmoEmailBeanConcurrentHashMap=new ConcurrentHashMap<>();
    /**
     * 已接受邮件
     */
    private ConcurrentHashMap<Integer,MmoEmailBean> toMmoEmailBeanConcurrentHashMap=new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, MmoEmailBean> getFromMmoEmailBeanConcurrentHashMap() {
        return fromMmoEmailBeanConcurrentHashMap;
    }

    public void setFromMmoEmailBeanConcurrentHashMap(ConcurrentHashMap<Integer, MmoEmailBean> fromMmoEmailBeanConcurrentHashMap) {
        this.fromMmoEmailBeanConcurrentHashMap = fromMmoEmailBeanConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, MmoEmailBean> getToMmoEmailBeanConcurrentHashMap() {
        return toMmoEmailBeanConcurrentHashMap;
    }

    public void setToMmoEmailBeanConcurrentHashMap(ConcurrentHashMap<Integer, MmoEmailBean> toMmoEmailBeanConcurrentHashMap) {
        this.toMmoEmailBeanConcurrentHashMap = toMmoEmailBeanConcurrentHashMap;
    }

    public ConcurrentLinkedQueue<TeamApplyOrInviteBean> getTeamApplyOrInviteBeans() {
        return teamApplyOrInviteBeans;
    }

    public void setTeamApplyOrInviteBeans(ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans) {
        this.teamApplyOrInviteBeans = teamApplyOrInviteBeans;
    }

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
    }

    public void setTeamApplyOrInviteSize(Integer teamApplyOrInviteSize) {
        this.teamApplyOrInviteSize = teamApplyOrInviteSize;
    }
    public Integer getLastSceneId() {
        return lastSceneId;
    }

    public void setLastSceneId(Integer lastSceneId) {
        this.lastSceneId = lastSceneId;
    }

    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }

    public List<Integer> getNeedDeleteEquipmentIds() {
        return needDeleteEquipmentIds;
    }

    public void setNeedDeleteEquipmentIds(List<Integer> needDeleteEquipmentIds) {
        this.needDeleteEquipmentIds = needDeleteEquipmentIds;
    }

    public HashMap<Integer, EquipmentBean> getEquipmentBeanHashMap() {
        return equipmentBeanHashMap;
    }

    public void setEquipmentBeanHashMap(HashMap<Integer, EquipmentBean> equipmentBeanHashMap) {
        this.equipmentBeanHashMap = equipmentBeanHashMap;
    }

    public BackPackManager getBackpackManager() {
        return backpackManager;
    }

    public void setBackpackManager(BackPackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }


    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }
    /**
     * 增加邀请
     * @param teamApplyOrInviteBean
     */
    public void addTeamApplyOrInviteBean(TeamApplyOrInviteBean teamApplyOrInviteBean) {
        checkOutTime();
        //邀请的大小，先进先出咯
        if (teamApplyOrInviteBeans.size() >= teamApplyOrInviteSize) {
            teamApplyOrInviteBeans.poll();
        }
        teamApplyOrInviteBeans.add(teamApplyOrInviteBean);
    }
    /**
     * 检测邀请过时
     */
    private void checkOutTime() {
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()) {
            TeamApplyOrInviteBean bean = (TeamApplyOrInviteBean) iterator.next();
            if (bean.endTime < System.currentTimeMillis()) {
                teamApplyOrInviteBeans.remove(bean);
            }
        }
    }

    /**
     * 初始化对象
     *
     * @param role
     * @param baseRoleMessage
     */
    public void init(MmoRolePOJO role, BaseRoleMessage baseRoleMessage) {
        setId(role.getId());
        setMmoSceneId(role.getMmoSceneId());
        setName(role.getName());
        setOnStatus(role.getOnStatus());
        setStatus(role.getStatus());
        setType(role.getType());
        List<SkillBean> skillBeans = CommonsUtil.skillIdsToSkillBeans(role.getSkillIds());
        setSkillBeans(skillBeans);
        setHp(baseRoleMessage.getHp());
        setNowHp(baseRoleMessage.getHp());
        setMp(baseRoleMessage.getMp());
        setDamageAdd(baseRoleMessage.getDamageAdd());
        setNowMp(baseRoleMessage.getMp());
        setAttack(baseRoleMessage.getAttack());
        List<Integer> skillIds = CommonsUtil.split(role.getSkillIds());
        setSkillIdList(skillIds);
        setCdMap(new HashMap<Integer, Long>());
        setBufferBeans(new CopyOnWriteArrayList<>());
        setEquipmentBeanHashMap(new HashMap<>());
    }

    /**
     * 使用道具
     */
    public Boolean useArticle(Integer articleId) {
        Article article = backpackManager.getArticleByArticleId(articleId);
        if (article != null && article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            //药品
            MedicineBean medicineBean = (MedicineBean) article;
            //删减
            backpackManager.useOrAbandonArticle(articleId, 1);
            Boolean flag = medicineBean.useMedicene(getId());
            return flag;
        } else if (article != null && article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            //装备
            EquipmentBean equipmentBean = (EquipmentBean) article;
            //删减
            backpackManager.useOrAbandonArticle(articleId, 1);
            //穿
            return useEquipment(equipmentBean);
        } else {
            return false;
        }

    }

    /**
     *  穿装备 or替换装备
     */

    private Boolean useEquipment(EquipmentBean equipmentBean) {
        //判断该位置是否有装备
        EquipmentBean oldBean = getEquipmentBeanHashMap().get(equipmentBean.getPosition());
        if (oldBean != null) {
            //放回背包内
            //背包新增数据
            //修改人物属性
            setAttack(getAttack() - oldBean.getAttackAdd());
            setDamageAdd(getDamageAdd() - oldBean.getDamageAdd());
            needDeleteEquipmentIds.add(oldBean.getEquipmentBagId());
            backpackManager.put(oldBean);
        }
        //背包减少装备
        backpackManager.useOrAbandonArticle(equipmentBean.getArticleId(), 1);
        //装备栏增加装备
        equipmentBeanHashMap.put(equipmentBean.getPosition(), equipmentBean);
        //人物属性
        setAttack(getAttack() + equipmentBean.getAttackAdd());
        setDamageAdd(getDamageAdd() + equipmentBean.getDamageAdd());
        return true;
    }

    /**
     * 脱装备
     */
    public Boolean unUseEquipment(Integer position) {
        //判断该位置是否有装备
        EquipmentBean equipmentBean = getEquipmentBeanHashMap().get(position);
        if (equipmentBean == null) {
            //无装备
            return false;
        } else {
            equipmentBeanHashMap.remove(position);
            //装备栏数据库减少该装备
            if (equipmentBean.getEquipmentBagId() != null) {
                needDeleteEquipmentIds.add(equipmentBean.getEquipmentBagId());
            }
            //装备栏id为null
            equipmentBean.setEquipmentBagId(null);
            //放入背包
            backpackManager.put(equipmentBean);
            setAttack(getAttack() - equipmentBean.getAttackAdd());
            setDamageAdd(getDamageAdd() - equipmentBean.getDamageAdd());
            return true;
        }
    }

    /**
     * 获取装备栏所有信息
     */
    public List<EquipmentDto> getEquipments() {
        List<EquipmentDto> equipmentDtos = new ArrayList<>();
        for (EquipmentBean bean : equipmentBeanHashMap.values()) {
            EquipmentDto equipmentDto = new EquipmentDto();
            equipmentDto.setId(bean.getId());
            equipmentDto.setNowdurability(bean.getNowDurability());
            equipmentDto.setPosition(bean.getPosition());
            equipmentDto.setEquipmentBagId(bean.getEquipmentBagId());
            equipmentDto.setEquipmentId(bean.getEquipmentId());
            equipmentDtos.add(equipmentDto);
        }
        return equipmentDtos;
    }

    /**
     * 根据skillI获取技能
     */
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        for (SkillBean b : getSkillBeans()) {
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
        SkillBean skillBean = getSkillBeanBySkillId(skillId);
        //武器耐久度-2
        EquipmentBean equipmentBean = this.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode());
        if (equipmentBean != null) {
            equipmentBean.setNowDurability(equipmentBean.getNowDurability() - 2);
            if (equipmentBean.getNowDurability() < 0) {
                equipmentBean.setNowDurability(0);
            }
        }
        if (skillBean.getConsumeType().equals(ConsumeTypeCode.HP.getCode())) {
            //扣血
            setNowHp(getNowHp() - skillBean.getConsumeNum());

        } else {
            //扣篮
            setNowMp(getNowMp() - skillBean.getConsumeNum());
            //判断是否已经有自动回蓝任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap = ScheduledThreadPoolUtil.getReplyMpRole();
            //自动回蓝任务的key
            String key = getId() + "AUTO_MP";
            if (!replyMpRoleMap.containsKey(key)) {
                //number为空 代表着自动回蓝
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(this, null, DamageTypeCode.MP.getCode(), key);
                // 周期性执行，每5秒执行一次
                ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 5, TimeUnit.SECONDS);
                replyMpRoleMap.put(key, t);
            }
        }
        List<PlayModel.RoleIdDamage> list = new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getId());
        damageU.setToRoleId(getId());
        damageU.setToRoleType(RoleTypeCode.PLAYER.getCode());
        damageU.setFromRoleType(RoleTypeCode.PLAYER.getCode());
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
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        List<Integer> players;
        if (getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(this.getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }



        //  被攻击怪物or人物orBoss
        for (Role r :target) {
            r.beAttack(skillBean,this);
            //buffer
            for (Integer bufferId:skillBean.getBufferIds()) {
                BufferMessage bufferMessage=BufferMessageCache.getInstance().get(bufferId);
                skillBean.bufferToPeople(bufferMessage, this,r);
            }
        }

        //cd
        Map<Integer, Long> map = getCdMap();
        Long time = System.currentTimeMillis();
        int addTime = skillBean.getCd() * 1000;
        map.put(skillBean.getId(), time + addTime);
        //buffer
    }

    /**
     * 被攻击
     */
    @Override
    public void beAttack(SkillBean skillBean,Role fromRole) {
        Role role=this;
        Integer reduce = 0;
        if (skillBean.getSkillType().equals(SkillTypeCode.FIX.getCode())) {
            //固伤 只有技能伤害
            reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + this.getDamageAdd()));
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(fromRole.getId());
            damageU.setFromRoleType(fromRole.getType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
            damageU.setBufferId(-1);
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(skillBean.getId());
            changeNowBlood(-reduce,damageU,AttackStyleCode.USE_SKILL.getCode());
        }
        if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
            //百分比 按照攻击力比例增加
            Integer damage = skillBean.getBaseDamage();
            reduce = (int) Math.ceil(damage + role.getAttack() * skillBean.getAddPerson());
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(fromRole.getId());
            damageU.setFromRoleType(fromRole.getType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
            damageU.setBufferId(-1);
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(skillBean.getId());
            changeNowBlood(-reduce,damageU,AttackStyleCode.USE_SKILL.getCode());
        }

    }

    /**
     * 扣血
     */
    @Override
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {
        //获取对应线程的下标
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        if (type == AttackStyleCode.USE_SKILL.getCode()) {
            LogicThreadPool.getInstance().execute(new ChangeHpByAttackTask(number, this, damageU), index);
        } else if (type == AttackStyleCode.MEDICINE.getCode()){
            LogicThreadPool.getInstance().execute(new ChangeHpByMedicineTask(number, this, damageU), index);
        }else{
            LogicThreadPool.getInstance().execute(new ChangeHpByBufferTask(number,this,damageU),index);
        }
    }

    /**
     * 扣蓝
     */
    @Override
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        LogicThreadPool.getInstance().execute(new ChangeMpTask(number, this, damageU), index);
    }

    /**
     * buffer影响
     * @param bufferBean
     */
    @Override
    public void effectByBuffer(BufferBean bufferBean) {
        if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCE_HP.getCode())) {
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(bufferBean.getFromRoleId());
            damageU.setFromRoleType(bufferBean.getFromRoleType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setAttackStyle(AttackStyleCode.BUFFER.getCode());
            damageU.setBufferId(bufferBean.getId());
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(-1);
            changeNowBlood(-bufferBean.getBuffNum(),damageU,AttackStyleCode.BUFFER.getCode());
        } else if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCE_MP.getCode())) {
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(bufferBean.getFromRoleId());
            damageU.setFromRoleType(bufferBean.getFromRoleType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setAttackStyle(AttackStyleCode.BUFFER.getCode());
            damageU.setBufferId(bufferBean.getId());
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(-1);
            changeMp(-bufferBean.getBuffNum(),damageU);
        }
    }

    /**
     * 前往场景
     * @param nextSceneId
     * @return
     */
    public List<MmoSimpleRole> wentScene(Integer nextSceneId) {
        //修改scene 如果为null 则是刚从副本中出来
        if (getMmoSceneId() != null) {
            SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles().remove(getId());
        }
        SceneBeanMessageCache.getInstance().get(nextSceneId).getRoles().add(getId());
        setMmoSceneId(nextSceneId);

        //查询出npc 和SimpleRole
        List<MmoSimpleRole> nextSceneRoles = new ArrayList<>();
        SceneBean nextScene = SceneBeanMessageCache.getInstance().get(nextSceneId);
        List<Integer> roles = nextScene.getRoles();
        List<Integer> npcs = nextScene.getNpcs();
        //NPC
        for (Integer npcId : npcs) {
            MmoSimpleNPC temp = NpcMessageCache.getInstance().get(npcId);
            nextSceneRoles.add(CommonsUtil.NpcToMmoSimpleRole(temp));
        }
        //ROLES
        for (Integer rId : roles) {
            MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(rId);
            if (role!=null) {
                nextSceneRoles.add(role);
            }
        }
        return nextSceneRoles;
    }

    /**
     * 获取邀请信息
     */
    public List<TeamApplyOrInviteBean> getInviteBeans() {
        checkOutTime();
        return teamApplyOrInviteBeans.stream().filter(e -> e.getType().equals(TeamApplyInviteCode.INVITE.getCode())).collect(Collectors.toList());
    }

    /**
     * 拒绝邀请
     */
    public TeamApplyOrInviteBean refuseInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean = null;
        while (iterator.hasNext()) {
            teamApplyOrInviteBean = (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId) &&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())) {
                teamApplyOrInviteBeans.remove(teamApplyOrInviteBean);
                getTeamApplyOrInviteBeans().remove(teamApplyOrInviteBean);
                return teamApplyOrInviteBean;
            }
        }
        return null;
    }

    /**
     * 是否包含该邀请
     * @param teamId
     * @return
     */
    public TeamApplyOrInviteBean containInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean = null;
        while (iterator.hasNext()) {
            teamApplyOrInviteBean = (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId) &&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())) {
                return teamApplyOrInviteBean;
            }
        }
        return teamApplyOrInviteBean;
    }

    /**
     * 前往副本
     * @param copySceneBean
     * @return
     */
    public Boolean wentCopyScene(CopySceneBean copySceneBean) {
        //从当前场景消失
        Integer sceneId = getMmoSceneId();
        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(sceneId);
        sceneBean.getRoles().remove(getId());
        setMmoSceneId(null);
        //人物设置副本
        this.setCopySceneId(copySceneBean.getId());
        this.setLastSceneId(sceneId);
        this.setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
        //副本操作
        copySceneBean.getRoles().add(this);
        if (copySceneBean.getNowBoss()==null) {
            copySceneBean.bossComeOrFinish();
        }
        return true;
    }

    @Override
    /**
     * 聊天
     */
    public void update(Role fromRole, String str,Integer chatType) {
        Channel c=ChannelMessageCache.getInstance().get(getId());
        if (c!=null){
            ChatModel.RoleDto roleDto=ChatModel.RoleDto.newBuilder()
                    .setId(fromRole.getId()).setName(fromRole.getName()).setOnStatus(fromRole.getOnStatus())
                    .setStatus(fromRole.getStatus()).setTeamId(fromRole.getTeamId()==null?-1:fromRole.getTeamId()).setType(fromRole.getType()).build();
            ChatModel.ChatModelMessage myMessage=ChatModel.ChatModelMessage.newBuilder()
                    .setDataType(ChatModel.ChatModelMessage.DateType.AcceptMessageResponse)
                    .setAcceptMessageResponse(ChatModel.AcceptMessageResponse.newBuilder().setFromRole(roleDto).setChatType(chatType).setStr(str)).build();
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ACCEPT_MESSAGE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessage.toByteArray());
            c.writeAndFlush(nettyResponse);
        }
    }

    @Override
    public Integer returnRoleId() {
        return getId();
    }

    /**
     * 改变血量buffer的任务
     */
    private class ChangeHpByBufferTask implements Runnable{
        Logger logger = Logger.getLogger(ChangeHpByBufferTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;
        public ChangeHpByBufferTask() {
        }

        public ChangeHpByBufferTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }
        @Override
        public void run() {
            logger.info("当前changeHpByBuffer线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getNowHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setNowHp(0);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());

            //封装成nettyResponse
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damageRpsponse = PlayModel.DamagesNoticeResponse.newBuilder();
            damageRpsponse.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damageRpsponse.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            //广播
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }
    /**
     * 改变蓝量buffer的任务
     */
    private class ChangeHpByMedicineTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeMpTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeHpByMedicineTask() {
        }

        public ChangeHpByMedicineTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeHp线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            Integer sceneId = mmoSimpleRole.getMmoSceneId();
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }
    /**
     * 被攻击改变血量的任务
     */
    private class ChangeHpByAttackTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeHpByMedicineTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeHpByAttackTask() {
        }

        public ChangeHpByAttackTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeHpByAttack线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getNowHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setNowHp(0);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            List<PlayModel.RoleIdDamage> list = new ArrayList<>();
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            list.add(damageU.build());
            //封装成nettyResponse
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
            PlayModel.UseSkillResponse.Builder useSkillBuilder = PlayModel.UseSkillResponse.newBuilder();
            useSkillBuilder.addAllRoleIdDamages(list);
            myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            //广播
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }

        }
    }
    /**
     * 改变蓝量的任务
     */
    private class ChangeMpTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeMpTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeMpTask() {
        }

        public ChangeMpTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeMp线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldMp = mmoSimpleRole.getNowMp();
            Integer newNumber = oldMp + number;
            if (newNumber > getMp()) {
                mmoSimpleRole.setNowMp(getMp());
                number = getMp() - oldMp;
            } else {
                mmoSimpleRole.setNowMp(newNumber);
            }
            damageU.setDamage(number);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }
}
