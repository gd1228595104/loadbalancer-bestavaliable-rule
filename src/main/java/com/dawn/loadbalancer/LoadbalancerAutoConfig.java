package com.dawn.loadbalancer;

import com.dawn.loadbalancer.config.InitServiceLoadbalancer;
import com.dawn.loadbalancer.config.LoadbalancerProperties;
import com.dawn.loadbalancer.lifecycle.LoadbalancerStatLifecycle;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dawn
 * @date 2024-08-05
 */
@EnableConfigurationProperties(value = {LoadbalancerProperties.class})
@Configuration
public class LoadbalancerAutoConfig {

    @Bean
    public LoadbalancerStatLifecycle loadbalancerStatLifecycle() {
        return new LoadbalancerStatLifecycle();
    }

    @Bean
    public InitServiceLoadbalancer initServiceLoadbalancer(LoadbalancerProperties loadbalancerProperties, LoadBalancerClientFactory loadBalancerClientFactory) {
        return new InitServiceLoadbalancer(loadbalancerProperties, loadBalancerClientFactory);
    }
}
