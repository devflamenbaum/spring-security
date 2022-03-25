package com.codewithdevflamen.client.controller;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.event.RegistrationCompleteEvent;
import com.codewithdevflamen.client.model.UserModel;
import com.codewithdevflamen.client.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RegistrationController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserModel userModel, final HttpServletRequest request){
        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationURL(request)));
        return "Registration success";
    }

    private String applicationURL(HttpServletRequest request) {
        return String.format("http://%s:%s/S%s",
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath());
    }
}
