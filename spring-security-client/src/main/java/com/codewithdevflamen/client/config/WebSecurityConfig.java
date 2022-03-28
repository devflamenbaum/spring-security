package com.codewithdevflamen.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class WebSecurityConfig {

    private final static String[] WHITELIST = {
            "/register",
            "/verifyRegistration*",
            "/resendVerificationToken*"
    };

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(WHITELIST).permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/api-client-oidc"))
                .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
