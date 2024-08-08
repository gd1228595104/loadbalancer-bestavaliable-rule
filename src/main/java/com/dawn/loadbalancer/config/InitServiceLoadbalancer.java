package com.dawn.loadbalancer.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author dawn
 * @date 2024-08-07
 */
public class InitServiceLoadbalancer implements ApplicationListener<ContextRefreshedEvent> {

    private LoadbalancerProperties loadbalancerProperties;

    private LoadBalancerClientFactory clientFactory;

    public InitServiceLoadbalancer(LoadbalancerProperties loadbalancerProperties, LoadBalancerClientFactory clientFactory) {
        this.loadbalancerProperties = loadbalancerProperties;
        this.clientFactory = clientFactory;
    }

    @PostConstruct
    public void initServiceLoadbalancer() {
        if (loadbalancerProperties == null || CollectionUtils.isEmpty(loadbalancerProperties.getRule())) {
            // 配置为空的情况下，需要清楚LoadbalancerClientFactory中的Loadbalancer
            clientFactory.destroy();
            return;
        }
        List<LoadBalancerClientSpecification> specifications = new ArrayList<>();
        Map<String, String> rules = loadbalancerProperties.getRule();
        try {
            Set<Map.Entry<String, String>> entries = rules.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String serviceId = entry.getKey();
                String className = entry.getValue();
                Class<?>[] aClass = {Class.forName(className)};
                LoadBalancerClientSpecification specification = new LoadBalancerClientSpecification();
                specification.setName(serviceId);
                specification.setConfiguration(aClass);
                specifications.add(specification);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        clientFactory.setConfigurations(specifications);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            this.initServiceLoadbalancer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
