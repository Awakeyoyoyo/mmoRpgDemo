package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.GuildModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.GuildService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
@Service
public class GuildServiceImpl implements GuildService {
    @Override
    public void createGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]创建成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void joinGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]申请成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void setGuildPosition(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]设置职位成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void outGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]你已离开该公会！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void contributeMoney(NettyResponse nettyResponse) throws InvalidProtocolBufferException {

    }

    @Override
    public void contributeArticle(NettyResponse nettyResponse) throws InvalidProtocolBufferException {

    }

    @Override
    public void getArticle(NettyResponse nettyResponse) throws InvalidProtocolBufferException {

    }

    @Override
    public void getMoney(NettyResponse nettyResponse) throws InvalidProtocolBufferException {

    }

    @Override
    public void getGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        List<GuildModel.GuildApplyDto> guildApplyDtosList = myMessage.getGetGuildApplyListResponse().getGuildApplyDtosList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]公会申请列表：");
        for (GuildModel.GuildApplyDto g : guildApplyDtosList) {
            System.out.println("[-][-]");
            System.out.println("[-][-]申请id：" + g.getId() + " 申请人id: " + g.getRoleId() + " 申请入姓名:" + g.getRoleName());
            System.out.println("[-][-]公会id：" + g.getGuildId() + " 公会名称：" + g.getGuildName());
            System.out.println("[-][-]创建时间：" + sdf.format(g.getCreateTime()) + " 失效时间：" + sdf.format(g.getEndTime()));
            System.out.println("[-][-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getGuildBean(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        GuildModel.GuildDto guildDto = myMessage.getGetGuildBeanResponse().getGuildDto();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]公会信息：");
        System.out.println("[-][-]");
        System.out.println("[-][-]公会id：" + guildDto.getId() + " 公会名称：" + guildDto.getName());
        System.out.println("[-][-]公会等级：" + guildDto.getLevel() + " 公会人数：" + guildDto.getPeopleNum());
        System.out.println("[-][-]公会会长id：" + guildDto.getChairmanId());
        System.out.println("[-][-]创建时间：" + sdf.format(guildDto.getCreateTime()));
        System.out.println("[-][-]");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void agreeGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已同意该申请！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void refuseGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        GuildModel.GuildModelMessage myMessage;
        myMessage = GuildModel.GuildModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已拒绝该申请！");
        System.out.println("[-]--------------------------------------------------------");
    }
}