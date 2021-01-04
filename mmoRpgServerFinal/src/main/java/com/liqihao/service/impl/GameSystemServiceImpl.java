package com.liqihao.service.impl;

import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.commons.StateCode;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.protobufObject.GameSystemModel;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 游戏系统模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "GameSystemModel$GameSystemModelMessage")
public class GameSystemServiceImpl implements com.liqihao.service.GameSystemService {
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Override
    @HandlerCmdTag(cmd = ConstantValue.NET_IO_OUTTIME,module = ConstantValue.GAME_SYSTEM_MODULE)
    public void netIoOutTime(GameSystemModel.GameSystemModelMessage myMessage, Channel channel) {
        //获取相同的channel对应的roleId 然后根据其删除缓存中的信息
        MmoSimpleRole role=CommonsUtil.getRoleByChannel(channel);
        //删除缓存中的信息
        if (role!=null) {
            //保存背包信息入数据库
            Integer roleId=role.getId();
            MmoSimpleRole mmoSimpleRole=OnlineRoleMessageCache.getInstance().get(roleId);
            CommonsUtil.equipmentIntoDataBase(mmoSimpleRole);
            CommonsUtil.bagIntoDataBase(mmoSimpleRole.getBackpackManager(),roleId);
            CommonsUtil.RoleInfoIntoDataBase(role);
            // 直接获取即可 父类
            MmoSimpleRole mmoRole= OnlineRoleMessageCache.getInstance().get(roleId);
            MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
            mmoRolePOJO.setId(mmoRole.getId());
            mmoRolePOJO.setMmosceneid(mmoRole.getMmoSceneId());
            mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
            mmoRolePOJO.setName(mmoRole.getName());
            mmoRolePOJO.setSkillIds(CommonsUtil.listToString(mmoRole.getSkillIdList()));
            mmoRolePOJO.setType(mmoRole.getType());
            mmoRolePOJO.setStatus(mmoRole.getStatus());
            OnlineRoleMessageCache.getInstance().remove(roleId);
            //修改数据库
            mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
            //删除缓存中角色
            OnlineRoleMessageCache.getInstance().remove(role.getId());
            SceneBeanMessageCache.getInstance().get(role.getMmoSceneId()).getRoles().remove(role.getId());
            //角色退出队伍
            if (mmoRole.getTeamId()!=null){
                TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(mmoRole.getTeamId());
                teamBean.exitPeople(mmoRole.getId());
            }
        }
        //用户下线
        NettyResponse response=new NettyResponse();
        response.setCmd(ConstantValue.OUT_RIME_RESPONSE);
        response.setStateCode(StateCode.SUCCESS);
        GameSystemModel.GameSystemModelMessage modelMessage=GameSystemModel.GameSystemModelMessage.newBuilder()
                .setDataType(GameSystemModel.GameSystemModelMessage.DateType.OutTimeResponse)
                .setOutTimeResponse(GameSystemModel.OutTimeResponse.newBuilder().setMessage("太久没活动了，服务器已断开连接").build()).build();
        response.setData(modelMessage.toByteArray());
        channel.writeAndFlush(response);
        return;
    }
}
