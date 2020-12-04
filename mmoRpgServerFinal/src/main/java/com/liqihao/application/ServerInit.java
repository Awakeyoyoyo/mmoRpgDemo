package com.liqihao.application;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.RoleOnStatusCode;
import com.liqihao.commons.RoleStatusCode;
import com.liqihao.commons.RoleTypeCode;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ThreadPools;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerInit{
    @Autowired
    private MmoScenePOJOMapper scenePOJOMapper;
    @Autowired
    private MmoRolePOJOMapper rolePOJOMapper;
    private ApplicationContext applicationContext;
    public void init() {
        //初始化线程池
        ThreadPools.init();
        List<MmoScenePOJO> mmoScenePOJOS = scenePOJOMapper.selectAll();
        //分解出可用scene
        ConcurrentHashMap<Integer,MmoScene> mmoScenes=new ConcurrentHashMap<Integer,MmoScene>();
        ConcurrentHashMap<Integer,MmoRolePOJO> mmoRoles=new ConcurrentHashMap<Integer,MmoRolePOJO>();
        for (MmoScenePOJO mmoScenePOJO : mmoScenePOJOS) {
            MmoScene mmoScene=new MmoScene();
            mmoScene.setId(mmoScenePOJO.getId());
            mmoScene.setPlaceName(mmoScenePOJO.getPlacename());
            //构建场景可通的下一个场景
            List<Integer> canScene=CommonsUtil.split(mmoScenePOJO.getCanscene());
            List<MmoSimpleScene> mmoSimpleScenes=new ArrayList<>();
            for (Integer id: canScene) {
                MmoSimpleScene mmoSimpleScene=new MmoSimpleScene();
                mmoSimpleScene.setId(id);
                for (MmoScenePOJO temp : mmoScenePOJOS) {
                    if (temp.getId().equals(id)) {
                        mmoSimpleScene.setPalceName(temp.getPlacename());
                        break;
                    }
                }
                mmoSimpleScenes.add(mmoSimpleScene);
            }
            mmoScene.setCanScene(mmoSimpleScenes);
            //构建场景中的角色
            List<Integer> roles= CommonsUtil.split(mmoScenePOJO.getRoles());
            List<MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
            for (Integer id:roles){
                MmoRolePOJO mmoRolePOJO=rolePOJOMapper.selectByPrimaryKey(id);
                if (mmoRolePOJO.getOnstatus().equals(RoleOnStatusCode.ONLINE.getCode())) {
                    MmoSimpleRole mmoSimpleRole = new MmoSimpleRole();
                    mmoSimpleRole.setId(mmoRolePOJO.getId());
                    mmoSimpleRole.setName(mmoRolePOJO.getName());
                    mmoSimpleRole.setOnstatus(RoleOnStatusCode.getValue(mmoRolePOJO.getOnstatus()));
                    mmoSimpleRole.setStatus(RoleStatusCode.getValue(mmoRolePOJO.getStatus()));
                    mmoSimpleRole.setType(RoleTypeCode.getValue(mmoRolePOJO.getType()));
                    mmoSimpleRoles.add(mmoSimpleRole);
                }
                mmoRoles.put(mmoRolePOJO.getId(), mmoRolePOJO);
            }
            mmoScene.setRoles(mmoSimpleRoles);
            mmoScenes.put(mmoScene.getId(),mmoScene);
        }
        //初始化缓存
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMap=new ConcurrentHashMap<>();
        MmoCache.init(mmoScenes,mmoRoles,scmMap,npcMap);
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));

    }

}
