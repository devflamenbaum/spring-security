package com.codewithdevflamen.client.service;

import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.entity.VerificationToken;
import com.codewithdevflamen.client.model.UserModel;

public interface UserService {

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String validate(String token);

    VerificationToken generateNewToken(String token);

    User findByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);
}
