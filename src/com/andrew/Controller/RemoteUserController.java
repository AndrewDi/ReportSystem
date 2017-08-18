package com.andrew.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Settings")
public class RemoteUserController {

    @RequestMapping("EditSshUser")
    public String DBList(ModelMap modelMap){
        return "Settings/EditSshUser";
    }
}
