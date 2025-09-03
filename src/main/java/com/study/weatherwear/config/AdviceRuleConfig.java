package com.study.weatherwear.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdviceRuleConfig {
    private Condition condition;
    private String advice;

    private boolean onlyIfDaytime = false;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition {
        private String field;
        private String operator;
        private Object value;
    }

}
