package com.andrew.Controller;

import com.andrew.Common.ArrayUtils;
import com.andrew.Model.ParamRuleModel;
import com.andrew.Model.RemoteUserModel;
import com.andrew.Service.DBConnService;
import com.andrew.Service.RemoteUserService;
import com.andrew.Service.RuleCheckService;
import com.andrew.Service.SshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Rule")
public class RuleCheckController {

    @Autowired
    private RuleCheckService ruleCheckService;

    @Autowired
    private DBConnService dbConnService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SshService sshService;


    @RequestMapping("DBCheck")
    public String DBList(ModelMap modelMap,@RequestParam(value = "DBName",required = true) String DBName){
        return "Home/DBParamCheck";
    }

    @RequestMapping("dbcfgcheck")
    public @ResponseBody
    List<ParamRuleModel> getDBCfgChecked(@RequestParam(value = "DBName",required = true) String DBName){
        return this.ruleCheckService.getDBCFGChecked(DBName);
    }

    @RequestMapping("dbmcfgcheck")
    public @ResponseBody
    List<ParamRuleModel> getDBMCfgChecked(@RequestParam(value = "DBName",required = true) String DBName){
        return this.ruleCheckService.getDBMCFGChecked(DBName);
    }

    @RequestMapping("db2setcheck")
    public @ResponseBody
    List<ParamRuleModel> getDB2SetChecked(@RequestParam(value = "DBName",required = true) String DBName){
        return this.ruleCheckService.getDB2SetChecked(DBName);
    }

    @RequestMapping("extcheck")
    public @ResponseBody
    List<ParamRuleModel> getExtChecked(@RequestParam(value = "DBName",required = true) String DBName){
        Map<String,Object> BaseInfo=dbConnService.getDBConnAny(DBName);
        if(BaseInfo!=null){
            String host=BaseInfo.get("HOST").toString();
            RemoteUserModel remoteUserModel=remoteUserService.getRemoteUserByHost(host);
            String sshResult = sshService.ShScpAndExecOnce("getExtRuleParam.sh",remoteUserModel.getHost(),remoteUserModel.getUserName(),remoteUserModel.getPasswd() );
            return ruleCheckService.getObjectChecked("EXT", ArrayUtils.strToMapList(sshResult));
        }
        return null;
    }
}