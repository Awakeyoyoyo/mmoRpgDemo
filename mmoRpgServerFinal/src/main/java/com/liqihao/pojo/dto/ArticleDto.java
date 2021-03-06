package com.liqihao.pojo.dto;

/**
 * 传输背包物品dto
 * @author lqhao
 */
public class ArticleDto {
    /**
     * 背包中物品id
     */
    private Integer articleId;
    /**
     *   物品id
     */
    private Integer id;
    /**
     *   数量
     */
    private Integer quantity;
    /**
     * 物品类型
     */
    private Integer articleType;
    /**
     *  数据库行id
     */

    private Integer bagId;
    /**
     *  当前耐久度
     */
    private Integer nowDurability;

    /**
     *
     * 武器实例id
     */
    private Integer equipmentId;

    /**
     * 仓库id
     */
    private Integer wareHouseId;

    /**
     * 仓库 数据库id
     */
    private Integer wareHouseDBId;
    /**
     * 交易栏id
     */
    private Integer dealArticleId;

    public Integer getDealArticleId() {
        return dealArticleId;
    }

    public void setDealArticleId(Integer dealArticleId) {
        this.dealArticleId = dealArticleId;
    }

    public Integer getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(Integer wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public Integer getWareHouseDBId() {
        return wareHouseDBId;
    }

    public void setWareHouseDBId(Integer wareHouseDBId) {
        this.wareHouseDBId = wareHouseDBId;
    }

    public Integer getNowDurability() {
        return nowDurability;
    }

    public void setNowDurability(Integer nowDurability) {
        this.nowDurability = nowDurability;
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

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }
}
