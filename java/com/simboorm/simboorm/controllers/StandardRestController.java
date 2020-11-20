package com.simboorm.simboorm.controllers;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class StandardRestController extends StandardController {
    @Override
    protected String handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        try {
            response.sendRedirect("/error");
        } catch(Exception ignored) {}
        return null;
    }
}
