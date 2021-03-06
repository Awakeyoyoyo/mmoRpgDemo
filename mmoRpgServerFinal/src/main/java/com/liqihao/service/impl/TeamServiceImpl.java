package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.cache.ChannelMessageCache;
import com.liqihao.cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.teamFirstTask.TeamTaskAction;
import com.liqihao.pojo.bean.teamBean.TeamApplyOrInviteBean;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.TeamService;
import com.liqihao.util.NotificationUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


/**
 * 队伍模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "TeamModel$TeamModelMessage")
public class TeamServiceImpl implements TeamService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.APPLY_FOR_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void applyForTeamRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer teamId=myMessage.getApplyForTeamRequest().getTeamId();
        if (teamId==0){
            throw new RpgServerException(StateCode.FAIL,"请输入参数");
        }
        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId()!=null){
            throw new RpgServerException(StateCode.FAIL,"已经有队伍了");
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (teamBean==null){
            throw new RpgServerException(StateCode.FAIL,"该队伍已不存在");
        }
        Integer leaderId=teamBean.getLeaderId();
        Channel c=ChannelMessageCache.getInstance().get(leaderId);
        if (c==null){
            throw new RpgServerException(StateCode.FAIL,"队长已经离线");
        }
        TeamApplyOrInviteBean teamApplyBean=teamBean.applyTeam(mmoSimpleRole);
        //protobuf
        TeamModel.ApplyInviteBeanDto.Builder applyBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
        applyBeanBuilder.setTeamId(teamApplyBean.getTeamId()).setCreateTime(teamApplyBean.getCreateTime())
                .setEndTime(teamApplyBean.getEndTime()).setRoleId(teamApplyBean.getRoleId())
                .setTeamName(teamBean.getTeamName()).setType(teamApplyBean.getType());
        TeamModel.ApplyForTeamResponse applyForTeamResponse=TeamModel.ApplyForTeamResponse.newBuilder().setApplyInviteBeanDtos(applyBeanBuilder.build()).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.ApplyForTeamResponse);
        teamMessageBuilder.setApplyForTeamResponse(applyForTeamResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.APPLY_FOR_TEAM_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        NotificationUtil.sendMessage(c,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CREATE_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void createTeamRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        //判断是否在线 并且返回玩家对象
        Channel channel = mmoSimpleRole.getChannel();
        String teamName = myMessage.getCreateTeamRequest().getName();

        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId() != null) {
            throw new RpgServerException(StateCode.FAIL, "已经有队伍了");
        }
        TeamBean teamBean = TeamServiceProvider.createNewTeamBean(mmoSimpleRole, teamName);
        mmoSimpleRole.setTeamId(teamBean.getTeamId());
        //任务条件触发
        TeamTaskAction taskAction = new TeamTaskAction();
        taskAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_TEAM.getCode());
        mmoSimpleRole.getTaskManager().handler(taskAction, mmoSimpleRole);

        if (mmoSimpleRole.getMmoHelperBean() != null) {
            mmoSimpleRole.getMmoHelperBean().setTeamId(teamBean.getTeamId());
        }
        //ptotobuf
        TeamModel.RoleDto role = TeamModel.RoleDto.newBuilder().setId(mmoSimpleRole.getId())
                .setHp(mmoSimpleRole.getHp()).setMp(mmoSimpleRole.getMp())
                .setName(mmoSimpleRole.getName()).setNowHp(mmoSimpleRole.getNowHp())
                .setTeamId(mmoSimpleRole.getTeamId())
                .setProfessionId(mmoSimpleRole.getProfessionId())
                .setLevel(mmoSimpleRole.getLevel())
                .setEquipmentLevel(mmoSimpleRole.getEquipmentLevel())
                .setNowMP(mmoSimpleRole.getNowMp()).build();
        List<TeamModel.RoleDto> roles = new ArrayList<>();
        roles.add(role);
        TeamModel.TeamBeanDto teamBeanDto = TeamModel.TeamBeanDto.newBuilder().addAllRoleDtos(roles)
                .setLeaderId(teamBean.getLeaderId()).setTeamName(teamBean.getTeamName())
                .setTeamId(teamBean.getTeamId()).build();
        sendTeamMessageResponse(teamBeanDto, channel);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BAN_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void banPeopleRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer roleId=myMessage.getBanPeopleRequest().getRoleId();
        MmoSimpleRole player=OnlineRoleMessageCache.getInstance().get(roleId);
        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId()==null){
            throw new RpgServerException(StateCode.FAIL,"不在任何队伍中");
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"不是队长没有该权利");

        }
        if (player.getId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"不能自己踢除自己");
        }
        //判断玩家是否在队伍里
        if (player.getTeamId()==null||!player.getTeamId().equals(mmoSimpleRole.getTeamId())){
            throw new RpgServerException(StateCode.FAIL,"该玩家已不在队伍中");
        }
        teamBean.banPeople(roleId);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.DELETE_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void deleteTeamRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        //判断玩家是否已经有队伍
        Integer teamId=mmoSimpleRole.getTeamId();
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        //判断当前玩家是否有队伍
        if (teamBean==null){
            throw new RpgServerException(StateCode.FAIL,"当前角色无队伍");
        }
        //判断当前玩家是否为队长
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"不是队长，无权解散队伍");
        }
        //解散队伍
        //广播队伍已经解散信息
        teamBean.breakUp();
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ENTRY_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void entryPeopleRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        /**
         *  若当前channle的role与传入的roleId相等,则表示用户接受了该队伍的邀请
         *  若当前channle的role与传入的roleId不相等，则是队长同意该用户的申请，需要判断当前channel role是否为队长
         */
        Integer roleId=myMessage.getEntryPeopleRequest().getRoleId();
        Integer teamId=myMessage.getEntryPeopleRequest().getTeamId();
        Channel channel = mmoSimpleRole.getChannel();
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (roleId.equals(mmoSimpleRole.getId())){
            if (teamBean==null){
                throw new RpgServerException(StateCode.FAIL,"没有该队伍");
            }
            //玩家角色
            if (mmoSimpleRole.getTeamId()!=null){
                throw new RpgServerException(StateCode.FAIL,"已经有队伍了");
            }
            //用户接受队伍邀请
            //查看人物中是否有该请求
            TeamApplyOrInviteBean applyOrInviteBean=mmoSimpleRole.containInvite(teamId);
            if (applyOrInviteBean==null){
                throw new RpgServerException(StateCode.FAIL,"没有该邀请或者邀请已经过期");
            }

            // 此时传入的channel是用户的，mmo是玩家
            teamBean.addRole(mmoSimpleRole, channel);
            //用户删除该邀请
            mmoSimpleRole.getTeamApplyOrInviteBeans().remove(applyOrInviteBean);
        }else{
            //队长同意该用户进队伍
            if (teamBean==null){
                throw new RpgServerException(StateCode.FAIL,"请先创建队伍");
            }
            //获取玩家
            mmoSimpleRole=OnlineRoleMessageCache.getInstance().get(roleId);
            if (mmoSimpleRole.getTeamId()!=null){
                throw new RpgServerException(StateCode.FAIL,"已经有队伍了");
            }
            TeamApplyOrInviteBean inviteBean=teamBean.containsInvite(roleId);
            if (inviteBean==null){
                throw new RpgServerException(StateCode.FAIL,"该申请已经过期了");

            }
            // 此时传入的channel是队长，mmorole是玩家
            teamBean.addRole(mmoSimpleRole, channel);
            //队伍删除该申请
            teamBean.getTeamApplyOrInviteBeans().remove(inviteBean);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EXIT_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void exitTeamRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        //判断是否有队伍
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            throw new RpgServerException(StateCode.FAIL,"当前不在队伍中");
        }
        //离开队伍
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        teamBean.exitPeople(mmoSimpleRole.getId());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.INVITE_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void invitePeopleRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer roleId=myMessage.getInvitePeopleRequest().getRoleId();
        if (roleId==0){
            throw new RpgServerException(StateCode.FAIL,"请输入参数");
        }
        MmoSimpleRole inviteRole=OnlineRoleMessageCache.getInstance().get(roleId);
        if (inviteRole==null){
            throw new RpgServerException(StateCode.FAIL,"该用户不在线");
        }
        //判断是否有队伍
        if (mmoSimpleRole.getTeamId()==null){
            throw new RpgServerException(StateCode.FAIL,"请先创建队伍");
        }
        //判断是否有队伍
        if (inviteRole.getTeamId()!=null){
            throw new RpgServerException(StateCode.FAIL,"该用户已经有队伍了");
        }
        //判断是否是队长
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"你不是队长无法邀请玩家进队伍");
        }
        TeamApplyOrInviteBean inviteBean=teamBean.invitePeople(inviteRole);
        Channel c=ChannelMessageCache.getInstance().get(roleId);
        //protobuf
        TeamModel.ApplyInviteBeanDto.Builder inviteBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
        inviteBeanBuilder.setTeamId(inviteBean.getTeamId()).setCreateTime(inviteBean.getCreateTime())
                .setEndTime(inviteBean.getEndTime()).setRoleId(inviteBean.getRoleId())
                .setTeamName(teamBean.getTeamName()).setType(inviteBean.getType());
        TeamModel.InvitePeopleResponse invitePeopleResponse=TeamModel.InvitePeopleResponse.newBuilder().setApplyInviteBeanDtos(inviteBeanBuilder.build()).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.InvitePeopleResponse);
        teamMessageBuilder.setInvitePeopleResponse(invitePeopleResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.INVITE_PEOPLE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        NotificationUtil.sendMessage(c,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_APPLY_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void refuseApplyRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        //teamId和roleId和teamApplyId给队长
        //判断是否在线 并且返回玩家对象
        Integer roleId=myMessage.getRefuseApplyRequest().getRoleId();
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            throw new RpgServerException(StateCode.FAIL,"你根本无队伍");
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"你根本不是队长");
        }
        TeamApplyOrInviteBean bean=teamBean.refuseApply(roleId);
        if (bean==null) {
            return;
        }
        //protobuf
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.RefuseApplyResponse);
        teamMessageBuilder.setRefuseApplyResponse(TeamModel.RefuseApplyResponse.newBuilder().setTeamName(teamBean.getTeamName()));
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.REFUSE_APPLY_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        Channel c=ChannelMessageCache.getInstance().get(roleId);
        if (c==null) {
            return;
        }
        NotificationUtil.sendMessage(c,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_INVITE_REQUEST,module = ConstantValue.TEAM_MODULE)

    public void refuseInviteRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象
        Integer teamId=myMessage.getRefuseInviteRequest().getTeamId();
        TeamApplyOrInviteBean bean=mmoSimpleRole.refuseInvite(teamId);
        if (bean==null){
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        //protobuf
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.RefuseInviteResponse);
        teamMessageBuilder.setRefuseInviteResponse(TeamModel.RefuseInviteResponse.newBuilder().setRoleName(mmoSimpleRole.getName()));
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.REFUSE_INVITE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        Channel c=ChannelMessageCache.getInstance().get(teamBean.getLeaderId());
        if (c==null) {
            return;
        }
        NotificationUtil.sendMessage(c,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.APPLY_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void applyMessageRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();

        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            throw new RpgServerException(StateCode.FAIL,"当前角色无队伍");

        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"不是队长无权限获取队伍申请信息");
        }
        List<TeamApplyOrInviteBean> applyOrInviteBeans=teamBean.getTeamApplyBean();
        //发送给角色
        List<TeamModel.ApplyInviteBeanDto> applyInviteBeanDtos=new ArrayList<>();
        for (TeamApplyOrInviteBean e:applyOrInviteBeans) {
            TeamModel.ApplyInviteBeanDto.Builder applyBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
            applyBeanBuilder.setTeamId(e.getTeamId()).setCreateTime(e.getCreateTime())
                    .setEndTime(e.getEndTime()).setRoleId(e.getRoleId())
                    .setTeamName(teamBean.getTeamName()).setType(e.getType());
            applyInviteBeanDtos.add(applyBeanBuilder.build());
        }
        //protobuf
        TeamModel.ApplyMessageResponse applyMessageResponse=TeamModel.ApplyMessageResponse.newBuilder().addAllApplyInviteBeanDtos(applyInviteBeanDtos).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.ApplyMessageResponse);
        teamMessageBuilder.setApplyMessageResponse(applyMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.APPLY_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        NotificationUtil.sendMessage(channel,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.INVITE_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void inviteMessage(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = mmoSimpleRole.getChannel();
        List<TeamApplyOrInviteBean> inviteBeanList=mmoSimpleRole.getInviteBeans();
        //发送给角色
        List<TeamModel.ApplyInviteBeanDto> inviteBeanDtos=new ArrayList<>();
        for (TeamApplyOrInviteBean e:inviteBeanList) {
            TeamModel.ApplyInviteBeanDto.Builder inviteBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(e.getTeamId());
            inviteBeanBuilder.setTeamId(e.getTeamId()).setCreateTime(e.getCreateTime())
                    .setEndTime(e.getEndTime()).setRoleId(e.getRoleId())
                    .setTeamName(teamBean.getTeamName()).setType(e.getType());
            inviteBeanDtos.add(inviteBeanBuilder.build());
        }
        //protobuf
        TeamModel.InviteMessageResponse inviteMessageResponse=TeamModel.InviteMessageResponse.newBuilder().addAllApplyInviteBeanDtos(inviteBeanDtos).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.InviteMessageResponse);
        teamMessageBuilder.setInviteMessageResponse(inviteMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.INVITE_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //send
        NotificationUtil.sendMessage(channel,nettyResponse,teamMessageBuilder.build());
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.TEAM_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void teamMessageRequest(TeamModel.TeamModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            throw new RpgServerException(StateCode.FAIL,"没有进入任何队伍");
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        //protobuf
        List<TeamModel.RoleDto> roles=new ArrayList<>();
        for (MmoSimpleRole simpleRole:teamBean.getMmoSimpleRoles()) {
            TeamModel.RoleDto role = TeamModel.RoleDto.newBuilder().setId(simpleRole.getId())
                    .setHp(simpleRole.getHp()).setMp(simpleRole.getMp())
                    .setTeamId(mmoSimpleRole.getTeamId())
                    .setProfessionId(simpleRole.getProfessionId())
                    .setEquipmentLevel(simpleRole.getEquipmentLevel())
                    .setLevel(simpleRole.getLevel())
                    .setName(simpleRole.getName()).setNowHp(simpleRole.getNowHp())
                    .setNowMP(simpleRole.getNowMp()).build();
            roles.add(role);
        }
        TeamModel.TeamBeanDto teamBeanDto=TeamModel.TeamBeanDto.newBuilder().addAllRoleDtos(roles)
                .setLeaderId(teamBean.getLeaderId()).setTeamName(teamBean.getTeamName())
                .setTeamId(teamBean.getTeamId()).setCopySceneBeanId(teamBean.getCopySceneBeanId()==null?-1:teamBean.getCopySceneBeanId())
                .setCopySceneId(teamBean.getCopySceneId()==null?-1:teamBean.getCopySceneId()).build();
        //send
        sendTeamMessageResponse(teamBeanDto,channel);
    }

    /**
     * 发送队伍信息响应
     */
    public void sendTeamMessageResponse(TeamModel.TeamBeanDto teamBeanDto,Channel channel){
        TeamModel.TeamMessageResponse teamMessageResponse=TeamModel.TeamMessageResponse.newBuilder().setTeamBeanDto(teamBeanDto).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.TeamMessageResponse);
        teamMessageBuilder.setTeamMessageResponse(teamMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.TEAM_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        NotificationUtil.sendMessage(channel,nettyResponse,teamMessageBuilder.build());
    }
}
