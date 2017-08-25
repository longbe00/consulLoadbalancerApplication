package com.example.demo.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Data
@NoArgsConstructor
public class HealthCheckFilter implements ChooseFilter{
	
	private ILoadBalancer lb;
	private List<Server> filteredServerList;
	private boolean isProccessed;
	@Autowired
	private RestTemplate restTemplate;

	public HealthCheckFilter(ILoadBalancer lb) {
		// TODO Auto-generated constructor stub
		this.lb = lb;
		this.isProccessed = false;
	}
	@Override
	public ChooseFilter next(ChooseFilter nextFilter) {
	    
		//HealthCheck의 경우 LoadBalancer 시작으로 서버 정보를 받아온다.
		filter(lb);
		if(isProccessed)//필터가 실행되면 걸러진 서버 리스트 전달
			nextFilter.filter(filteredServerList);
		else
			nextFilter.filter(lb);
		
		return nextFilter;

	}

	@HystrixCommand(groupKey="consul" , fallbackMethod="failFilter")
	@Override
	public void filter(List<Server> serverList) {
	}

	@Override
	@HystrixCommand(groupKey="consul" , fallbackMethod="failFilter")
	public void filter(ILoadBalancer lb) {
		String test = null;
		test.equals("");
		List<Server> reachableServers = lb.getReachableServers();
		this.filteredServerList = reachableServers;
		isProccessed = true;
		
	}
	
	public void failFilter() {
		isProccessed = false;
		log.error("CircuitBreaker open!!");
		filteredServerList = lb.getReachableServers();
	}
	
	@Override
	public Server chooseSever() {
		return null;
	}
}
