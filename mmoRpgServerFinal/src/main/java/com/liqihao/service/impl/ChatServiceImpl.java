package com.liqihao.service.impl;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.provider.ChatServiceProvider;
import com.liqihao.service.ChatService;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

/**
 * 聊天模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel ="ChatModel$ChatModelMessage")
public class ChatServiceImpl implements ChatService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_ALL_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToAllRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        String str=myMessage.getSendToAllRequest().getStr();
        ChatServiceProvider.getInstance().notifyObserver(mmoSimpleRole,str);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_ONE_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToOneRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        String str=myMessage.getSendToOneRequest().getStr();
        Integer roleId=myMessage.getSendToOneRequest().getRoleId();
        ChatServiceProvider.getInstance().notifyOne(roleId,mmoSimpleRole,str);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_TEAM_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToTeamRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        String str=myMessage.getSendToTeamRequest().getStr();
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            throw new RpgServerException(StateCode.FAIL,"当前角色不在组队状态");
        }
        ChatServiceProvider.getInstance().notifyTeam(teamId,mmoSimpleRole,str);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_SCENE_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToSceneRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        String str=myMessage.getSendToSceneRequest().getStr();
        ChatServiceProvider.getInstance().notifyScene(mmoSimpleRole,str);
    }
}
