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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    private Map<String,Object> buildMap(Map<String,Object> map,String name,Object value){
        int dotIndex=name.indexOf('.');
        if(dotIndex<0){
            if(map.containsKey(name)) map.remove(name);
            String valueStr=(value==null)? "nil":value.toString().trim();
            if(StringUtils.isNumber(valueStr)&&!valueStr.startsWith("0x")&&!valueStr.contains("e")){
                try {
                    Object parseValue;
                    if (valueStr.contains("\\."))
                        parseValue = Double.parseDouble(valueStr);
                    else parseValue = Long.valueOf(valueStr);
                    map.put(name, parseValue);
                }catch (NumberFormatException e){
                    map.put(name,valueStr);
                }
            }
            else {
                map.put(name,valueStr);
            }
            return map;
        }
        Map<String,Object> subMap=new HashMap<>();
        String keyName=name.substring(0,dotIndex);
        String newName=name.substring(dotIndex+1);
        if(map.containsKey(keyName)){
            subMap=(Map<String,Object>)map.get(keyName);
        }
        else {
            map.put(keyName, subMap);
        }
        return this.buildMap(subMap,newName,value);
    }

    public Map<String,Object> buildEnv(Map<String,Object> extraEnv,List<Map<String,Object>> params){
        Map<String, Object> env = new HashMap<>();
        if(extraEnv!=null)
            env=extraEnv;
        for(Map<String,Object> param:params){
            if(param.containsKey("NAME")){
                String name=param.get("NAME").toString();
                for (String key:param.keySet()){
                    if(!key.equals(name)){
                        this.buildMap(env,String.format("$%s.%s", name,key),param.get(key));
                    }
                }
            }
            else {
                continue;
            }
        }
        return env;
    }

    private String getValueByEnv(Map<String,Object> env,String fullname){
        String[] names = fullname.split("\\.");
        Map<String,Object> tmpEnv=env;
        for(String name:names){
            if(tmpEnv.containsKey(name)&&tmpEnv.get(name) instanceof Map){
                tmpEnv=(Map<String,Object>)tmpEnv.get(name);
            }
            else {
                if(tmpEnv.get(name)==null)
                    return "nil";
                else
                    return tmpEnv.get(name).toString();
            }
        }
        return null;
    }

    private List<ParamRuleModel> execRules(List<ParamRuleModel> rules,Map<String,Object> env){
        for(ParamRuleModel rule:rules){
            try {
                Expression expression = AviatorEvaluator.compile(rule.getRule());
                List<String> variables = expression.getVariableFullNames();
                for(String var:variables){
                    rule.setParams(rule.getParams()+String.format("%s=%s;",var,this.getValueByEnv(env,var)));
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