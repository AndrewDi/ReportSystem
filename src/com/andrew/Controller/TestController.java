package com.andrew.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Home")
public class TestController {

    @RequestMapping("hello")
    public String hello(){

        return "Home/Index";
    }
}
