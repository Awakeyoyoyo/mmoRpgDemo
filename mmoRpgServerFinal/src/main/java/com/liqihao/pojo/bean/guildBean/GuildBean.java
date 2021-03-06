package com.liqihao.pojo.bean.guildBean;

import com.liqihao.Dbitem.Item;
import com.liqihao.cache.OnlineRoleMessageCache;
import com.liqihao.cache.RoleMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.GuildRolePositionCode;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.GuildModel;
import com.liqihao.provider.GuildServiceProvider;
import com.liqihao.util.NotificationUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 公会实例bean
 *
 * @author lqhao
 */
public class GuildBean extends Item {
    /**
     * 公会id
     */
    private Integer id;
    /**
     * 公会名称
     */
    private String name;
    /**
     * 会长名称
     */
    private Integer chairmanId;
    /**
     * 公会人数
     */
    private Integer peopleNum;
    /**
     * 公会等级
     */
    private Integer level;
    /**
     * 公会仓库
     */
    private WareHouseManager wareHouseManager;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 人员id集合
     */
    private CopyOnWriteArrayList<GuildRoleBean> guildRoleBeans = new CopyOnWriteArrayList<>();
    /**
     * 申请入公会请求
     */
    private CopyOnWriteArrayList<GuildApplyBean> guildApplyBeans = new CopyOnWriteArrayList<>();
    /**
     * 金钱
     */
    private Integer money;

    public CopyOnWriteArrayList<GuildRoleBean> getGuildRoleBeans() {
        return guildRoleBeans;
    }

    public void setGuildRoleBeans(CopyOnWriteArrayList<GuildRoleBean> guildRoleBeans) {
        this.guildRoleBeans = guildRoleBeans;
    }

    public CopyOnWriteArrayList<GuildApplyBean> getGuildApplyBeans() {
        return guildApplyBeans;
    }

    public void setGuildApplyBeans(CopyOnWriteArrayList<GuildApplyBean> guildApplyBeans) {
        this.guildApplyBeans = guildApplyBeans;
    }

    /**
     * 金币的读写锁
     */
    public final ReadWriteLock moneyRwLock = new ReentrantReadWriteLock();

    /**
     * 公会成员读写锁
     */
    public final ReadWriteLock peopleRwLock = new ReentrantReadWriteLock();

