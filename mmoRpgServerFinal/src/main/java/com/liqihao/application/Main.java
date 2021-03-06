package com.liqihao.application;

import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.handler.DispatcherServlet;
import com.liqihao.netty.NettyTcpServer;
import com.liqihao.provider.EmailServiceProvider;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动类
 * @author lqhao
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        //查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        //根据id获取bean
        //Spring就是一个大工厂（容器）专门生成bean bean就是对象
        //输出获取到的对象
        ServerInit serverInit = (ServerInit) applicationContext.getBean("serverInit");
        serverInit.init();
        NettyTcpServer nettyTcpServer=(NettyTcpServer)applicationContext.getBean("nettyTcpServer");
        nettyTcpServer.run();
    }
}
