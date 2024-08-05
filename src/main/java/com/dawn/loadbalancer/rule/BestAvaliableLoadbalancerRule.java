package com.dawn.loadbalancer.rule;

import com.dawn.loadbalancer.lifecycle.LoadbalancerStatLifecycle;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author dawn
 * @date 2024-08-05
 */
public class BestAvaliableLoadbalancerRule {

    @Bean
    public ReactorServiceInstanceLoadBalancer bestAvaliableLoadbalancer(Environment env,
                                                                        LoadBalancerClientFactory loadBalancerClientFactory,
                                                                        LoadbalancerStatLifecycle loadBalancerStatLifecycle) {
        String name = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new BestAvaliableLoadbalancer(name,
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), loadBalancerStatLifecycle);
    }
}
