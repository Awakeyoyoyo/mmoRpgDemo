package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务管理类
 * @author lqhao
 */
public class TaskManager {
    private HashMap<Integer,BaseTaskBean> taskBeans=new HashMap<>();

    public HashMap<Integer, BaseTaskBean> getTaskBeans() {
        return taskBeans;
    }

    public void setTaskBeans(HashMap<Integer, BaseTaskBean> taskBeans) {
        this.taskBeans = taskBeans;
    }

    /**
     * 增加任务
     * @return
     */
    public void addTask(BaseTaskBean taskBean){
        taskBeans.put(taskBean.getTaskMessageId(),taskBean);
    }

    /**
     * 移除任务
     * @param taskMessageId
     */
    public void removeTask(Integer taskMessageId){
        taskBeans.remove(taskMessageId);
    }

    /**
     * 所有任务更新
     * @param actionDto
     * @param role
     */
    public void handler(ActionDto actionDto, MmoSimpleRole role) throws RpgServerException {
        for (BaseTaskBean value : taskBeans.values()) {
            value.update(actionDto,role);
        }
    }
    /**
     * 返回任务列表
     */
    public List<BaseTaskBean> getTasks(){
        return taskBeans.values().stream().collect(Collectors.toList());
    }

    /**
     * 返回任务列表Ids
     */
    public List<Integer> getTaskIds(){
        return taskBeans.values().stream().map(BaseTaskBean::getTaskMessageId).collect(Collectors.toList());
    }
}