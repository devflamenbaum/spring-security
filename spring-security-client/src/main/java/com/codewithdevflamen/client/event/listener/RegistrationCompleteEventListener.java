package com.codewithdevflamen.client.event.listener;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.event.RegistrationCompleteEvent;
import com.codewithdevflamen.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;

    public RegistrationCompleteEventListener(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //create verification token for the user link;
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        userService.saveVerificationTokenForUser(user, token);

        //send Email
        String url = event.getUrl() + "verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: {}", url);

    }
}
