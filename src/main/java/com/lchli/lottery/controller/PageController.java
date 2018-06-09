package com.lchli.lottery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("view/public")
public class PageController {


    @GetMapping("/apk/upload")
    public String uploadApk() {
        return "uploadApk";
    }
}
