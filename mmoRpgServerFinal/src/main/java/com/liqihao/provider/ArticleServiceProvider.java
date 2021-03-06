package com.liqihao.provider;


import com.liqihao.cache.EquipmentMessageCache;
import com.liqihao.cache.MedicineMessageCache;
import com.liqihao.dao.MmoEquipmentPOJOMapper;
import com.liqihao.pojo.MmoEquipmentPOJO;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 掉落物品提供类
 * @author lqhao
 */
@Component
public class ArticleServiceProvider implements ApplicationContextAware {
    private final Logger log = LoggerFactory.getLogger(EmailServiceProvider.class);

    MmoEquipmentPOJOMapper equipmentPOJOMapper;
    /**
     * 增添集合存储所有武器实例
     */
    private static ConcurrentHashMap<Integer,EquipmentBean> equipmentBeanConcurrentHashMap=new ConcurrentHashMap<>();

    /**
     * 自增id
     */
    private static AtomicInteger equipmentBeanIdAuto;
    private static Integer id;
    private ArticleServiceProvider() {

    }

    @Autowired
    public void setEquipmentPOJOMapper(MmoEquipmentPOJOMapper equipmentPOJOMapper) {
        this.equipmentPOJOMapper = equipmentPOJOMapper;
    }

    public static ConcurrentHashMap<Integer, EquipmentBean> getEquipmentBeanConcurrentHashMap() {
        return equipmentBeanConcurrentHashMap;
    }
    public static void setEquipmentBeanConcurrentHashMap(ConcurrentHashMap<Integer, EquipmentBean> equipmentBeanConcurrentHashMap) {
        ArticleServiceProvider.equipmentBeanConcurrentHashMap = equipmentBeanConcurrentHashMap;
    }

    public static Integer getId() {
        return id;
    }

    public static void setId(Integer id) {
        ArticleServiceProvider.id = id;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Integer index = equipmentPOJOMapper.selectNextIndex();
        equipmentBeanIdAuto = new AtomicInteger(index);
        id = index - 1;
        List<MmoEquipmentPOJO> equipmentPoJos=equipmentPOJOMapper.selectAll();
        for (MmoEquipmentPOJO equipmentPoJo : equipmentPoJos) {
            EquipmentMessage equipmentMessage=EquipmentMessageCache.getInstance().get(equipmentPoJo.getMessageId());
            EquipmentBean equipmentBean=CommonsUtil.equipmentMessageToEquipmentBean(equipmentMessage);
            equipmentBean.setEquipmentId(equipmentPoJo.getId());
            equipmentBeanConcurrentHashMap.put(equipmentBean.getEquipmentId(),equipmentBean);
        }
        log.info("EmailServiceProvider：数据库下一个主键index:" + index + " 之前有id：" + id);
    }
    /**
     * 生成药品到副本
     * @param copySceneBean
     * @param medicineIds
     * @return
     */
    public  static List<MedicineBean> productMedicineToCopyScene(CopySceneBean copySceneBean, List<Integer> medicineIds){
        List<MedicineBean> medicineBeans=new ArrayList<>();
        for (Integer id:medicineIds) {
            MedicineBean medicineBean= productMedicine(id);
            medicineBean.setFloorIndex(copySceneBean.getFloorIndex());
            medicineBean.setQuantity(1);
            medicineBeans.add(medicineBean);
        }
        return medicineBeans;
    }

    /**
     * 生成装备到副本
     * @param copySceneBean
     * @param equipmentIds
     * @return
     */
    public static List<EquipmentBean> productEquipmentToCopyScene(CopySceneBean copySceneBean, List<Integer> equipmentIds){
        List<EquipmentBean> equipmentBeans=new ArrayList<>();
        for (Integer id:equipmentIds) {
            EquipmentBean equipmentBean=productEquipment(id);
            equipmentBean.setFloorIndex(copySceneBean.getFloorIndex());
            equipmentBeans.add(equipmentBean);
        }
        return equipmentBeans;
    }

    /**
     * 生产装备
     * @param equipmentId
     * @return
     */
    public static EquipmentBean productEquipment(Integer equipmentId){
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(equipmentId);
        EquipmentBean equipmentBean= CommonsUtil.equipmentMessageToEquipmentBean(equipmentMessage);
        equipmentBean.setEquipmentId(equipmentBeanIdAuto.incrementAndGet());
        equipmentBean.setNowDurability(equipmentMessage.getDurability());
        equipmentBean.setQuantity(1);
        equipmentBeanConcurrentHashMap.put(equipmentBean.getEquipmentId(),equipmentBean);
        DbUtil.insertEquipment(equipmentBean);
        return equipmentBean;
    }

    /**
     * 生成药品
     * @param medicineId
     * @return
     */
    public static MedicineBean productMedicine(Integer medicineId){
        MedicineMessage medicineMessage= MedicineMessageCache.getInstance().get(medicineId);
        MedicineBean medicineBean= CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
        return medicineBean;
    }
}
