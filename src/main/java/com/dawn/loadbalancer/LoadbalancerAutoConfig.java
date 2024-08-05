package com.dawn.loadbalancer;

import com.dawn.loadbalancer.lifecycle.LoadbalancerStatLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dawn
 * @date 2024-08-05
 */
@Configuration
public class LoadbalancerAutoConfig {

    @Bean
    public LoadbalancerStatLifecycle loadbalancerStatLifecycle(){
        return new LoadbalancerStatLifecycle();
    }
}
