package com.codewithdevflamen.client.event;

import com.codewithdevflamen.client.entity.User;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String url;

    public RegistrationCompleteEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
