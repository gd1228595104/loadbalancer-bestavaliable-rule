package com.dawn.loadbalancer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dawn
 * @date 2024-08-07
 */
@ConfigurationProperties(prefix = "loadbalancer")
public class LoadbalancerProperties {

    private Map<String, String> rule = new HashMap<>();

    public Map<String, String> getRule() {
        return rule;
    }

    public void setRule(Map<String, String> rule) {
        this.rule = rule;
    }
}
