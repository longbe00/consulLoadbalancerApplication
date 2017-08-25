package com.example.demo.filter.Impl;

public enum rankingCriteria {
    
    /**
     * 전체적 상황이 가장 좋은 SERVER
     */
    TOP_RANKING, 
    /**
     * CPU 사용률이 가장 낮은 SERVER
     */
    LOW_CPU, 
    /**
     * MEMORY 사용률이 가장 낮은 SERVER
     */
    LOW_MEMORY,
    /**
     * DISK 사용률이 가장 낮은 SERVER
     */
    LOW_DISK
}
