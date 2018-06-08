package com.lchli.lottery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "welcome");
        return "index";
    }


    @GetMapping("/apk/upload")
    public String uploadApk() {
        return "uploadApk";
    }
}
