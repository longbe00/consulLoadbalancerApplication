package com.example.demo.filter.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.consul.discovery.ConsulServer;
import org.springframework.stereotype.Component;

import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;
import com.example.demo.filter.NodeRanking;
import com.example.demo.model.HealthOutputList;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.Server;

import lombok.extern.java.Log;

@Log
@Component
public class NodeRankingImpl implements NodeRanking {

    List<Float> rankValue = new ArrayList<Float>();
    List<Server> rankedServerList = new ArrayList<Server>();

    @Override
    public List<Server> lowCPU(List<Server> serverList) {

        rankValue.clear();
        rankedServerList.clear();

        for (Server server : serverList) {

            float value = 100;

            ConsulServer consulServer = (ConsulServer) server;
            HealthService health = consulServer.getHealthService();

            for (Check check : health.getChecks()) {

                // Output 항목에 HealthCheck 결과가 있는지 OS 문자열로 체크
                if (check.getOutput().indexOf("OS") > 0) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(); // JSON 매핑을 위한 ObjectMapper 생성

                        // Check 항목에서 HealthCheck 결과가 있는 output만 가져온다.
                        // output에 임의로 입력했던 줄바꿈 문자를 모두 삭제한다.
                        String output = check.getOutput().replaceAll("\\n", "");

                        // output값을 HealthOutputList 클래스에 매핑
                        HealthOutputList outputList = mapper.readValue(output, HealthOutputList.class);

                        // CPU 값만 가져온다.
                        value = Float.valueOf(outputList.CPU);

                        // 처음 가져온 값이면 바로 설정
                        if (rankValue.size() < 1) {
                            rankValue.add(value);
                            rankedServerList.add(server);
                        }
                        else {
                            boolean isOK = false;

                            // 이전 값과 비교해서 List.set() 함수를 이용해 위치 설정
                            for (int i = 0; i < rankValue.size(); i++) {
                                if (value < rankValue.get(i)) {
                                    rankValue.set(i, value);
                                    rankedServerList.set(i, server);

                                    isOK = true;
                                    break;
                                }
                            }

                            // 마지막에 추가
                            if (!isOK) {
                                rankValue.add(value);
                                rankedServerList.add(server);
                            }
                        }
                    }
                    catch (JsonParseException ex) {
                        log.info("## Parse Error : " + ex.toString());
                    }
                    catch (JsonMappingException ex) {
                        log.info("## Mapping Error : " + ex.toString());
                    }
                    catch (Exception ex) {
                        log.info("## Error : " + ex.toString());
                    }

                }
            }
        }

        // 결과 확인
        for (Float f : rankValue) {
            log.info("## rank : " + String.valueOf(f));
        }

        /*
         * // Collections.sort로하면 null 에러가 발생함
         * 
         * Collections.sort(svList, new Comparator<Server>() {
         * 
         * @Override public int compare(Server s1, Server s2) { float f1 = 100, f2 =
         * 100;
         * 
         * if (s1 != null && s2 != null) {
         * 
         * ConsulServer consulServer = (ConsulServer) s1; HealthService health =
         * consulServer.getHealthService(); for (Check check : health.getChecks()) { if
         * (check.getOutput().indexOf("OS") > 0) { try { ObjectMapper mapper = new
         * ObjectMapper(); String output = check.getOutput().replaceAll("\\n", "");
         * 
         * HealthOutputList outputList = mapper.readValue(output,
         * HealthOutputList.class);
         * 
         * f1 = Float.valueOf(outputList.CPU); } catch (JsonParseException ex) {
         * log.info("## Parse Error : " + ex.toString()); } catch (JsonMappingException
         * ex) { log.info("## Mapping Error : " + ex.toString()); } catch (Exception ex)
         * { log.info("## Error : " + ex.toString()); }
         * 
         * } }
         * 
         * consulServer = (ConsulServer) s2; health = consulServer.getHealthService();
         * for (Check check : health.getChecks()) { if (check.getOutput().indexOf("OS")
         * > 0) { try { ObjectMapper mapper = new ObjectMapper(); String output =
         * check.getOutput().replaceAll("\\n", "");
         * 
         * HealthOutputList outputList = mapper.readValue(output,
         * HealthOutputList.class);
         * 
         * f2 = Float.valueOf(outputList.CPU); } catch (JsonParseException ex) {
         * log.info("## Parse Error : " + ex.toString()); } catch (JsonMappingException
         * ex) { log.info("## Mapping Error : " + ex.toString()); } catch (Exception ex)
         * { log.info("## Error : " + ex.toString()); } } } } return f1 > f2 ? -1 : (f1
         * < f2) ? 1 : 0; }
         * 
         * });
         */

        return rankedServerList;
    }

    @Override
    public List<Server> lowMemory(List<Server> serverList) {

        rankValue.clear();
        rankedServerList.clear();

        for (Server server : serverList) {

            float value = 100;

            ConsulServer consulServer = (ConsulServer) server;
            HealthService health = consulServer.getHealthService();

            for (Check check : health.getChecks()) {

                // Output 항목에 HealthCheck 결과가 있는지 OS 문자열로 체크
                if (check.getOutput().indexOf("OS") > 0) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(); // JSON 매핑을 위한 ObjectMapper 생성

                        // Check 항목에서 HealthCheck 결과가 있는 output만 가져온다.
                        // output에 임의로 입력했던 줄바꿈 문자와 % 기호를 모두 삭제한다.
                        String output = check.getOutput().replaceAll("\\n", "");

                        // output값을 HealthOutputList 클래스에 매핑
                        HealthOutputList outputList = mapper.readValue(output, HealthOutputList.class);

                        // Memory 값만 가져온다.
                        value = Float.valueOf(outputList.Memory.replaceAll("%", ""));

                        // 처음 가져온 값이면 바로 설정
                        if (rankValue.size() < 1) {
                            rankValue.add(value);
                            rankedServerList.add(server);
                        }
                        else {
                            boolean isOK = false;

                            // 이전 값과 비교해서 List.set() 함수를 이용해 위치 설정
                            for (int i = 0; i < rankValue.size(); i++) {
                                if (value < rankValue.get(i)) {
                                    rankValue.set(i, value);
                                    rankedServerList.set(i, server);

                                    isOK = true;
                                    break;
                                }
                            }

                            // 마지막에 추가
                            if (!isOK) {
                                rankValue.add(value);
                                rankedServerList.add(server);
                            }
                        }
                    }
                    catch (JsonParseException ex) {
                        log.info("## Parse Error : " + ex.toString());
                    }
                    catch (JsonMappingException ex) {
                        log.info("## Mapping Error : " + ex.toString());
                    }
                    catch (Exception ex) {
                        log.info("## Error : " + ex.toString());
                    }

                }
            }
        }

        // 결과 확인
        for (Float f : rankValue) {
            log.info("## Memory Rank : " + String.valueOf(f));
        }

        return rankedServerList;
    }

    @Override
    public List<Server> lowDisk(List<Server> serverList) {
        return null;
    }

    @Override
    public List<Server> lowNetworkRx(List<Server> serverList) {

        rankValue.clear();
        rankedServerList.clear();

        for (Server server : serverList) {

            float value = 100;

            ConsulServer consulServer = (ConsulServer) server;
            HealthService health = consulServer.getHealthService();

            for (Check check : health.getChecks()) {

                // Output 항목에 HealthCheck 결과가 있는지 OS 문자열로 체크
                if (check.getOutput().indexOf("OS") > 0) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(); // JSON 매핑을 위한 ObjectMapper 생성

                        // Check 항목에서 HealthCheck 결과가 있는 output만 가져온다.
                        // output에 임의로 입력했던 줄바꿈 문자와 % 기호를 모두 삭제한다.
                        String output = check.getOutput().replaceAll("\\n", "");

                        // output값을 HealthOutputList 클래스에 매핑
                        HealthOutputList outputList = mapper.readValue(output, HealthOutputList.class);

                        // TrafficRx 값만 가져온다.
                        value = Float.valueOf(outputList.TrafficRX.replaceAll("GB", ""));

                        // 처음 가져온 값이면 바로 설정
                        if (rankValue.size() < 1) {
                            rankValue.add(value);
                            rankedServerList.add(server);
                        }
                        else {
                            boolean isOK = false;

                            // 이전 값과 비교해서 List.set() 함수를 이용해 위치 설정
                            for (int i = 0; i < rankValue.size(); i++) {
                                if (value < rankValue.get(i)) {
                                    rankValue.set(i, value);
                                    rankedServerList.set(i, server);

                                    isOK = true;
                                    break;
                                }
                            }

                            // 마지막에 추가
                            if (!isOK) {
                                rankValue.add(value);
                                rankedServerList.add(server);
                            }
                        }
                    }
                    catch (JsonParseException ex) {
                        log.info("## Parse Error : " + ex.toString());
                    }
                    catch (JsonMappingException ex) {
                        log.info("## Mapping Error : " + ex.toString());
                    }
                    catch (Exception ex) {
                        log.info("## Error : " + ex.toString());
                    }
                }
            }
        }

        // 결과 확인
        for (Float f : rankValue) {
            log.info("## TrafficRx Rank : " + String.valueOf(f));
        }

        return rankedServerList;
    }

    @Override
    public List<Server> lowNetworkTx(List<Server> serverList) {

        rankValue.clear();
        rankedServerList.clear();

        for (Server server : serverList) {

            float value = 100;

            ConsulServer consulServer = (ConsulServer) server;
            HealthService health = consulServer.getHealthService();

            for (Check check : health.getChecks()) {

                // Output 항목에 HealthCheck 결과가 있는지 OS 문자열로 체크
                if (check.getOutput().indexOf("OS") > 0) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(); // JSON 매핑을 위한 ObjectMapper 생성

                        // Check 항목에서 HealthCheck 결과가 있는 output만 가져온다.
                        // output에 임의로 입력했던 줄바꿈 문자와 % 기호를 모두 삭제한다.
                        String output = check.getOutput().replaceAll("\\n", "");

                        // output값을 HealthOutputList 클래스에 매핑
                        HealthOutputList outputList = mapper.readValue(output, HealthOutputList.class);

                        // TrafficTx 값만 가져온다.
                        value = Float.valueOf(outputList.TrafficTX.replaceAll("GB", ""));

                        // 처음 가져온 값이면 바로 설정
                        if (rankValue.size() < 1) {
                            rankValue.add(value);
                            rankedServerList.add(server);
                        }
                        else {
                            boolean isOK = false;

                            // 이전 값과 비교해서 List.set() 함수를 이용해 위치 설정
                            for (int i = 0; i < rankValue.size(); i++) {
                                if (value < rankValue.get(i)) {
                                    rankValue.set(i, value);
                                    rankedServerList.set(i, server);

                                    isOK = true;
                                    break;
                                }
                            }

                            // 마지막에 추가
                            if (!isOK) {
                                rankValue.add(value);
                                rankedServerList.add(server);
                            }
                        }
                    }
                    catch (JsonParseException ex) {
                        log.info("## Parse Error : " + ex.toString());
                    }
                    catch (JsonMappingException ex) {
                        log.info("## Mapping Error : " + ex.toString());
                    }
                    catch (Exception ex) {
                        log.info("## Error : " + ex.toString());
                    }
                }
            }
        }

        // 결과 확인
        for (Float f : rankValue) {
            log.info("## TrafficTx Rank : " + String.valueOf(f));
        }

        return rankedServerList;
    }

    @Override
    public List<Server> rankingServer(rankingCriteria rankType, List<Server> serverList) {

        rankValue.clear();
        rankedServerList.clear();

        for (Server server : serverList) {

            float value = 0L;

            ConsulServer consulServer = (ConsulServer) server;
            HealthService health = consulServer.getHealthService();

            for (Check check : health.getChecks()) {

                // Output 항목에 HealthCheck 결과가 있는지 OS 문자열로 체크
                if (check.getOutput().indexOf("OS") > 0) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(); // JSON 매핑을 위한 ObjectMapper 생성

                        // Check 항목에서 HealthCheck 결과가 있는 output만 가져온다.
                        // output에 임의로 입력했던 줄바꿈 문자와 % 기호를 모두 삭제한다.
                        String output = check.getOutput().replaceAll("\\n", "");

                        // output값을 HealthOutputList 클래스에 매핑
                        HealthOutputList outputList = mapper.readValue(output, HealthOutputList.class);

                        // rankType에 따라 각각의 항목에 가중치 값을 곱해줘서 점수를 매긴다.

                        float insCPU = 0L, insMemory = 0L, insDisk = 0L;

                        switch (rankType) {
                        case TOP_RANKING:
                            insCPU = (float) 0.33;
                            insMemory = (float) 0.33;
                            insDisk = (float) 0.33;
                            break;

                        case LOW_CPU:
                            insCPU = (float) 0.5;
                            insMemory = (float) 0.25;
                            insDisk = (float) 0.25;
                            break;
                        case LOW_MEMORY:
                            insCPU = (float) 0.25;
                            insMemory = (float) 0.5;
                            insDisk = (float) 0.25;
                            break;
                        case LOW_DISK:
                            insCPU = (float) 0.25;
                            insMemory = (float) 0.25;
                            insDisk = (float) 0.5;
                            break;
                        }

                        Float CPU = (float) (Float.valueOf(outputList.CPU) * insCPU);
                        Float Memory = (float) (Float.valueOf(outputList.Memory.replaceAll("%", "")) * insMemory);
                        Float Disk = (float) (Float.valueOf(outputList.Disk.replaceAll("%", "")) * insDisk);

                        value = CPU + Memory + Disk;

                        // 결과 확인
                        log.info("## Total (CPU / Memory / Disk) : " + String.valueOf(value) + "( " + String.valueOf(CPU) + " / " + String.valueOf(Memory) + " / " + String.valueOf(Disk) + " )");

                        // 처음 가져온 값이면 바로 설정
                        if (rankValue.size() < 1) {
                            rankValue.add(value);
                            rankedServerList.add(server);
                        }
                        else {
                            boolean isOK = false;

                            // 이전 값과 비교해서 List.set() 함수를 이용해 위치 설정
                            for (int i = 0; i < rankValue.size(); i++) {
                                if (value < rankValue.get(i)) {
                                    rankValue.set(i, value);
                                    rankedServerList.set(i, server);

                                    isOK = true;
                                    break;
                                }
                            }

                            // 마지막에 추가
                            if (!isOK) {
                                rankValue.add(value);
                                rankedServerList.add(server);
                            }
                        }
                    }
                    catch (JsonParseException ex) {
                        log.info("## Parse Error : " + ex.toString());
                    }
                    catch (JsonMappingException ex) {
                        log.info("## Mapping Error : " + ex.toString());
                    }
                    catch (Exception ex) {
                        log.info("## Error : " + ex.toString());
                    }
                }
            }
        }

        // 결과 확인
        int i = 1;
        log.info("");
        for (Float f : rankValue) {
            log.info("## Rank " + String.valueOf(i++) + " : " + String.valueOf(f));
        }
        log.info("");

        return rankedServerList;
    }

}
