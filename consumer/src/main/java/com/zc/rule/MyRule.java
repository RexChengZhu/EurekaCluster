package com.zc.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import sun.jvm.hotspot.jdi.IntegerValueImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 自定义ribbon权重负载均衡策略
 * 根据 ip和端口配的权重作为一个映射，选择的时候在服务和次数映射表选择，每次选择了一个服务之后，把对应服务的次数减一
 */
public class MyRule extends AbstractLoadBalancerRule{

    private static Map<String,String> ribbonMap = new HashMap<>();
    private static Map<String,String> originMap = new HashMap<>();

    static {
        // 如果想要实现不重启更新配置，可以开启一个定时器刷新更新配置文件
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties("ribbonRule.properties");
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()){
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                originMap.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        System.out.println("");
    }




    @Override
    public Server choose(Object key) {
        // 如果所有服务都用完了则重新拷贝一份
        Optional<String> isEmpty = ribbonMap.values().stream().filter(data -> !"0".equals(data)).findAny();
        if (!isEmpty.isPresent()){
            ribbonMap.putAll(originMap);
        }
        ILoadBalancer loadBalancer = getLoadBalancer();
        List<Server> reachableServers = loadBalancer.getReachableServers();
        Optional<Server> avaServer = reachableServers.stream().filter(server -> ribbonMap.keySet().contains(server.getHostPort())).findAny();
        if (avaServer.isPresent()){
            Server server = avaServer.get();
            synchronized (server){
                String value = ribbonMap.get(server.getHostPort());
                Integer integer = Integer.valueOf(value);
                integer--;
                if (integer<=0){
                    ribbonMap.remove(server.getHostPort());
                }else{
                    ribbonMap.put(server.getHostPort(), integer+"");
                }
            }
            return server;
        }
        return null;
    }
}
