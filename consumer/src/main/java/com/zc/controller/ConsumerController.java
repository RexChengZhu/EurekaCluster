package com.zc.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ConsumerController implements ApplicationContextAware {


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String test(){
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 400 ; i++) {
            pool.execute(()->{
                ResponseEntity<String> forEntity = restTemplate.getForEntity("http://PROVIDER/test", String.class);
                String body = forEntity.getBody();

                System.out.println(body);
            });
        }
        return "ok";
    }


    @GetMapping("/test1")
    public String test1(){
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://PROVIDER-02/test", String.class);
        String body = forEntity.getBody();

        return body;
    }
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
