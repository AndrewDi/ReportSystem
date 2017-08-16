package com.andrew.Service;

import com.alibaba.druid.util.StringUtils;
import com.andrew.Common.ResultType;
import com.andrew.Model.ParamRuleModel;
import com.googlecode.aviator.AviatorEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleCheckServices {

    private String SELECT_RULES_BY_TYPE="SELECT ID,TYPE, NAME, RULE, RESULT_TYPE, REQUIRED, FIX_RULE FROM CMDB.PARAM_RULES WHERE TYPE=?";

    private String SELECT_DSM_DBCFG="SELECT NAME,VALUE,VALUE_FLAGS FROM IBMIOCM.DB2LUW_DBCFG WHERE IBMIOCM_DATABASE=?";

    private String SELECT_DSM_DBMCFG="SELECT NAME,VALUE,VALUE_FLAGS FROM IBMIOCM.DB2LUW_DBMCFG WHERE IBMIOCM_DATABASE=?";

    private String SELECT_DSM_DB2SET="SELECT REG_VAR_NAME AS NAME,REG_VAR_VALUE AS VALUE FROM IBMIOCM.DB2LUW_REG_VARIABLES WHERE IBMIOCM_DATABASE=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ParamRuleModel> getRulesByType(String type){
        RowMapper<ParamRuleModel> paramRuleModelRowMapper= (resultSet, i) -> {
            ParamRuleModel paramRuleModel=new ParamRuleModel();
            paramRuleModel.setId(resultSet.getInt("ID"));
            paramRuleModel.setName(resultSet.getString("NAME"));
            paramRuleModel.setType(resultSet.getString("TYPE"));
            paramRuleModel.setRule(resultSet.getString("RULE"));
            switch (resultSet.getString("RESULT_TYPE")){
                case "INT":paramRuleModel.setResultType(ResultType.INT);break;
                case "STRING":paramRuleModel.setResultType(ResultType.STRING);break;
                case "BOOL":paramRuleModel.setResultType(ResultType.BOOL);break;
            }
            paramRuleModel.setRequired(resultSet.getBoolean("REQUIRED"));
            paramRuleModel.setFixRule(resultSet.getString("FIX_RULE"));
            return paramRuleModel;
        };
        List<ParamRuleModel> result = jdbcTemplate.query(SELECT_RULES_BY_TYPE,paramRuleModelRowMapper,type);
        return result;
    }

    public Map<String,Object> buildEnv(List<Map<String,Object>> params){
        Map<String, Object> env = new HashMap<>();
        for(Map<String,Object> param:params){
            String value="";
            if(param.get("VALUE")==null){
                value="nil";
            }
            else {
                value=param.get("VALUE").toString();
            }
            if(StringUtils.isNumber(value)&&!value.startsWith("0x")){
                env.put(String.format("$%s_VALUE", param.get("NAME").toString()),StringUtils.stringToInteger(value));
            }
            else {
                env.put(String.format("$%s_VALUE", param.get("NAME").toString()), value);
            }
            if(param.containsKey("VALUE_FLAGS")) {
                env.put(String.format("$%s_VALUE_FLAGS", param.get("NAME").toString()), param.get("VALUE_FLAGS"));
            }
        }
        return env;
    }

    private List<ParamRuleModel> execRules(List<ParamRuleModel> rules,Map<String,Object> env){
        for(ParamRuleModel rule:rules){
            Object result = AviatorEvaluator.execute(rule.getRule(),env);
            rule.setEvalResult(result);
            rule.setFixResult((String)AviatorEvaluator.execute(rule.getFixRule(),env));
        }
        return rules;
    }

    /**
     * Check DBCfg Rules
     * @param dbname
     * @return
     */
    public List<ParamRuleModel> getDBCFGChecked(String dbname){
        List<ParamRuleModel> rules = this.getRulesByType("DBCFG");
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DBCFG,dbname);
        Map<String,Object> env=this.buildEnv(params);
        env.put("$DB",dbname);
        return this.execRules(rules,env);
    }

    /**
     * Check DBM Cfg Rules
     * @param dbname
     * @return
     */
    public List<ParamRuleModel> getDBMCFGChecked(String dbname){
        List<ParamRuleModel> rules = this.getRulesByType("DBMCFG");
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DBMCFG,dbname);
        Map<String,Object> env=this.buildEnv(params);
        env.put("$DB",dbname);
        return this.execRules(rules,env);
    }

    /**
     * Check DB2Set Rules
     * @param dbname
     * @return
     */
    public List<ParamRuleModel> getDB2SetChecked(String dbname){
        List<ParamRuleModel> rules = this.getRulesByType("DB2SET");
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DB2SET,dbname);
        Map<String,Object> env=this.buildEnv(params);
        env.put("$DB",dbname);
        return this.execRules(rules,env);
    }
}
