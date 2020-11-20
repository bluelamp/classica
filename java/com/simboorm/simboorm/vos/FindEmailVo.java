package com.simboorm.simboorm.vos;

public class FindEmailVo {
    private final String name;
    private final String contact;

    public FindEmailVo(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}
