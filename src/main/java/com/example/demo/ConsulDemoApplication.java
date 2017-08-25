/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ecwid.consul.v1.ConsulClient;
import com.example.demo.consul.ConsulService;
import com.example.demo.support.JedisFactory;
import com.netflix.loadbalancer.IRule;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
@EnableConfigurationProperties
@RibbonClient(name = "ribbonConfig", configuration = RibbonConfig.class)
@EnableCircuitBreaker 
@ComponentScan
public class ConsulDemoApplication {
	@Autowired
	private Environment env;

	@Value("${spring.consul.agent.host}")
	private String consulHost;
	
	@Autowired
	private JedisFactory jedisFactory;
	@Autowired
	private ConsulService consulService;
	
	@Autowired
	SpringClientFactory springClientFactory;
//	@Autowired
//	CustomRule customRule;
//	@Autowired
//	private SampleClient sampleClient;

	
//	@RequestMapping("/feign")
//	public String feign() {
//		return sampleClient.choose();
//	}

	/*@Bean
	public SubtypeModule sampleSubtypeModule() {
		return new SubtypeModule(SimpleRemoteEvent.class);
	}*/
	@Bean 
	public ConsulClient ConsulClient(){
		return new ConsulClient(consulHost);
	}
	@Bean
	public SampleProperties sampleProperties() {
		return new SampleProperties();
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	

	public static void main(String[] args) {
		SpringApplication.run(ConsulDemoApplication.class, args);
	}

//	@Bean
//	@ConditionalOnMissingBean
//	public IRule ribbonRule() {
//		return customRule;
//	}
//	@Bean
//	@ConditionalOnMissingBean
//	public IRule ribbonRule() {
//		return new CustomRule(consulService);
//	}
	@Bean
	public CustomLoadBalancerClient customLoadBalancerClient() {
		return new CustomRibbonLoadBalancerClient(springClientFactory);
	}
}
