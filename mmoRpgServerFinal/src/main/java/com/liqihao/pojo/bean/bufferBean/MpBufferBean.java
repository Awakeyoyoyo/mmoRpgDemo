package com.liqihao.pojo.bean.bufferBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.ConsumeTypeCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;

/**
 * 印象蓝量的buffer
 * @author lqhao
 */
public class MpBufferBean extends BaseBufferBean {
    /**
     * -1 1判断是扣除还是增加
     */
    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public MpBufferBean() {
    }

    public MpBufferBean(Integer flag) {
        this.flag = flag;
    }

    @Override
    public void effectToPeople(Role toRole){
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
        PlayModel.RoleIdDamage.Builder builder = builderRoleDamage(toRole);
        toRole.changeMp(flag*bufferMessage.getBuffNum(), builder);
    }

    @Override
    public void effectToRole(Role toRole){
        toRole.mpRwLock.writeLock().lock();
        try {
            BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
            int mp = toRole.getNowMp() - bufferMessage.getBuffNum();
            if (mp <= 0) {
                mp = 0;
            }
            toRole.setNowMp(mp);
        } finally {
            toRole.mpRwLock.writeLock().unlock();
        }
        sendAllRoleDamage(toRole);
    }
}