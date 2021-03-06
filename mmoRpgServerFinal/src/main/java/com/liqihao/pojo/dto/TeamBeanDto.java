package com.liqihao.pojo.dto;

import java.util.List;

/**
 * 简单队伍传输类
 * @author lqhao
 */
public class TeamBeanDto {
    Integer teamId;
    List<RoleDto> roleDtos;
    Integer leaderId;
    String teamName;
    Integer copySceneId;
    Integer copySceneBeanId;
    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }

    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public List<RoleDto> getRoleDtos() {
        return roleDtos;
    }

    public void setRoleDtos(List<RoleDto> roleDtos) {
        this.roleDtos = roleDtos;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
