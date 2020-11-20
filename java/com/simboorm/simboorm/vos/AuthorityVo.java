package com.simboorm.simboorm.vos;

public class AuthorityVo {
    private final int readLevel;
    private final int writeLevel;
    public AuthorityVo(int readLevel, int writeLevel) {
        this.readLevel = readLevel;
        this.writeLevel = writeLevel;
    }

    public int getReadLevel() {
        return readLevel;
    }

    public int getWriteLevel() {
        return writeLevel;
    }
}
