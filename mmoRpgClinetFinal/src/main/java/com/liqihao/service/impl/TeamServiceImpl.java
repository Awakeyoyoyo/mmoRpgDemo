package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.ProfessionMessage;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.service.TeamService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeamServiceImpl implements TeamService {
    @Override
    public void teamMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.TeamMessageResponse messageResponse = myMessage.getTeamMessageResponse();
        ConcurrentHashMap<Integer, ProfessionMessage> p=MmoCacheCilent.getInstance().getProfessionMessageConcurrentHashMap();
        TeamModel.TeamBeanDto teamBeanDto = messageResponse.getTeamBeanDto();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]队伍的id： " + teamBeanDto.getTeamId() + " 队伍的名称： " + teamBeanDto.getTeamName());
        System.out.println("[-]队长的id： " + teamBeanDto.getLeaderId());
        System.out.println("[-]该队伍所关联的副本的实例id： " + teamBeanDto.getCopySceneBeanId());
        System.out.println("[-][-]队内成员： ");
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        for (TeamModel.RoleDto r : teamBeanDto.getRoleDtosList()) {
            if (mmoRole.getId().equals(r.getId())){
                mmoRole.setTeamId(r.getTeamId());
            }
            System.out.println("[-]");
            System.out.println("[-][-]角色id： " + r.getId() + "角色名称： " + r.getName() +"职业："+p.get(r.getProfessionId()).getName()+ " 所在队伍id： " + r.getTeamId());
            System.out.println("[-][-]Hp:" + r.getNowHp() + "/" + r.getHp());
            System.out.println("[-][-]Mp:" + r.getNowMP() + "/" + r.getMp());
            System.out.println("[-][-]等级："+r.getLevel());
            System.out.println("[-][-]装备总等级："+r.getEquipmentLevel());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void applyForTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.ApplyInviteBeanDto applyInviteBeanDto = myMessage.getApplyForTeamResponse().getApplyInviteBeanDtos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Date create = new Date(applyInviteBeanDto.getCreateTime());
        String createStr = sdf.format(create);
        Date end = new Date(applyInviteBeanDto.getEndTime());
        String endStr = sdf.format(end);
        System.out.println("[-]---------------------------------------------------------");
        System.out.println("[-]玩家id： " + applyInviteBeanDto.getRoleId() + " 申请加入 " + applyInviteBeanDto.getTeamName() + " 队伍" +
               " 申请的队伍id： " + applyInviteBeanDto.getTeamId() );
        System.out.println("[-]申请时间："+createStr);
        System.out.println("[-]该申请过期时间:" + endStr);
        System.out.println("[-]---------------------------------------------------------");
    }

    @Override
    public void invitePeopleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.ApplyInviteBeanDto inviteBeanDto = myMessage.getInvitePeopleResponse().getApplyInviteBeanDtos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Date create = new Date(inviteBeanDto.getCreateTime());
        String createStr = sdf.format(create);
        Date end = new Date(inviteBeanDto.getEndTime());
        String endStr = sdf.format(end);
        System.out.println("[-]---------------------------------------------------------");
        System.out.println("[-]"+inviteBeanDto.getTeamName() + " 队伍邀请你加入" + " 你的角色id：" + inviteBeanDto.getRoleId() + " 邀请的队伍id： "
                + inviteBeanDto.getTeamId() );
        System.out.println("[-]邀请时间："+createStr);
        System.out.println("[-]该邀请过期时间:" + endStr);
        System.out.println("[-]---------------------------------------------------------");
    }

    @Override
    public void applyMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        List<TeamModel.ApplyInviteBeanDto> applyInviteBeanDtos = myMessage.getApplyMessageResponse().getApplyInviteBeanDtosList();
        System.out.println("[-]--------------------------------------------------------");
        for (TeamModel.ApplyInviteBeanDto applyInviteBeanDto : applyInviteBeanDtos) {
            Date create = new Date(applyInviteBeanDto.getCreateTime());
            String createStr = sdf.format(create);
            Date end = new Date(applyInviteBeanDto.getEndTime());
            String endStr = sdf.format(end);
            System.out.println("[-]");
            System.out.println("[-]玩家id： " + applyInviteBeanDto.getRoleId() + " 申请加入 " + applyInviteBeanDto.getTeamName() + " 队伍" +
                    " 申请的队伍id： " + applyInviteBeanDto.getTeamId() );
            System.out.println("[-]申请时间："+createStr);
            System.out.println("[-]该申请过期时间:" + endStr);
            System.out.println("[-]");
        }
        System.out.println("[-]---------------------------------------------------------");
    }

    @Override
    public void inviteMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        List<TeamModel.ApplyInviteBeanDto> inviteBeanDtos = myMessage.getInviteMessageResponse().getApplyInviteBeanDtosList();
        System.out.println("[-]--------------------------------------------------------");
        for (TeamModel.ApplyInviteBeanDto inviteBeanDto : inviteBeanDtos) {
            Date create = new Date(inviteBeanDto.getCreateTime());
            String createStr = sdf.format(create);
            Date end = new Date(inviteBeanDto.getEndTime());
            String endStr = sdf.format(end);
            System.out.println("[-]");
            System.out.println("[-]"+inviteBeanDto.getTeamName() + " 队伍邀请你加入" + " 你的角色id：" + inviteBeanDto.getRoleId() + " 邀请的队伍id： "
                    + inviteBeanDto.getTeamId() );
            System.out.println("[-]邀请时间："+createStr);
            System.out.println("[-]该邀请过期时间:" + endStr);
            System.out.println("[-]");
        }
        System.out.println("[-]---------------------------------------------------------");
    }

    @Override
    public void refuseInviteResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        String roleName=myMessage.getRefuseInviteResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]用户： "+roleName+" 含泪拒绝了你的队伍邀请");
        System.out.println("[-]--------------------------------------------------------");

    }

    @Override
    public void refuseApplyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        String teamName=myMessage.getRefuseApplyResponse().getTeamName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]队伍： "+teamName+" 含泪拒绝了你的队伍申请");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void exitTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.RoleDto r = myMessage.getExitTeamResponse().getRoleDto();
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        if (mmoRole.getId().equals(r.getId())){
            mmoRole.setTeamId(r.getTeamId());
        }
        System.out.println("[-]--------------------------------------------------------");

        System.out.println("[-][-]角色id： " + r.getId() + "角色名称： " + r.getName() + " 所在队伍id： " + r.getTeamId());
        System.out.println("[-][-]等级："+r.getLevel());
        System.out.println("[-][-]装备总等级："+r.getEquipmentLevel());
        System.out.println("[-][-]Hp:" + r.getNowHp() + "/" + r.getHp());
        System.out.println("[-][-]Mp:" + r.getNowMP() + "/" + r.getMp());
        System.out.println("[-]默默地离开了当前队伍");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void entryPeopleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);

        TeamModel.RoleDto r = myMessage.getEntryPeopleResponse().getRoleDto();
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        if (mmoRole.getId().equals(r.getId())){
            mmoRole.setTeamId(r.getTeamId());
        }
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-][-]角色id： " + r.getId() + "角色名称： " + r.getName() + " 所在队伍id： " + r.getTeamId());
        System.out.println("[-][-]等级："+r.getLevel());
        System.out.println("[-][-]装备总等级："+r.getEquipmentLevel());
        System.out.println("[-][-]Hp:" + r.getNowHp() + "/" + r.getHp());
        System.out.println("[-][-]Mp:" + r.getNowMP() + "/" + r.getMp());
        System.out.println("[-]默默地加入了当前队伍");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void leaderTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        Integer teamId=myMessage.getLeaderTeamResponse().getTeamId();
        String teamName=myMessage.getLeaderTeamResponse().getTeamName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]恭喜 兄弟 你已经成为了队伍id: "+teamId+" 队伍名："+teamName+"的队长");
        System.out.println("[-]--------------------------------------------------------");

    }

    @Override
    public void deleteTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        mmoRole.setTeamId(null);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]唏嘘，你的队伍已经解散");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void banPeopleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        mmoRole.setTeamId(null);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]兄弟，你已经被队长无情强制踢出队伍");
        System.out.println("[-]--------------------------------------------------------");
    }
}
