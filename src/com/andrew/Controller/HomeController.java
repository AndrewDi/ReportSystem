package com.andrew.Controller;

import com.andrew.Service.DBConnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Home")
public class HomeController {

    @Autowired
    private DBConnService dbConnService;

    @RequestMapping("index")
    public String Index(ModelMap modelMap){

        return "Home/Index";
    }

    @RequestMapping("DBList")
    public String DBList(ModelMap modelMap){
        modelMap.addAttribute("data",dbConnService.getDBConnALL());
        return "Home/DBList";
    }
}
