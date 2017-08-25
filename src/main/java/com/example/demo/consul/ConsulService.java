package com.example.demo.consul;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogRegistration;
import com.ecwid.consul.v1.catalog.model.CatalogRegistration.Service;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsulService {

	@Autowired
	private ConsulClient consulClient;

	public void registerEdgeServer(EdgeInfo edge){
		Service service = new Service();
		service.setId(edge.getServiceId());
		service.setService(edge.getServiceName());
		CatalogRegistration catalogRegistration = new CatalogRegistration();
		catalogRegistration.setService(service);
		catalogRegistration.setAddress(edge.getAddress());
		//catalogRegistration.setDatacenter(edge.getDatacenter());
		catalogRegistration.setNode(edge.getNodeId());
		consulClient.catalogRegister(catalogRegistration);
	}
	
	public void createService(NewService newService){
		consulClient.agentServiceRegister(newService);
	}
	
	public List<String> getContentServerList(String contentName){
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> resultServerList = new ArrayList<String>();
		Response<GetValue> response = consulClient.getKVValue(contentName);
		String value = (response!=null?response.getValue().getDecodedValue():"");
		if(value!=null){
			try {
				List<String> foundServerIds = objectMapper.readValue(value, List.class);
				resultServerList.addAll(foundServerIds);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultServerList;
	}
}
