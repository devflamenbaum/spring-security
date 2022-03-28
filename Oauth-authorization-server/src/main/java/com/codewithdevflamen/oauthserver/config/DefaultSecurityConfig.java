package com.codewithdevflamen.oauthserver.config;

import com.codewithdevflamen.oauthserver.service.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class DefaultSecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    public DefaultSecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(authorizeRequest -> authorizeRequest.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());

        return http.build();

    }

    @Autowired
    public void bind(AuthenticationManagerBuilder builder){
        builder.authenticationProvider(customAuthenticationProvider);

    }
}
