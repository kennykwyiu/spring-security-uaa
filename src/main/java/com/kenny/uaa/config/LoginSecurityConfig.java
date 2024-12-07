package com.kenny.uaa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
@Order(100)
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(req -> req.anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username1")
                        .defaultSuccessUrl("/")
//                        .successHandler(getJsonAuthenticationSuccessHandler())
//                        .failureHandler(getJsonLoginFailureHandler())
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/perform_logout"))
//                        .logoutSuccessHandler(getJsonLogoutSuccessHandler()))
                .rememberMe(rememberMe -> rememberMe
                        .tokenValiditySeconds(30 * 24 * 3600) // 30days
                        .rememberMeCookieName("someKeyToRemember"))
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers("/public/**", "/error", "/h2-console/**")
                ;
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
