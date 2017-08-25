package com.example.demo.support;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;


/**
 * @author mike
 *
 */
@Component
public class JedisFactory {

	@Value("${redis.server.url}")
	private String redisServerUrl;
	
	@Value("${redis.server.port}")
	private Integer redisServerPort;
	 
	@Value("${redis.server.timeout}")
	private Integer redisTimeout;
	
	@Value("${redis.server.password}")
	private String redisPassword;
	
	
	
	
	@Value("${redis.pool.config.test-while-idle}")
	private Boolean testWhileIdle;

	@Value("${redis.pool.config.min-evictable-idle-time-millis}")
	private Long minEvictableIdleTimeMillis;;

	@Value("${redis.pool.config.time-between-eviction-runs-millis}")
	private Long timeBetweenEvictionRunsMillis;

	@Value("${redis.pool.config.num-tests-per-eviction-run}")
	private Integer numTestsPerEvictionRun;

	private JedisPool jedisPool;
	
	@Value("${redis.pool.config.pool-no-use}")
	private Boolean redisNoUse;
	
	@PostConstruct
	private void init() {
		JedisPoolConfig  poolConfig = new JedisPoolConfig();
		
		poolConfig.setTestWhileIdle(testWhileIdle);			//idle 한 상태에 있는 것들에 대해서 검증
		poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);	//60초이상 idle하면 제거
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis); 	//30초마다 동작
		poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);

		if (!redisNoUse) {
			
			jedisPool = new JedisPool(poolConfig, redisServerUrl, redisServerPort, redisTimeout, redisPassword);
		}
	}
	
	/**
	 * Get Jedis Connection Resource
	 * 
	 * @return Jedis
	 */
	public Jedis getResource() {
		
		if (jedisPool == null) {
			 throw new JedisConnectionException(
					    "Could not get a resource from the pool(null)");
		}
		
		return jedisPool.getResource();
	}
	
	
	@PreDestroy
	private void destroy() {
		
		if (jedisPool != null) {
			jedisPool.destroy();
		}
	}
	
}
