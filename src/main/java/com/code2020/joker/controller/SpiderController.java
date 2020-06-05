package com.code2020.joker.controller;

import com.code2020.joker.component.JokeSpiderComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/spider")
public class SpiderController {

    @Autowired
    private JokeSpiderComponent component;

    @GetMapping("dealType")
    public String dealType(){
        try {
            component.spiderType("abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("deal")
    public String deal(){
        try {
            component.spider("abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("dealContent")
    public String dealContent(){
        try {
            component.spiderContent("abc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
