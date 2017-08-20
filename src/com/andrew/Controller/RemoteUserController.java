package com.andrew.Controller;

import com.andrew.Model.RemoteUserModel;
import com.andrew.Service.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/Settings")
public class RemoteUserController {

    @Autowired
    private RemoteUserService remoteUserService;

    @RequestMapping("EditSshUser")
    public String DBList(ModelMap modelMap){
        return "Settings/EditSshUser";
    }

    @RequestMapping("getremoteusers")
    public @ResponseBody
    List<RemoteUserModel> getRemoteUsersAll() {
        return this.remoteUserService.getRemoteUserAll();
    }

    @RequestMapping("editRemoteusers")
    @ResponseBody
    public int editRemoteUser(final RemoteUserModel model) {
        if(model==null||model.getId()==0)
            return -1;
        return this.remoteUserService.editRemoteUserByID(model);
    }
}
