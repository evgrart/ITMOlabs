package com.example.lab4.dto;

import java.io.Serializable;
import java.util.Map;

public class ProofStepDto implements Serializable {
    private String type;        // SUBSTITUTION, EVALUATION, INEQUALITY_CHECK
    private String expression;  
    private String result;      
    private Map<String, Double> substitution; 

    public ProofStepDto() {}

    public ProofStepDto(String type, String expression, String result) {
        this.type = type;
        this.expression = expression;
        this.result = result;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Map<String, Double> getSubstitution() { return substitution; }
    public void setSubstitution(Map<String, Double> substitution) { this.substitution = substitution; }
}