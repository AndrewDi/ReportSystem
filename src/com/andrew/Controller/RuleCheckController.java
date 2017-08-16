package com.andrew.Controller;

import com.andrew.Model.ParamRuleModel;
import com.andrew.Service.RuleCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/Rule")
public class RuleCheckController {

    @Autowired
    private RuleCheckService ruleCheckService;

    @RequestMapping("dbcfgcheck")
    public @ResponseBody
    List<ParamRuleModel> getDBCfgChecked(@RequestParam(value = "DBName",required = true) String DBName){
        return this.ruleCheckService.getDBCFGChecked(DBName);
    }
}
