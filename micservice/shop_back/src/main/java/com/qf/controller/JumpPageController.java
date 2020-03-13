package com.qf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jumpPageTo")
public class JumpPageController {

    /**
     * 跳转到home页面
     *
     * @param home
     * @return
     */
    @RequestMapping("/{home}")
    public String home(@PathVariable("home") String home) {
        return home;
    }
}
