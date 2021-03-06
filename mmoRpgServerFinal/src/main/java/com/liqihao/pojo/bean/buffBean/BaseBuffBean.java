package com.liqihao.pojo.bean.buffBean;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.cache.BufferMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.NotificationUtil;

/**
 * buffer Bean
 * @author lqhao
 */
public abstract class BaseBuffBean {
    private Integer fromRoleId;
    private Integer toRoleId;
    private Long createTime;
    private Integer fromRoleType;
    private Integer toRoleType;
    private Integer BufferMessageId;

    public Integer getBufferMessageId() {
        return BufferMessageId;
    }

    public void setBufferMessageId(Integer bufferMessageId) {
        BufferMessageId = bufferMessageId;
    }

    public Integer getFromRoleType() {
        return fromRoleType;
    }

    public void setFromRoleType(Integer fromRoleType) {
        this.fromRoleType = fromRoleType;
    }

    public Integer getToRoleType() {
        return toRoleType;
    }

    public void setToRoleType(Integer toRoleType) {
        this.toRoleType = toRoleType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Integer getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(Integer toRoleId) {
        this.toRoleId = toRoleId;
    }

    /**
     * 对人物的影响
     * @param toRole
     */
    public abstract void  effectToPeople(Role toRole);

    /**
     * 对角色的印象
     * @param toRole
     */
    public abstract void effectToRole(Role toRole,Role fromRole);

    /**
     * 广播buffer伤害信息
     * @param toRole
     */
    public void sendAllRoleDamage(Role toRole,PlayModel.RoleIdDamage.Builder damageU){
        //生成数据包
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播信息
        NotificationUtil.notificationSceneRole(nettyResponse,toRole,myMessageBuilder.build());
    }

    /**
     * 生成简单protobuf的buffer伤害类
     * @param toRole
     * @return
     */
    public  PlayModel.RoleIdDamage.Builder builderSimpleRoleDamage(Role toRole){
        //扣血类型
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getFromRoleId());
        damageU.setFromRoleType(getFromRoleType());
        damageU.setToRoleId(toRole.getId());
        damageU.setToRoleType(toRole.getType());
        damageU.setBufferId(getBufferMessageId());
        damageU.setSkillId(-1);
        return damageU;
    }

    /**
     * 生成protobuf的buffer伤害类
     * @param toRole
     * @return
     */
    public PlayModel.RoleIdDamage.Builder builderRoleDamage(Role toRole){
        BufferMessage bufferMessage=BufferMessageCache.getInstance().get(getBufferMessageId());
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setDamageType(DamageTypeCode.HP.getCode()).setAttackStyle(AttackStyleCode.BUFFER.getCode())
                .setDamage(bufferMessage.getBuffNum()).setFromRoleId(getFromRoleId()).setToRoleId(getToRoleId())
                .setState(toRole.getStatus()).setMp(toRole.getNowMp()).setBufferId(getBufferMessageId()).setNowblood(toRole.getNowHp());
        return damageU;
    }

}
