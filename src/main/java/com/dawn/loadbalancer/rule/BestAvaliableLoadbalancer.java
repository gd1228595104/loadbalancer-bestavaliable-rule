package com.dawn.loadbalancer.rule;

import com.dawn.loadbalancer.lifecycle.LoadbalancerStatLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dawn
 * @date 2024-08-05
 */
public class BestAvaliableLoadbalancer implements ReactorServiceInstanceLoadBalancer {

    private Logger log = LoggerFactory.getLogger(BestAvaliableLoadbalancer.class);

    private String serviceId;

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private LoadbalancerStatLifecycle loadBalancerStatLifecycle;

    public BestAvaliableLoadbalancer(String serviceId,
                                     ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                     LoadbalancerStatLifecycle loadBalancerStatLifecycle) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.loadBalancerStatLifecycle = loadBalancerStatLifecycle;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        ServiceInstance chosen = null;
        long min = Long.MAX_VALUE;

        for (ServiceInstance instance : instances) {
            long activeRequestCount = loadBalancerStatLifecycle.getActiveRequestCount(instance);
            if (activeRequestCount == 0) {
                chosen = instance;
                break;
            }
            if (activeRequestCount < min) {
                min = activeRequestCount;
                chosen = instance;
            }
        }

        return new DefaultResponse(chosen);
    }
}
