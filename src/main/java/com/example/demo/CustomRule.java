package com.example.demo;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.consul.ConsulService;
import com.example.demo.filter.ContentsCheckFilter;
import com.example.demo.filter.HealthCheckFilter;
import com.example.demo.filter.ProtocolCheckFilter;
import com.example.demo.filter.ResourceCheckFilter;
import com.example.demo.support.JedisFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mike
 *
 */
@Slf4j
@Component
public class CustomRule extends AbstractLoadBalancerRule {
	@Autowired
	private HealthCheckFilter healthCheckFilter;
	
	private JedisFactory jedisFactory;
	private ConsulService consulService;
    private AtomicInteger nextServerCyclicCounter;
    private static final boolean AVAILABLE_ONLY_SERVERS = true;
    private static final boolean ALL_SERVERS = false;
    
    public CustomRule() {
         nextServerCyclicCounter = new AtomicInteger(0);
    }

    public CustomRule(JedisFactory jedisFactory) {
         this();
         this.jedisFactory = jedisFactory;
    }

	public CustomRule(ConsulService consulService) {
        this();
        this.consulService = consulService;
   }

    public CustomRule(ILoadBalancer lb) {
         this();
         setLoadBalancer(lb);
    }

    public Server choose(ILoadBalancer lb, Object key) {
         
    	String contentName = (String)key;
         if (lb == null) {
              log.warn("no load balancer");
              return null;
         }
         
         Server server = null;
         int count = 0;
         while (server == null && count++ < 10) {
     	    /*
     	     * heath Check & Service Server List Get
     	     */
        	healthCheckFilter.setLb(lb);
     	    log.info("##select contents:{}",key);
     	    
    	    
     	    
     	    /*
     	     * health check->content check -> resource check
     	     * health check(service discovery/health check/protocol check)
     	     * filtering이 끝나면 최종 Sever가 선택됨
     	     */
     	    server = healthCheckFilter.next(new ProtocolCheckFilter()).next(new ContentsCheckFilter(jedisFactory, contentName)).next(new ResourceCheckFilter()).chooseSever();
//     	    server = hcFilter.next(new ProtocolCheckFilter()).next(new ConsulContentsCheckFilter(consulService, contentName)).next(new ResourceCheckFilter()).chooseSever();

     	    
              if (server == null) {
                    /* Transient. */
                    Thread.yield();
                    continue;
              }

              
              
              
              
              if (server.isAlive() && (server.isReadyToServe())) {
                    return (server);
              }

              // Next.
              server = null;
         }

         if (count >= 10) {
              log.warn("No available alive servers after 10 tries from load balancer: " + lb);
         }
         /*
          * To Do
          * resource Filter
          */

         
         /*
          * To Do
          * content Filter
          */
         
         return server;
    }

    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
         for (;;) {
              int current = nextServerCyclicCounter.get();
              int next = (current + 1) % modulo;
              if (nextServerCyclicCounter.compareAndSet(current, next))
                    return next;
         }
    }

    @Override
    public Server choose(Object key) {
         return choose(getLoadBalancer(), key);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }
}
