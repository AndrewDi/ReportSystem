package com.andrew.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Home")
public class HomeController {

    @RequestMapping("hello")
    public String hello(ModelMap modelMap){
        modelMap.addAttribute("name","hello,world");
        return "Home/Index";
    }
}
