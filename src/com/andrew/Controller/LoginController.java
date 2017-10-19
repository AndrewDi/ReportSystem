package com.andrew.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(){
        return new ModelAndView("Login/Index");
    }

    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    public ModelAndView Login(HttpSession session,@RequestParam(value = "InputUserName") String username, @RequestParam(value = "inputPassword") String passwd)
    {
        if(!username.equals("admin")||!passwd.equals("ASdf@0987"))
        {
            ModelMap modelMap=new ModelMap();
            modelMap.put("error","User "+username+" Not Auth to Login");
            return new ModelAndView("Login/Index",modelMap);
        }
        session.setAttribute("isLogin",true);
        return new ModelAndView("Home/Index");
    }

    @RequestMapping(value = "/Logout",method = RequestMethod.GET)
    public String Logout(HttpSession session){
        if(session.getAttribute("isLogin")!=null){
            session.removeAttribute("isLogin");
        }
        return "Login/Index";
    }

}
