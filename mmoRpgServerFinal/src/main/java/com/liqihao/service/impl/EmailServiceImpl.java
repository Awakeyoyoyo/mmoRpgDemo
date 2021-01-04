package com.liqihao.service.impl;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.MediceneMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.provider.EmailServiceProvider;
import com.liqihao.service.EmailService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件模块
 * @author lqhao
 */
public class EmailServiceImpl implements EmailService {
    @Override
    public void getEmailMessageRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getGetEmailMessageRequest().getEmailId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        MmoEmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"没有该id的邮件".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        EmailModel.EmailDto emailDto= CommonsUtil.mmoEmailBeanToEmailDto(mmoEmailBean);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailMessageResponse)
                .setGetEmailMessageResponse(EmailModel.GetEmailMessageResponse.newBuilder().setEmailDto(emailDto).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void getEmailArticleRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getGetEmailArticleRequest().getEmailId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //获取邮件详情
        MmoEmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"没有该id的邮件".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //判断邮件是否有物品
        if (!mmoEmailBean.getHasArticle()){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该邮件没有物品".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //根据邮件实体类信息初始化物品
        if (mmoEmailBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())){
            BackPackManager backPackManager=mmoSimpleRole.getBackpackManager();
            MedicineMessage medicineMessage= MediceneMessageCache.getInstance().get(mmoEmailBean.getArticleId());
            MedicineBean medicineBean=CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(mmoEmailBean.getArticleNum());
            if (backPackManager.put(medicineBean)){
                NettyResponse nettyResponse=new NettyResponse();
                nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
                nettyResponse.setStateCode(StateCode.FAIL);
                //protobuf 生成registerResponse
                nettyResponse.setData("背包已满".getBytes());
                channel.writeAndFlush(nettyResponse);
                return;
            }
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailArticleResponse)
                .setGetEmailArticleResponse(EmailModel.GetEmailArticleResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void acceptEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        List<MmoEmailBean> mmoEmailBeans=EmailServiceProvider.getToEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (MmoEmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.AcceptEmailListResponse)
                .setAcceptEmailListResponse(EmailModel.AcceptEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ACCEPT_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void isSendEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        List<MmoEmailBean> mmoEmailBeans=EmailServiceProvider.getToEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (MmoEmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.IsSendEmailListResponse)
                .setIsSendEmailListResponse(EmailModel.IsSendEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.IS_SEND_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void sendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer articleId=myMessage.getSendEmailRequest().getArticleId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        String context=myMessage.getSendEmailRequest().getContext();
        String title=myMessage.getSendEmailRequest().getTitle();
        Integer articleNum=myMessage.getSendEmailRequest().getArticleNum();
        Integer toRoleId=myMessage.getSendEmailRequest().getToRoleId();
        Integer articleType=myMessage.getSendEmailRequest().getArticleType();
        MmoSimpleRole toRole= OnlineRoleMessageCache.getInstance().get(toRoleId);
        MmoEmailBean mmoEmailBean=new MmoEmailBean();
        mmoEmailBean.setContext(context);
        mmoEmailBean.setTitle(title);
        mmoEmailBean.setArticleId(articleId);
        mmoEmailBean.setArticleNum(articleNum);
        mmoEmailBean.setArticleType(articleType);
        mmoEmailBean.setToRoleId(toRoleId);
        mmoEmailBean.setFromRoleId(mmoSimpleRole.getId());
        EmailServiceProvider.sendArticleEmail(mmoSimpleRole,toRole,mmoEmailBean);

        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.SendEmailResponse)
                .setSendEmailResponse(EmailModel.SendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void deleteAcceptEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteAcceptEmailRequest().getEmailId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        EmailServiceProvider.deleteAcceptEmail(mmoSimpleRole,emailId);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteAcceptEmailResponse)
                .setDeleteAcceptEmailResponse(EmailModel.DeleteAcceptEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_ACCEPT_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void deleteSendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteSendEmailRequest().getEmailId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        EmailServiceProvider.deleteIsSendEmail(mmoSimpleRole,emailId);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteSendEmailResponse)
                .setDeleteSendEmailResponse(EmailModel.DeleteSendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
