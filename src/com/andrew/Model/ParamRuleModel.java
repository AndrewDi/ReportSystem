package com.andrew.Model;

import com.andrew.Common.ResultType;

public class ParamRuleModel {
    private int id;
    private String type;
    private String name;
    private String rule;
    private ResultType resultType;
    private Boolean required;
    private String fixRule;
    private Object evalResult;
    private String fixResult;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFixRule() {
        return fixRule;
    }

    public void setFixRule(String fixRule) {
        this.fixRule = fixRule;
    }

    public Object getEvalResult() {
        return evalResult;
    }

    public void setEvalResult(Object evalResult) {
        this.evalResult = evalResult;
    }

    public String getFixResult() {
        return fixResult;
    }

    public void setFixResult(String fixResult) {
        this.fixResult = fixResult;
    }
}
