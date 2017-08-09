package com.andrew.Controller;

import com.andrew.Model.RemoteUserModel;
import com.andrew.Service.DBConnService;
import com.andrew.Service.RemoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/Home")
public class HomeController {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DBConnService dbConnService;

    @Autowired
    private RemoteUserService remoteUserService;

    @RequestMapping("index")
    public String Index(ModelMap modelMap){

        return "Home/Index";
    }

    @RequestMapping("DBList")
    public String DBList(ModelMap modelMap){
        modelMap.addAttribute("data",dbConnService.getDBConnALL());
        return "Home/DBList";
    }

    @RequestMapping("DBReport")
    public String GetDBReport(ModelMap modelMap,
                              @RequestParam(value = "DBName",required = true) String DBName,
                              @RequestParam(value = "StartTime",required = true) String StartTime,
                              @RequestParam(value = "EndTime",required = true) String EndTime){
        logger.info("Found DBName:"+DBName+" StartTime:"+StartTime+" EndTime:"+EndTime);
        Map<String,Object> BaseInfo=dbConnService.getDBConnAny(DBName);
        if(BaseInfo!=null){
            String host=BaseInfo.get("HOST").toString();
            RemoteUserModel remoteUserModel=remoteUserService.getRemoteUserByHost(host);
        }
        modelMap.addAttribute("BaseInfo",BaseInfo);
        return "Home/DBReport";
    }
}
