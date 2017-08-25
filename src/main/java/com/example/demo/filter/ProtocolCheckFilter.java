package com.example.demo.filter;

import java.util.List;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public class ProtocolCheckFilter implements ChooseFilter {

	private ILoadBalancer lb;
	private List<Server> filteredServerList;
	private boolean isProccessed;
	
	@Override
	public ChooseFilter next(ChooseFilter nextFilter) {
		// TODO Auto-generated method stub
		if(isProccessed)//필터가 실행되면 걸러진 서버 리스트 전달
			nextFilter.filter(filteredServerList);
		else
			nextFilter.filter(lb);
		
		return nextFilter;
		
	}

	@Override
	public void filter(List<Server> serverList) {
		// TODO Auto-generated method stub
		this.filteredServerList = serverList;
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
