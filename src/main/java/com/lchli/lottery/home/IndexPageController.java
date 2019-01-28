package com.lchli.lottery.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexPageController {

    @GetMapping("/")
    public String index(Model model) {
        //model.addAttribute("name", "welcome");
        return "index";
    }

}
