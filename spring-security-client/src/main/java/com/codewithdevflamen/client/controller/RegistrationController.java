package com.codewithdevflamen.client.controller;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.entity.VerificationToken;
import com.codewithdevflamen.client.event.RegistrationCompleteEvent;
import com.codewithdevflamen.client.model.PasswordModel;
import com.codewithdevflamen.client.model.UserModel;
import com.codewithdevflamen.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
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

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String token, HttpServletRequest request){
        VerificationToken newToken = userService.generateNewToken(token);
        User user = newToken.getUser();
        resendVerificationTokenMail(user, applicationURL(request), newToken);
        return "Verification link sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){
        User user = userService.findByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationURL(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);

        if(!result.equalsIgnoreCase("valid")){
            return "expired token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);

        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password changed";
        } else {
            return "invalid user";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){

        User user = userService.findByEmail(passwordModel.getEmail());
        if(!userService.checkValidOldPassword(user, passwordModel.getOldPassword())){
            return "Invalid Old Password";
        }

        userService.changePassword(user, passwordModel.getNewPassword());
        return "Change Password Successfully";
    }

    private String passwordResetTokenMail(User user, String applicationURL, String token) {

        String url = applicationURL + "savePassword?token=" + token;

        log.info("Click the link to reset your account: {}", url);

        return url;

    }

    private void resendVerificationTokenMail(User user, String applicationURL, VerificationToken newToken) {


        //send Email
        String url = applicationURL + "verifyRegistration?token=" + newToken.getToken();

        log.info("Click the link to verify your account: {}", url);
    }

    private String applicationURL(HttpServletRequest request) {
        return String.format("http://%s:%s/%s",
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath());
    }
}
