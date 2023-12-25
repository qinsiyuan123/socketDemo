package com.example.websocketdemo.com.qsy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: qsy
 * @Date: 2023/12/25  16:25
 */
@Controller
@RequestMapping("/demo")
public class WebSocketController {

    @GetMapping("/toWebSocketDemo/{cid}")
    public String toWebSocketDemo(@PathVariable String cid, Model model){
        model.addAttribute("cid", cid);
        return "websocketDemo";
    }
}
