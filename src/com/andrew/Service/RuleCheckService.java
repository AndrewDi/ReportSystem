package com.andrew.Service;

import com.alibaba.druid.util.StringUtils;
import com.andrew.Common.ResultType;
import com.andrew.Model.ParamRuleModel;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleCheckService {

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

    public Map<String,Object> buildEnv(List<Map<String,Object>> params) {
        return this.buildEnv(null,params);
    }

    public Map<String,Object> buildEnv(Map<String,Object> extraEnv,List<Map<String,Object>> params){
        Map<String, Object> env = new HashMap<>();
        if(extraEnv!=null)
            env=extraEnv;
        for(Map<String,Object> param:params){
            String value="";
            if(param.get("VALUE")==null){
                value="nil";
            }
            else {
                value=param.get("VALUE").toString();
            }
            param.replace("NAME",param.get("NAME").toString().replace(".","_").trim());
            if(StringUtils.isNumber(value)&&!value.startsWith("0x")&&!value.contains("e")){
                Integer valInteger=StringUtils.stringToInteger(value);
                if(valInteger!=null){
                    env.put(String.format("$%s_VALUE", param.get("NAME").toString()), valInteger);
                }else {
                    env.put(String.format("$%s_VALUE", param.get("NAME").toString()), value);
                }
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
            try {
                Expression expression = AviatorEvaluator.compile(rule.getRule());
                List<String> variables = expression.getVariableFullNames();
                for(String var:variables){
                    if(env.containsKey(var)){
                        rule.setParams(rule.getParams()+String.format("%s=%s;",var,env.get(var).toString()));
                    }
                    else {
                        rule.setEvalResult(String.format("Can Not Find Var:%s",var));
                    }
                }
                Object result = expression.execute(env);
                rule.setEvalResult(result);
            }
            catch (ExpressionRuntimeException runException){
                rule.setEvalResult(runException.getMessage());
            }
            catch (ExpressionSyntaxErrorException synException){
                rule.setEvalResult(synException.getMessage());
            }
            if(rule.getFixRule()!=null&&!StringUtils.isEmpty(rule.getFixRule()))
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
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DBCFG,dbname);
        Map<String,Object> env=new HashMap<>();
        env.put("$DB",dbname);
        return this.getObjectChecked("DBCFG",params,env);
    }

    /**
     * Check DBM Cfg Rules
     * @param dbname
     * @return
     */
    public List<ParamRuleModel> getDBMCFGChecked(String dbname){
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DBMCFG,dbname);
        Map<String,Object> env=new HashMap<>();
        env.put("$DB",dbname);
        return this.getObjectChecked("DBMCFG",params,env);
    }

    /**
     * Check DB2Set Rules
     * @param dbname
     * @return
     */
    public List<ParamRuleModel> getDB2SetChecked(String dbname){
        List<Map<String,Object>> params = this.jdbcTemplate.queryForList(SELECT_DSM_DB2SET,dbname);
        Map<String,Object> env=new HashMap<>();
        env.put("$DB",dbname);
        return this.getObjectChecked("DB2SET",params,env);
    }

    /**
     * Check Object Rules From Outside
     * @param type
     * @param objects
     * @return
     */
    public List<ParamRuleModel> getObjectChecked(String type,List<Map<String,Object>> objects){
        return this.getObjectChecked(type,objects,null);
    }

    /**
     * Check Object Rules From Outside
     * @param type
     * @param objects
     * @return
     */
    public List<ParamRuleModel> getObjectChecked(String type,List<Map<String,Object>> objects,Map<String,Object> extraEnv){
        List<ParamRuleModel> rules = this.getRulesByType(type);
        Map<String,Object> env=this.buildEnv(extraEnv,objects);
        return this.execRules(rules,env);
    }
}