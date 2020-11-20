package com.simboorm.simboorm.vos;

public class BlockipVo {
    private final String ip;
    private final int level;


    public BlockipVo(String ip, int level) {
        this.ip = ip;
        this.level = level;
    }

    public String getIp() {
        return ip;
    }

    public int getLevel() {
        return level;
    }
}