    public Integer getMoney() {
        moneyRwLock.readLock().lock();
        try {
            return money;
        } finally {
            moneyRwLock.readLock().unlock();
        }
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getId() {
        return id;
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

    public Integer getChairmanId() {
        return chairmanId;
    }

    public void setChairmanId(Integer chairmanId) {
        this.chairmanId = chairmanId;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public WareHouseManager getWareHouseManager() {
        return wareHouseManager;
    }

    public void setWareHouseManager(WareHouseManager wareHouseManager) {
        this.wareHouseManager = wareHouseManager;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    /**
     * 检查申请是否过时
     */
    private void checkOutTime() {
        Iterator iterator = guildApplyBeans.iterator();
        List<Integer> outTimeApplyIds = new ArrayList<>();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()) {
            GuildApplyBean bean = (GuildApplyBean) iterator.next();
            if (bean.getEndTime() < System.currentTimeMillis()) {
                iterator.remove();
                outTimeApplyIds.add(bean.getId());
            }
        }
        //数据入库
        GuildServiceProvider.getInstance().deleteApply(outTimeApplyIds);
    }

    /**
     * 公会申请
     * @param guildApplyBean
     */
    public void addGuildApplyBean(GuildApplyBean guildApplyBean) {
        //每次插入的时候删除过时的
        synchronized (guildApplyBeans) {
            checkOutTime();
            guildApplyBeans.add(guildApplyBean);
        }
    }

    /**
     * 拒绝申请
     */
    public boolean refuseApply(Integer guildApplyId) throws RpgServerException {
        synchronized (guildApplyBeans) {
            checkOutTime();
            Iterator iterator = guildApplyBeans.iterator();
            //每次插入都删除申请过时或者
            while (iterator.hasNext()) {
                GuildApplyBean bean = (GuildApplyBean) iterator.next();
                if (guildApplyId.equals(bean.getId())) {
                    iterator.remove();
                    MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(bean.getRoleId());
                    if (role == null) {
                        // 角色不在线 不发
                    } else {
                        sendJoinResponse(role, false);
                    }
                    //数据入库
                    List<Integer> deleteList = new ArrayList<Integer>();
                    deleteList.add(bean.getId());
                    GuildServiceProvider.getInstance().deleteApply(deleteList);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过申请
     */
    public boolean agreeApply(Integer guildApplyId) throws RpgServerException {
        synchronized (guildApplyBeans) {
            checkOutTime();
            Iterator iterator = guildApplyBeans.iterator();
            //每次插入都删除申请过时或者
            while (iterator.hasNext()) {
                GuildApplyBean bean = (GuildApplyBean) iterator.next();
                if (guildApplyId.equals(bean.getId())) {
                    peopleNum = peopleNum + 1;
                    //guild成员bean 增加
                    GuildRoleBean guildRoleBean = new GuildRoleBean();
                    guildRoleBean.setRoleId(bean.getRoleId());
                    guildRoleBean.setGuildId(getId());
                    guildRoleBean.setContribution(0);
                    guildRoleBean.setGuildPositionId(GuildRolePositionCode.COMMON_PEOPLE.getCode());
                    guildRoleBean.setId(GuildServiceProvider.guildRoleIdAuto.incrementAndGet());
                    //成员列表
                    peopleRwLock.writeLock().lock();
                    try {
                        getGuildRoleBeans().add(guildRoleBean);
                    }finally {
                        peopleRwLock.writeLock().unlock();
                    }
                    //删除申请记录
                    guildApplyBeans.remove(bean);
                    //对应角色
                    MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(bean.getRoleId());
                    if (role == null) {
                        // 角色不在线 直接插入数据库
                        MmoRolePOJO mmoRolePOJO = RoleMessageCache.getInstance().get(bean.getRoleId());
                        mmoRolePOJO.setGuildId(bean.getGuildId());
                        //数据库删除 更新
                        GuildServiceProvider.getInstance().updateRolePOJO(mmoRolePOJO);
                    } else {
                        GuildBean guildBean=this;
                        role.execute(() -> {
                            role.setGuildBean(guildBean);
                            role.updateItem(role.getId());
                        });
                        sendJoinResponse(role, true);
                    }
                    //数据库删除 更新
                    List<Integer> deleteList = new ArrayList<Integer>();
                    deleteList.add(bean.getId());
                    GuildBean guildBean = this;
                    //入库
                    GuildServiceProvider.getInstance().insertGuildRolePOJO(guildRoleBean);
                    GuildServiceProvider.getInstance().deleteApply(deleteList);
                    updateItem(guildBean.getId());
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 离开公会
     */
    public void leaveGuild(Integer roleId) throws RpgServerException {
        peopleRwLock.writeLock().lock();
        try{
            Iterator iterator = guildRoleBeans.iterator();
            while (iterator.hasNext()) {
                GuildRoleBean roleBean = (GuildRoleBean) iterator.next();
                if (roleId.equals(roleBean.getRoleId())) {
                    peopleNum = peopleNum - 1;
                    guildRoleBeans.remove(roleBean);
                    MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(roleId);
                    if (role == null) {
                        // 角色不在线 直接插入数据库
                        MmoRolePOJO mmoRolePOJO = RoleMessageCache.getInstance().get(roleId);
                        mmoRolePOJO.setGuildId(-1);
                        //数据库
                        GuildServiceProvider.getInstance().updateRolePOJO(mmoRolePOJO);
                    } else {
                        role.execute(() -> {
                            role.setGuildBean(null);
                            role.updateItem(role.getId());
                        });
                    }
                    GuildBean guildBean = this;
                    //数据库删除该人的记录
                    GuildServiceProvider.getInstance().deletePeople(roleBean.getRoleId());
                    updateItem(guildBean.getId());
                    break;
                }
            }
        }finally {
            peopleRwLock.writeLock().unlock();
        }
    }


    /**
     * 设置公会成员职位
     */
    public void setRolePosition(Integer toRoleId, Integer position) throws RpgServerException {
        //每次插入都删除申请过时或者
        //锁公会成员bean 防止成员离开 遍历成员出错
        peopleRwLock.readLock().lock();
        try{
            Iterator iterator = guildRoleBeans.iterator();
            while (iterator.hasNext()) {
                GuildRoleBean roleBean = (GuildRoleBean) iterator.next();
                if (toRoleId.equals(roleBean.getRoleId())) {
                    //锁单独成员bean 防止同时修改
                    synchronized (roleBean) {
                        roleBean.setGuildPositionId(position);
                        roleBean.updateItem(roleBean.getId());
                        break;
                    }
                }
            }
        }finally {
            peopleRwLock.readLock().unlock();
        }
    }

    /**
     * 返回玩家在公会的信息
     * @param roleId
     * @return
     */
    public GuildRoleBean getRoleGuildMsg(Integer roleId) {
        peopleRwLock.readLock().lock();
        try {
            Iterator iterator = guildRoleBeans.iterator();
            //每次插入都删除申请过时或者
            while (iterator.hasNext()) {
                GuildRoleBean roleBean = (GuildRoleBean) iterator.next();
                if (roleId.equals(roleBean.getRoleId())) {
                    return roleBean;
                }
            }
            return null;
        } finally {
            peopleRwLock.readLock().unlock();
        }
    }

    /**
     * 捐金币
     *
     * @param money
     */
    public void contributeMoney(Integer money) {
        moneyRwLock.writeLock().lock();
        try {
            Integer temp = money + getMoney();
            setMoney(temp);
            updateItem(getId());
        } finally {
            moneyRwLock.writeLock().unlock();
        }
    }

    /**
     * 拿出金币
     *
     * @param money
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    public void takeMoney(Integer money, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        moneyRwLock.writeLock().lock();
        try {
            Integer guildMoney = getMoney();
            if (money > guildMoney) {
                throw new RpgServerException(StateCode.FAIL, "仓库金币不足");
            }
            Integer temp = guildMoney - money;
            setMoney(temp);
            mmoSimpleRole.execute(new Runnable() {
                @Override
                public void run() {
                    mmoSimpleRole.setMoney(mmoSimpleRole.getMoney() + money);
                    mmoSimpleRole.updateItem(mmoSimpleRole.getId());
                }
            });
            updateItem(getId());
        } finally {
            moneyRwLock.writeLock().unlock();
        }
    }

    /**
     * description 是否已经存在该玩家的公会申请
     * @param roleId
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/29 15:18
     */
    public boolean constrainGuildApplyBean(Integer roleId) {
        synchronized (guildApplyBeans){
            Iterator iterator=guildApplyBeans.iterator();
            while (iterator.hasNext()){
                GuildApplyBean guildApplyBean= (GuildApplyBean) iterator.next();
                if (guildApplyBean.getRoleId().equals(roleId)){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 发送申请拒绝或者通过
     *
     * @param role
     * @param flag
     */
    public void sendJoinResponse(MmoSimpleRole role, boolean flag) {
        Channel channel = role.getChannel();
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GUILD_APPLY_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.ApplyResponse);
        GuildModel.ApplyResponse.Builder applyResponseBuilder = GuildModel.ApplyResponse.newBuilder().setSuccessFlag(flag);
        messageData.setApplyResponse(applyResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        NotificationUtil.sendMessage(channel,nettyResponse,messageData.build());
    }

    /**
     * 更新公会信息
     * @param id
     */
    @Override
    public void updateItem(Integer id) {
        if (getChangeFlag().compareAndSet(false,true)) {
            GuildBean guildBean=this;
            ScheduledThreadPoolUtil.addTask(() -> GuildServiceProvider.getInstance().updateGuildPOJO(guildBean));
        }
    }
}
