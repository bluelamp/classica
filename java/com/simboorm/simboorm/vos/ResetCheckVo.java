package com.simboorm.simboorm.vos;

public class ResetCheckVo {
    private final String email;
    private final String contact;
    private final String name;

    public ResetCheckVo(String email, String contact, String name) {
        this.email = email;
        this.contact = contact;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }
}
