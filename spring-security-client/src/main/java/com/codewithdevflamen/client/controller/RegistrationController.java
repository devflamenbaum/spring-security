package com.codewithdevflamen.client.controller;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.event.RegistrationCompleteEvent;
import com.codewithdevflamen.client.model.UserModel;
import com.codewithdevflamen.client.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/verifyRegistration")
    public String verifyToken(@RequestParam("token") String token){
        String result = userService.validate(token);
        if(result.equalsIgnoreCase("valid")){
            return "User verified successfully";
        }
        return "Bad User";
    }

    private String applicationURL(HttpServletRequest request) {
        return String.format("http://%s:%s/%s",
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath());
    }
}
