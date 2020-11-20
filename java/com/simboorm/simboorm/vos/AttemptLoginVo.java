package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.utility.Client;

import javax.servlet.http.HttpServletRequest;

public class AttemptLoginVo {
    private final String email;
    private final String password;
    private final String ip;
    private final String loginResult;

    public AttemptLoginVo(String email, String password, String ip, String loginResult) {
        this.email = email;
        this.password = password;
        this.ip = ip;
        this.loginResult = loginResult;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public String getLoginResult() {
        return loginResult;
    }
}
