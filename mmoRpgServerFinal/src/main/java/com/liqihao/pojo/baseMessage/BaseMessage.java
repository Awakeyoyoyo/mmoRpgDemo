package com.liqihao.pojo.baseMessage;

import java.util.List;

public class BaseMessage {
    List<SceneMessage> sceneMessages;
    List<NPCMessage> npcMessages;
    List<SkillMessage> skillMessages;
    BufferMessage bufferMessage;
    BaseRoleMessage baseRoleMessage;

    public BufferMessage getBufferMessage() {
        return bufferMessage;
    }

    public void setBufferMessage(BufferMessage bufferMessage) {
        this.bufferMessage = bufferMessage;
    }

    public List<SkillMessage> getSkillMessages() {
        return skillMessages;
    }

    public void setSkillMessages(List<SkillMessage> skillMessages) {
        this.skillMessages = skillMessages;
    }


    public BaseRoleMessage getBaseRoleMessage() {
        return baseRoleMessage;
    }

    public void setBaseRoleMessage(BaseRoleMessage baseRoleMessage) {
        this.baseRoleMessage = baseRoleMessage;
    }

    public List<NPCMessage> getNpcMessages() {
        return npcMessages;
    }

    public void setNpcMessages(List<NPCMessage> npcMessages) {
        this.npcMessages = npcMessages;
    }

    public List<SceneMessage> getSceneMessages() {
        return sceneMessages;
    }

    public void setSceneMessages(List<SceneMessage> sceneMessages) {
        this.sceneMessages = sceneMessages;
    }
}
