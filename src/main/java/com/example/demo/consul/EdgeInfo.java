package com.example.demo.consul;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EdgeInfo {
	private String serviceId;
	private String serviceName;
	private String address;
	private String datacenter;
	private String nodeId;

}
