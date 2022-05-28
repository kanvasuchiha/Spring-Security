package com.kanvas.spring.security.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${spring.security.oauth2.client.registration.api-client-oidc.client-name}")
    private String clientName;

    private static final String[] WHITE_LIST_URLS = {
            "/register",
            "/hello",
            "/resendVerifyToken*",
            "/verifyRegistration*"
    };

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf attacks happen when we are working with sessions and cookies to authenticate session info
        //As REST APIs are stateless by definition, we are using JSON Tokens for authorisation, we can
        // safely disable this security feature
        // We are defining here that anything that looks like "/api/**" is to be authenticated
        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .antMatchers(WHITE_LIST_URLS).permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .oauth2Login(oauth2login ->
                        oauth2login.loginPage("/oauth2/authorization/" + clientName))
                .oauth2Client(Customizer.withDefaults());
        return http.build();
    }

}
