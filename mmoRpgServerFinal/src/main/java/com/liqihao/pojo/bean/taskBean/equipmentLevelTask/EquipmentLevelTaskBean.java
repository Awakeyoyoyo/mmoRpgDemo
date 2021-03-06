package com.liqihao.pojo.bean.taskBean.equipmentLevelTask;

import com.liqihao.cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;

/**
 * @Classname EquipmentLevelTaskBean
 * @Description 装备等级任务
 * @Author lqhao
 * @Date 2021/1/27 15:17
 * @Version 1.0
 */
public class EquipmentLevelTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        EquipmentTaskLevelAction equipmentTaskLevelAction= (EquipmentTaskLevelAction) dto;
        Integer level=equipmentTaskLevelAction.getChangeLevel();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            setProgress(level);
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }
    }
}
