package com.liqihao.pojo.bean;

import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.MmoBaseMessageCache;
import com.liqihao.pojo.baseMessage.EquipmentMessage;

/**
 * Equipment Bean
 * @author Administrator
 */
public class EquipmentBean implements Article{
    /**
     * 装备信息Id
     */
    private Integer equipmentMessageId;
    private Integer nowDurability;
    private Integer quantity;
    /**
     * 缓存中背包id
     */
    private Integer articleId;
    /**
     * 背包数据库 数据库行记录id
     */
    private Integer bagId;
    /**
     * 装备数据库id
     */
    private Integer equipmentId;
    /**
     * 装备栏 数据库id
     */
    private Integer equipmentBagId;

    /**
     *
     *地面物品的下标
     */
    private Integer floorIndex;

    public Integer getEquipmentMessageId() {
        return equipmentMessageId;
    }

    public void setEquipmentMessageId(Integer equipmentMessageId) {
        this.equipmentMessageId = equipmentMessageId;
    }

    public Integer getFloorIndex() {
        return floorIndex;
    }

    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
    }

    public Integer getEquipmentBagId() {
        return equipmentBagId;
    }

    public void setEquipmentBagId(Integer equipmentBagId) {
        this.equipmentBagId = equipmentBagId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getBagId() {
        return bagId;
    }

    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }

    /**
     * 减耐久度
     * @return
     */
    public boolean reduceDurability(){
        Integer num= MmoBaseMessageCache.getInstance().getBaseDetailMessage().getReduceDurability();
        if (nowDurability>num){
            nowDurability-=num;
            return true;
        }
        return false;
    }

    /**
     * 修复
     * @return
     */
    public Integer fixDurability(){
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getEquipmentMessageId());
        nowDurability=equipmentMessage.getDurability();
        return nowDurability;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }
    public Integer getNowDurability() {
        return nowDurability;
    }

    public void setNowDurability(Integer nowDurability) {
        this.nowDurability = nowDurability;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Integer getArticleTypeCode() {
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getEquipmentMessageId());
        return equipmentMessage.getArticleType();
    }
}
