package com.simboorm.simboorm.utility;

import javax.servlet.http.HttpServletRequest;

public class Client {
    public static String getip(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(ip==null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
