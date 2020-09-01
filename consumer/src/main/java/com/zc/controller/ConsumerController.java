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

@RestController
public class ConsumerController implements ApplicationContextAware {


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String test(){


        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                ResponseEntity<String> forEntity = restTemplate.getForEntity("http://PROVIDER/test", String.class);
                String body = forEntity.getBody();

                System.out.println(body);
            }).start();

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
