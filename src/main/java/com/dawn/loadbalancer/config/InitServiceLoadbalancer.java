package com.dawn.loadbalancer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author dawn
 * @date 2024-08-07
 */
@Component
public class InitServiceLoadbalancer implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(InitServiceLoadbalancer.class);

    private LoadbalancerProperties loadbalancerProperties;

    private LoadBalancerClientFactory clientFactory;

    public InitServiceLoadbalancer(LoadbalancerProperties loadbalancerProperties,
                                   LoadBalancerClientFactory clientFactory) {
        this.loadbalancerProperties = loadbalancerProperties;
        this.clientFactory = clientFactory;
    }

    private void initServiceLoadbalancer() throws Exception {
        List<LoadBalancerClientSpecification> specifications = new ArrayList<>();
        Map<String, String> rules = loadbalancerProperties.getRule();

        Set<Map.Entry<String, String>> entries = rules.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String serviceId = entry.getKey();
            String className = entry.getValue();

            ApplicationContext parent = clientFactory.getParent();
            if (parent.containsBeanDefinition(serviceId + ".LoadBalancerClientSpecification")) {
                continue;
            }

            Class<?>[] aClass = {Class.forName(className)};
            LoadBalancerClientSpecification specification = new LoadBalancerClientSpecification();
            specification.setName(serviceId);
            specification.setConfiguration(aClass);
            specifications.add(specification);
        }
        clientFactory.setConfigurations(specifications);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.initServiceLoadbalancer();
        } catch (Exception e) {
            throw e;
        }
    }
}
