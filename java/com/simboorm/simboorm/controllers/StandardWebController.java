package com.simboorm.simboorm.controllers;

import com.simboorm.simboorm.utility.Client;
import com.simboorm.simboorm.utility.Variable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class StandardWebController extends StandardController {
    @Override
    protected String handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        try {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            if(status != null) {
                Integer statusCode = Integer.valueOf(status.toString());
                System.out.println(String.format("ip[%s] / User[%s] / ErrorCode[%d]", Client.getip(request), Variable.getUser(request.getSession()), statusCode));
                if(statusCode == HttpStatus.NOT_FOUND.value()) {
                    return "/error/404";
                } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    return "/error/500";
                } else {
                    return "/error/error";
                }
            }

        } catch(Exception ignored) {}
        return "error/error";
    }

}
