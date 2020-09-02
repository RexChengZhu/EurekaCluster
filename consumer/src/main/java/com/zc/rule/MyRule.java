package com.zc.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
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

    private static List<String> arr = new ArrayList<>();

    static {
        // 如果想要实现不重启更新配置，可以开启一个定时器刷新更新配置文件
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties("ribbonRule.properties");
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()){
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                // 获得每个服务的占比
                Integer size = Integer.valueOf(value);
                for (Integer i = 0; i < size; i++) {
                    arr.add(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        System.out.println("");
    }




    private static final Random random = new Random();
    @Override
    public Server choose(Object key) {
        // 如果所有服务都用完了则重新拷贝一份

        ILoadBalancer loadBalancer = getLoadBalancer();
        List<Server> reachableServers = loadBalancer.getReachableServers();
        int size = arr.size();
        int index = random.nextInt(size);
        String port = arr.get(index);
        Optional<Server> any = reachableServers.stream().filter(server -> port.equals(server.getHostPort())).findAny();
        return any.orElse(null);
    }


}
