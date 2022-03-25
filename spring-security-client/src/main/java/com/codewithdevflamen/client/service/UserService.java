package com.codewithdevflamen.client.service;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.model.UserModel;

public interface UserService {

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);
}
