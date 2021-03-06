package com.liqihao.pojo.bean.articleBean;

import com.liqihao.cache.EquipmentMessageCache;
import com.liqihao.cache.base.MmoBaseMessageCache;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.oneBestEquipmentTask.OneBestEquipmentAction;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.util.DbUtil;

/**
 * Equipment Bean
 * @author Administrator
 */
public class EquipmentBean extends Article{
    /**
     * 耐久度
     */
    private Integer nowDurability;
    /**
     * 装备数据库id
     */
    private Integer equipmentId;
    /**
     * 装备栏 数据库id
     */
    private Integer equipmentBagId;


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

    public Integer getNowDurability() {
        return nowDurability;
    }

    public void setNowDurability(Integer nowDurability) {
        this.nowDurability = nowDurability;
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


    @Override
    public Integer getArticleTypeCode() {
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getArticleMessageId());
        return equipmentMessage.getArticleType();
    }

    /**
     * description 修复耐久
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/29 16:17
     */
    public Integer fixDurability(){
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getArticleMessageId());
        nowDurability=equipmentMessage.getDurability();
        return nowDurability;
    }


    @Override
    public Article useOrAbandon(Integer number, BackPackManager backPackManager,Integer roleId) {
        //需要删除数据库的记录
        if (number>1){
            return null;
        }
        Integer bagId=getBagId();
        setBagId(null);
        backPackManager.getBackpacks().remove(this);
        backPackManager.setNowSize(backPackManager.getNowSize()-1);
        //数据库
        DbUtil.deleteBagById(bagId);
        return this;
    }


    @Override
    public ArticleDto getArticleMessage() {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setArticleId(getArticleId());
        articleDto.setId(getArticleMessageId());
        articleDto.setArticleType(getArticleTypeCode());
        articleDto.setQuantity(getQuantity());
        articleDto.setBagId(getBagId());
        articleDto.setNowDurability(getNowDurability());
        articleDto.setEquipmentId(getEquipmentId());
        articleDto.setWareHouseId(getWareHouseId());
        articleDto.setWareHouseDBId(getWareHouseDBId());
        articleDto.setDealArticleId(getDealArticleId());
        return articleDto;
    }


    @Override
    public <T extends Article> T getArticle() {
        return (T)this;
    }


    @Override
    public boolean put(BackPackManager backPackManager,Integer roleId) {
        //判断背包大小
        if ((backPackManager.getSize() - backPackManager.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //设置背包物品id
            setArticleId(backPackManager.getNewArticleId());
            backPackManager.setNowSize(backPackManager.getNowSize()+1);
            backPackManager.getBackpacks().add(this);
            setBagId(DbUtil.getBagPojoNextIndex());
            //数据库
            ArticleDto articleDto=new ArticleDto();
            articleDto.setQuantity(getQuantity());
            articleDto.setEquipmentId(getEquipmentId());
            articleDto.setArticleType(getArticleTypeCode());
            articleDto.setBagId(getBagId());
            DbUtil.insertBag(articleDto,roleId);
            return true;
        }
    }


    @Override
    public void clearPut(BackPackManager backPackManager,Integer roleId) {
        Integer oldBagId=getBagId();
        //设置背包物品id
        setArticleId(backPackManager.getNewArticleId());
        backPackManager.put(this,roleId);
        //数据库
        ArticleDto articleDto=new ArticleDto();
        articleDto.setQuantity(getQuantity());
        articleDto.setEquipmentId(getEquipmentId());
        articleDto.setArticleType(getArticleTypeCode());
        articleDto.setBagId(getBagId());

        DbUtil.deleteBagById(oldBagId);

    }

    @Override
    public boolean checkCanPut(BackPackManager backPackManager) {
        if (backPackManager.getNowSize() > backPackManager.getSize()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole) {
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getArticleMessageId());
        //判断该位置是否有装备
        EquipmentBean oldBean = mmoSimpleRole.getEquipmentBeanHashMap().get(equipmentMessage.getPosition());
        synchronized (backpackManager) {
            if (oldBean != null) {
                //放回背包内
                //背包新增数据
                //修改人物属性
                mmoSimpleRole.setAttack(mmoSimpleRole.getAttack() - equipmentMessage.getAttackAdd());
                mmoSimpleRole.setDamageAdd(mmoSimpleRole.getDamageAdd() - equipmentMessage.getDamageAdd());
                Integer oldEquipmentBagId=oldBean.getEquipmentBagId();
                oldBean.setEquipmentBagId(null);
                //入库
                DbUtil.deleteEquipmentBagById(oldEquipmentBagId);
                backpackManager.put(oldBean,mmoSimpleRole.getId());
            }
            //背包减少装备
            backpackManager.useOrAbandonArticle(getArticleId(), 1,mmoSimpleRole.getId());
            //装备栏增加装备
            mmoSimpleRole.getEquipmentBeanHashMap().put(equipmentMessage.getPosition(), this);
            equipmentBagId=DbUtil.getEquipmentBagNextIndex();
            //修改人物装备星数
            Integer olderEquipmentLevel=mmoSimpleRole.getEquipmentLevel();
            if (oldBean!=null) {
                EquipmentMessage oldEquipmentMessage = EquipmentMessageCache.getInstance().get(oldBean.getArticleMessageId());
                olderEquipmentLevel=olderEquipmentLevel+equipmentMessage.getEquipmentLevel()-oldEquipmentMessage.getEquipmentLevel();
            }else{
                olderEquipmentLevel=olderEquipmentLevel+equipmentMessage.getEquipmentLevel();
            }
            mmoSimpleRole.changeEquipmentLevel(olderEquipmentLevel);
            //穿极品装备
            OneBestEquipmentAction oneBestEquipmentAction=new OneBestEquipmentAction();
            oneBestEquipmentAction.setTaskTargetType(TaskTargetTypeCode.BEST_EQUIPMENT.getCode());
            oneBestEquipmentAction.setEquipmentLevel(equipmentMessage.getEquipmentLevel());
            mmoSimpleRole.getTaskManager().handler(oneBestEquipmentAction,mmoSimpleRole);
            //插入数据库
            EquipmentBean equipmentBean=this;
            Integer roleId=mmoSimpleRole.getId();
            DbUtil.addEquipmentBagPOJO(equipmentBean,roleId);
            //人物属性
            mmoSimpleRole.setAttack(mmoSimpleRole.getAttack() + equipmentMessage.getAttackAdd());
            mmoSimpleRole.setDamageAdd(mmoSimpleRole.getDamageAdd() + equipmentMessage.getDamageAdd());
            return true;
        }
    }

    @Override
    public void clearPutWareHouse(WareHouseManager wareHouseManager, Integer guildId) {
        Integer wareHouseDBId=getWareHouseDBId();
        wareHouseManager.putWareHouse(this,guildId);
        //数据库
        DbUtil.deleteWareHouseById(wareHouseDBId);

    }

    @Override
    public boolean checkCanPutWareHouse(WareHouseManager wareHouseManager) {
        if (wareHouseManager.getNowSize() > wareHouseManager.getSize()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean putWareHouse(WareHouseManager wareHouseManager, Integer guildId) {
        //判断背包大小
        if ((wareHouseManager.getSize() - wareHouseManager.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //设置仓库id
            setWareHouseId(wareHouseManager.addAndReturnWareHouseId());
            setWareHouseDBId(DbUtil.getWareHouseIndex());
            //删除背包id
            Integer bagId=getBagId();
            setBagId(null);
            setArticleId(null);
            wareHouseManager.getBackpacks().add(this);
            wareHouseManager.addAndReturnNowSize();
            //数据库
            ArticleDto articleDto=new ArticleDto();
            articleDto.setQuantity(getQuantity());
            articleDto.setEquipmentId(getEquipmentId());
            articleDto.setArticleType(getArticleTypeCode());
            articleDto.setWareHouseDBId(getWareHouseDBId());
            //入库
            DbUtil.insertEquipmentWareHouse(articleDto,guildId);
            DbUtil.deleteBagById(bagId);
            return true;
        }
    }

    @Override
    public Article useOrAbandonWareHouse(Integer number, WareHouseManager wareHouseManager,Integer guildId) {
        //需要删除数据库的记录
        Integer wareHouseDBId=getWareHouseDBId();
        setWareHouseDBId(null);
        wareHouseManager.getBackpacks().remove(this);
        wareHouseManager.reduceAndReturnNowSize();
        //数据库
        DbUtil.deleteWareHouseById(wareHouseDBId);
        return this;
    }

    @Override
    public boolean putDealBean(DealArticleBean dealArticleBean) {
        //判断交易栏大小
        if ((dealArticleBean.getSize() - dealArticleBean.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //删除背包id
            setBagId(null);
            setArticleId(null);
            dealArticleBean.getArticles().add(this);
            setDealArticleId(dealArticleBean.addAndReturnDealArticleId());
            dealArticleBean.addAndReturnNowSize();
            return true;
        }
    }

    @Override
    public Article abandonDealBean(Integer number,DealArticleBean dealArticleBean) {
        setDealArticleId(null);
        dealArticleBean.getArticles().remove(this);
        dealArticleBean.reduceAndReturnNowSize();
        return this;
    }

    @Override
    public DealBankArticleBean convertDealBankArticleBean() {
        DealBankArticleBean dealBankArticleBean=new DealBankArticleBean();
        dealBankArticleBean.setArticleMessageId(getArticleMessageId());
        dealBankArticleBean.setArticleType(getArticleTypeCode());
        dealBankArticleBean.setEquipmentId(getEquipmentId());
        dealBankArticleBean.setNum(getQuantity());
        return dealBankArticleBean;
    }

}
