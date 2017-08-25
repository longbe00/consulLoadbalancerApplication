## Synopsis
Spring cloud Consul 기반의 Custom Loadbancer Rule 적용 프로젝트 

## Custom Rule 적용 항목 
* Service/Health-Check
 * Consul Service Discover기반 
 * SpringCloud Ribbon의 Loadbalancer에서 자동 처리 
* Content-Check
 * Consul K/V
  * connection pool처리가 불가능 Rest API기반 처리 속도 확인 필요
 * Cache Application
  * Resdis 이용 하여 Key의 정규 표현식을 통한 처리 가능 (좀 더 유연한 처리 가능)
* Resource-Check
 * Consul Checks 기반
 * SpringCloud Ribbon의 Loadbalancer ConsulServer class 내부에 HealthCheck class에 Check 데이터 수집된 상태
 * 수집된 정보 기반 각 항목별 Ranking point 산정 합산후 최종 Endpoint 서버 선택

Spring cloud 사용 dependency 추가 
```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-feign</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-ribbon</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-consul-discovery</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zuul</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
```