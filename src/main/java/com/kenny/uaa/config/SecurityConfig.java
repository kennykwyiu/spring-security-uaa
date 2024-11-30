package com.kenny.uaa.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.authorizeRequests(
////                req -> req.mvcMatchers("/api/greeting")
////                .hasRole("ADMIN")
////        );
////
//        http.formLogin(Customizer.withDefaults())
//                .authorizeRequests(
//                        req -> req.mvcMatchers("/api/greeting")
//                                .authenticated()
//                );
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(req -> req.antMatchers("/api/**").authenticated())
            .formLogin(form -> form.loginPage("/login"))
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            ;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**");
    }
}
