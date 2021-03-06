package com.liqihao.cache;

import com.liqihao.cache.base.CommonsBeanCache;
import com.liqihao.pojo.baseMessage.NpcMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleNPC;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Npc实例Cache类
 * @author lqhao
 */
@Component
public class NpcMessageCache extends CommonsBeanCache<MmoSimpleNPC> {
    private static String npcMessage_file = "classpath:message/npcMessage.xlsx";
    private volatile static NpcMessageCache instance ;
    public static NpcMessageCache getInstance(){
        return instance;
    }
    public NpcMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<NpcMessage> npcMessageList= ExcelReaderUtil.readExcelFromFileName(npcMessage_file, NpcMessage.class);
        for (NpcMessage n:npcMessageList) {
            MmoSimpleNPC npc=new MmoSimpleNPC();
            npc.setId(n.getId());
            npc.setType(n.getType());
            npc.setTalk(n.getTalk());
            npc.setOnStatus(n.getOnStatus());
            npc.setName(n.getName());
            npc.setMmoSceneId(n.getMmoSceneId());
            npc.setStatus(n.getStatus());
            npc.setHp(n.getBlood());
            npc.setNowHp(n.getBlood());
            npc.setMp(n.getMp());
            npc.setNowMp(n.getMp());
            npc.setAttack(n.getAttack());
            npc.setBufferBeans(new CopyOnWriteArrayList<>());
            npc.setAddExp(n.getAddExp());
            concurrentHashMap.put(n.getId(),npc);
        }
    }
    private NpcMessageCache(ConcurrentHashMap<Integer,MmoSimpleNPC> map) {
        super(map);
    }

    public static void main(String[] args) throws IllegalAccessException, IOException, InstantiationException {
        NpcMessageCache npcMessageCache=new NpcMessageCache();
        npcMessageCache.init();
        System.out.print(npcMessageCache.concurrentHashMap.size());
    }
}
