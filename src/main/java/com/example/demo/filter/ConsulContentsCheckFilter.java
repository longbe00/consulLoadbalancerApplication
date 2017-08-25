package com.example.demo.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.consul.discovery.ConsulServer;

import com.example.demo.consul.ConsulService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
public class ConsulContentsCheckFilter implements ChooseFilter{

	private String contentName;
	private ILoadBalancer lb;
	private List<Server> filteredServerList;
	private boolean isProccessed;
	private ConsulService consulService;
	
	public ConsulContentsCheckFilter(ConsulService consulService, String contentName) {
		super();
		this.contentName = contentName;
		this.consulService = consulService;
	}

	@Override
	public ChooseFilter next(ChooseFilter nextFilter) {
	    
		if(isProccessed)//필터가 실행되면 걸러진 서버 리스트 전달
			nextFilter.filter(filteredServerList);
		else
			nextFilter.filter(lb);
		
		return nextFilter;
	}

	/* (non-Javadoc)
	 * contentName를  redis의 키로 사용하여 요청에 대한 서버 리스트를 filtering한다. 
	 * @see com.example.demo.filter.ChooseFilter#filter(java.util.List)
	 */
	@Override
	public void filter(List<Server> serverList) {
		// TODO Auto-generated method stub
		List<Server> foundServerList = new ArrayList<Server>();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<String> foundServerIds = consulService.getContentServerList(contentName);
			if(foundServerIds != null){
	    		for(String serverId:foundServerIds){
	        		for(Server item: serverList){
	        			ConsulServer server = (ConsulServer)item;
	        			if(server.getHealthService().getNode().getNode().equals(serverId)){
	        				foundServerList.add(item);
	        				break;
	        			}
	        		}
	    		}
			}
        } catch (JedisConnectionException ex) {
            log.error(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex2) {
            log.error("Redis connect failed.");
		}
		//1개 이상 매칭되는 경우에만 해당 서버 목록을 전달 
		if(foundServerList.size()>0)
			this.filteredServerList = serverList;
		log.info("## reachableServers :"+filteredServerList.toString());
		isProccessed = true;
	}

	@Override
	public void filter(ILoadBalancer lb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Server chooseSever() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
