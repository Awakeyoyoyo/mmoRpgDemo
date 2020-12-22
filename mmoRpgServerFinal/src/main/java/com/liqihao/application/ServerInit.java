package com.liqihao.application;

import com.liqihao.util.CommonsUtil;
import com.liqihao.util.LogicThreadPool;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    public void init() throws IOException, IllegalAccessException, InstantiationException {
        ScheduledThreadPoolUtil.init();
        LogicThreadPool.init(6);
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));
    }

}
