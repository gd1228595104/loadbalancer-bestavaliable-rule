package com.dawn.loadbalancer.lifecycle;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dawn
 * @date 2024-08-05
 */
public class LoadbalancerStatLifecycle implements LoadBalancerLifecycle<Object, Object, ServiceInstance> {

    private final Map<ServiceInstance, AtomicLong> activeRequestMap = new ConcurrentHashMap<>();

    @Override
    public void onStart(Request<Object> request) {

    }

    @Override
    public void onStartRequest(Request<Object> request, Response<ServiceInstance> lbResponse) {
        if (request.getContext() instanceof TimedRequestContext) {
            ((TimedRequestContext) request.getContext()).setRequestStartTime(System.nanoTime());
        }
        if (!lbResponse.hasServer()) {
            return;
        }
        ServiceInstance serviceInstance = lbResponse.getServer();
        AtomicLong activeRequestsCounter = activeRequestMap.computeIfAbsent(serviceInstance, instance -> new AtomicLong());
        activeRequestsCounter.incrementAndGet();
    }

    @Override
    public void onComplete(CompletionContext<Object, ServiceInstance, Object> completionContext) {
        if (CompletionContext.Status.DISCARD.equals(completionContext.status())) {
            return;
        }
        ServiceInstance serviceInstance = completionContext.getLoadBalancerResponse().getServer();
        AtomicLong activeRequestsCounter = activeRequestMap.get(serviceInstance);
        if (activeRequestsCounter != null) {
            activeRequestsCounter.decrementAndGet();
        }
    }

    public long getActiveRequestCount(ServiceInstance serviceInstance) {
        AtomicLong atomicLong = this.activeRequestMap.get(serviceInstance);
        return atomicLong == null ? 0L : atomicLong.get();
    }
}
