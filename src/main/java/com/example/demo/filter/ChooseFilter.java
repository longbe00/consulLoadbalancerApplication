package com.example.demo.filter;

import java.util.List;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public interface ChooseFilter {

	public abstract ChooseFilter next(ChooseFilter nextFilter);
	public abstract void filter(List<Server> serverList);
	public abstract void filter(ILoadBalancer lb);
	public abstract Server chooseSever();

}
