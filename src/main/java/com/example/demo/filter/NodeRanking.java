package com.example.demo.filter;

import java.util.List;

import com.example.demo.filter.Impl.rankingCriteria;
import com.netflix.loadbalancer.Server;

public interface NodeRanking
{
    
    public abstract List<Server> lowCPU(List<Server> serverList);
    
    public abstract List<Server> lowMemory(List<Server> serverList);
    
    public abstract List<Server> lowDisk(List<Server> serverList);
    
    public abstract List<Server> lowNetworkRx(List<Server> serverList);
    
    public abstract List<Server> lowNetworkTx(List<Server> serverList);

    public abstract List<Server> rankingServer(rankingCriteria rankType, List<Server> serverList);
}
