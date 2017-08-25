package com.example.demo.filter;

import java.util.List;

import com.example.demo.filter.Impl.NodeRankingImpl;
import com.example.demo.filter.Impl.rankingCriteria;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import lombok.extern.java.Log;

@Log
public class ResourceCheckFilter implements ChooseFilter
{

    private ILoadBalancer lb;
    private List<Server> filteredServerList;
    private Server selectedServer;
    private boolean isProccessed;

    @Override
    public ChooseFilter next(ChooseFilter nextFilter)
    {

        if (isProccessed)// 필터가 실행되면 걸러진 서버 리스트 전달
            nextFilter.filter(filteredServerList);
        else
            nextFilter.filter(lb);

        return nextFilter;
    }

    
    
    @Override
    public void filter(List<Server> serverList)
    {   
        NodeRankingImpl nodeRanking = new NodeRankingImpl();
        
        List<Server> list = nodeRanking.rankingServer(rankingCriteria.TOP_RANKING, serverList);
        if (list.size() > 0) {
            selectedServer = list.get(0);
        }
        
        
        
        // CPU 체크
//        List<Server> list = nodeRanking.LowCPU(serverList);
//        if(list.size() > 0)
//        {
//            SelectedServer = list.get(0);
//        }
        
        // Memory 체크
//        List<Server> list = nodeRanking.LowMemory(serverList);
//        if(list.size() > 0)
//        {
//            SelectedServer = list.get(0);
//        }
        
        // TrafficRx 체크
//        List<Server> list = nodeRanking.LowNetworkRx(serverList);
//        if(list.size() > 0)
//        {
//            SelectedServer = list.get(0);
//        }
        
        // TrafficTx 체크
//        List<Server> list = nodeRanking.LowNetworkTx(serverList);
//        if(list.size() > 0)
//        {
//            SelectedServer = list.get(0);
//        }

        isProccessed = true;
    }

    @Override
    public void filter(ILoadBalancer lb)
    {

    }

    @Override
    public Server chooseSever()
    {
        Server pickupedServer = this.selectedServer == null ? null : this.selectedServer; // this.filteredServerList.get(0);
        
        return pickupedServer;
    }

   

}
