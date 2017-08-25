package com.example.demo;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

public interface CustomLoadBalancerClient extends LoadBalancerClient{

	/**
	 * LoadBalancerClient의 choose Method로 content에 대한 필터 기능을 구현 할 수 없기 때문에 
	 * 추가적인 Method를 생성하여 Filter를 처리 할 수 있도록 처리
	 * @param serviceId
	 * @param contentName
	 * @return
	 */
	public ServiceInstance choose(String serviceId, String contentName); 
}
