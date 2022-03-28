package com.codewithdevflamen.client.service;

import com.codewithdevflamen.client.entity.PasswordResetToken;
import com.codewithdevflamen.client.entity.User;
import com.codewithdevflamen.client.entity.VerificationToken;
import com.codewithdevflamen.client.model.UserModel;
import com.codewithdevflamen.client.repository.PasswordResetTokenRepository;
import com.codewithdevflamen.client.repository.UserRepository;
import com.codewithdevflamen.client.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserModel userModel) {

        User user = User.builder()
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .email(userModel.getEmail())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

        return user;
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {

        VerificationToken verificationToken =
                new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validate(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);

        if(verificationToken == null) {
            return "invalid token";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if((verificationToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "Token has expired";
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "valid";
    }

    @Override
    public VerificationToken generateNewToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);

        String newToken = UUID.randomUUID().toString();

        verificationToken.setToken(newToken);
        verificationToken.setExpirationDate(Date.from(LocalDateTime.now().plus(Duration.of(10, ChronoUnit.MINUTES))
                                                .atZone(ZoneId.systemDefault()).toInstant()));

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);

    }
}
