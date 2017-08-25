package com.example.demo;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ecwid.consul.v1.agent.model.NewService;
import com.example.demo.consul.ConsulService;
import com.example.demo.consul.EdgeInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class exampleContoller {
	@Autowired
	private CustomLoadBalancerClient loadBalancer;

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ConsulService consulService;
	
	@Value("${spring.application.name:testConsulApp}")
	private String appName;

	@RequestMapping("/me")
	public ServiceInstance me() {
		return discoveryClient.getLocalServiceInstance();
	}
	
	@RequestMapping("/")
	public ServiceInstance lb() {
		ServiceInstance service = loadBalancer.choose(appName);
		
		return service;
	}

	@HystrixCommand(groupKey="test" , fallbackMethod="failFilter")
	@RequestMapping("/rest/{test}")
	public String rest(@PathVariable("test")String test) {
		//return this.restTemplate.getForObject("http://"+appName+"/me", String.class);
		if(test.equals("test")){
			String[] test1 = test.split("-");
			System.out.println("test:"+test1[1]);
		}
		else	
			System.out.println("test:1");
		return "ok";
	}

	@RequestMapping(value="/{serviceId}/{contentName}/chooseEdge",method = RequestMethod.GET)
	public String choose(@PathVariable("serviceId") String serviceId,@PathVariable("contentName") String contentName) {
		ServiceInstance service = loadBalancer.choose(serviceId,contentName);
		/*
		 * redirection Logic 구현
		 */
		return service.getUri().toString();
	}
	
	@RequestMapping("/instances")
	public List<ServiceInstance> instances() {
		return discoveryClient.getInstances(appName);
	}

	@RequestMapping(value="/registerEdge", method=RequestMethod.POST)
	public EdgeInfo registerEdgeServer(@RequestBody EdgeInfo edge) {
		consulService.registerEdgeServer(edge);
		return edge;
	}

	@RequestMapping(value="/createService", method=RequestMethod.POST)
	public NewService createService(@RequestBody NewService newService) {
		consulService.createService(newService);
		return newService;
	}

	public String failFilter(String test) {
		return "test";
	}


}
