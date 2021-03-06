package com.liqihao.provider;

import com.liqihao.cache.GoodsMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品服务提供类
 * @author lqhao
 */
@Component
public class GoodsServiceProvider {
    private static ConcurrentHashMap<Integer, GoodsBean> goodsBeanConcurrentHashMap=new ConcurrentHashMap<>();
    @PostConstruct
    private  void init(){
        for (GoodsMessage g:GoodsMessageCache.getInstance().values()){
            GoodsBean goodsBean= CommonsUtil.goodMessageToGoodBean(g);
            goodsBeanConcurrentHashMap.put(goodsBean.getId(),goodsBean);
        }
    }

    /**
     * 获取所有商品列表
     * @return
     */
    public static List<GoodsBean> getAllArticles(){
        List<GoodsBean> beans = new ArrayList<>(goodsBeanConcurrentHashMap.values());
        return beans;
    }

    /**
     *  对外提供买东西接口
     */
    public static Article sellArticle(Integer goodsId, Integer num, MmoSimpleRole mmoSimpleRole) throws Exception {
        GoodsBean goodsBean=goodsBeanConcurrentHashMap.get(goodsId);
        if (goodsBean == null) {
            throw new RpgServerException(StateCode.FAIL,"查无该商品");

        }
        GoodsMessage goodsMessage=GoodsMessageCache.getInstance().get(goodsBean.getId());
        synchronized (goodsBean) {
            if (goodsBean.getNowNum()<=0){
                throw new RpgServerException(StateCode.FAIL,"该商品数量不足");
            }

            Integer needMoney=num*goodsMessage.getPrice();
            if (mmoSimpleRole.getMoney()<needMoney){
                throw new RpgServerException(StateCode.FAIL,"用户不够钱");

            }
            mmoSimpleRole.setMoney(mmoSimpleRole.getMoney()-needMoney);
            goodsBean.setNowNum(goodsBean.getNowNum()-num);
        }
        boolean flag=false;
        Article article=null;
        if(goodsMessage.getArticleTypeId().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            //装备
            article= sellEquipment(goodsMessage.getArticleMessageId());
        }else{
            //药品
            article= sellMedicineBean(goodsMessage.getArticleMessageId(),num);
        }
        flag = mmoSimpleRole.getBackpackManager().canPutArticle(article.getArticleMessageId(), article.getArticleTypeCode(), article.getQuantity());
        if (!flag) {
            //背包满了
            synchronized (goodsBean) {
                goodsBean.setNowNum(goodsBean.getNowNum() + num);
            }
            throw new RpgServerException(StateCode.FAIL,"背包已经满了");
        }
        mmoSimpleRole.getBackpackManager().put(article,mmoSimpleRole.getId());
        return  article;
    }

    /**
     * 卖药品
     */
    private static MedicineBean sellMedicineBean(Integer medicineId, Integer num){
        MedicineBean medicineBean=ArticleServiceProvider.productMedicine(medicineId);
        medicineBean.setQuantity(num);
        return medicineBean;
    }

    /**
     * 卖装备
     */
    private static EquipmentBean sellEquipment(Integer equipmentId){
       EquipmentBean equipmentBean= ArticleServiceProvider.productEquipment(equipmentId);
       return equipmentBean;
    }
}
