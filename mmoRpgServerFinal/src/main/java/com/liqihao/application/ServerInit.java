package com.liqihao.application;

import com.liqihao.Cache.MmoCache;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.liqihao.util.YmlUtils;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    public void init() throws FileNotFoundException {
//        //初始化线程池
        //分解出可用scene
        //初始化缓存
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMap=new ConcurrentHashMap<>();
        //读取配置文件
        BaseRoleMessage baseRoleMessage=YmlUtils.getBaseRoleMessage();
        List<SceneMessage> sceneMessages=YmlUtils.getSceneMessage();
        for (SceneMessage s:sceneMessages) {
            scmMap.put(s.getId(),s);
        }
        for (NPCMessage n:YmlUtils.getNpcMessage()) {
            MmoSimpleNPC npc=new MmoSimpleNPC();
            npc.setId(n.getId());
            npc.setType(n.getType());
            npc.setTalk(n.getTalk());
            npc.setOnstatus(n.getOnstatus());
            npc.setName(n.getName());
            npc.setMmosceneid(n.getMmosceneid());
            npc.setStatus(n.getStatus());
            npc.setBlood(n.getBlood());
            npc.setNowBlood(n.getBlood());
            npc.setMp(n.getMp());
            npc.setNowMp(n.getMp());
            npc.setAttack(n.getAttack());
            npc.setBufferBeans(new CopyOnWriteArrayList<>());
            npcMap.put(n.getId(),npc);
        }
        //初始化NPC 和场景
        MmoCache.init(scmMap,npcMap);
        MmoCache.getInstance().setBaseRoleMessage(baseRoleMessage);
        ConcurrentHashMap<Integer, SceneBean> sceneBeanConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=new ConcurrentHashMap<>();
        //场景今昔
        for (SceneMessage m:sceneMessages){
            SceneBean sceneBean;
            sceneBean=CommonsUtil.sceneMessageToSceneBean(m);
            sceneBeanConcurrentHashMap.put(sceneBean.getId(),sceneBean);
        }
        //技能信息
        List<SkillMessage> skillMessages=YmlUtils.getSkillMessage();
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }
        //buffer信息
        List<BufferMessage> bufferMessage=YmlUtils.getBufferMessage();
        for (BufferMessage b:bufferMessage) {
            bufferMessageConcurrentHashMap.put(b.getId(),b);
        }
        //药品信息
        List<MedicineMessage> medicineMessages=YmlUtils.getMedicineMessages();
        for (MedicineMessage medicineMessage:medicineMessages) {
            medicineMessageConcurrentHashMap.put(medicineMessage.getId(),medicineMessage);
        }
        //装备信息
        List<EquipmentMessage> equipmentMessages=YmlUtils.getEquipmentMessages();
        for (EquipmentMessage equipmentMessage:equipmentMessages) {
            equipmentMessageConcurrentHashMap.put(equipmentMessage.getId(),equipmentMessage);
        }
        MmoCache.getInstance().setSkillMessageConcurrentHashMap(skillMessageConcurrentHashMap);
        MmoCache.getInstance().setBufferMessageConcurrentHashMap(bufferMessageConcurrentHashMap);
        MmoCache.getInstance().setEquipmentMessageConcurrentHashMap(equipmentMessageConcurrentHashMap);
        MmoCache.getInstance().setMedicineMessageConcurrentHashMap(medicineMessageConcurrentHashMap);
        MmoCache.getInstance().setSceneBeanConcurrentHashMap(sceneBeanConcurrentHashMap);
        ScheduledThreadPoolUtil.init();
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));

    }

}
