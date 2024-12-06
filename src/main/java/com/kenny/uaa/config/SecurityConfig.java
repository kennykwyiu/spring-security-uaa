package com.kenny.uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenny.uaa.security.filter.RestAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;

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
//            .authorizeRequests(req -> req.antMatchers("/api/**").authenticated())
                .authorizeRequests(req -> req
                        .antMatchers("/authorize/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterAt(getRestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username1")
                        .defaultSuccessUrl("/")
                        .successHandler(getJsonAuthenticationSuccessHandler())
                        .failureHandler(getJsonLoginFailureHandler())
                        .permitAll())

                .httpBasic(Customizer.withDefaults())

                .csrf(csrf -> csrf.ignoringAntMatchers("/authorize/**", "/admin/**", "/api/**"))
//                .csrf(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessHandler(getJsonLogoutSuccessHandler()))
//                .rememberMe(rememberMe -> rememberMe
//                        .tokenValiditySeconds(30 * 24 * 3600) // 30days
//                        .rememberMeCookieName("someKeyToRemember"))
        ;
    }

    private static AuthenticationFailureHandler getJsonLoginFailureHandler() {
        return (req, res, exp) -> {
            val objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("UTF-8");
            val errData = Map.of(
                    "title", "Authentication Failure!!!",
                    "details", exp.getMessage()
            );
            res.getWriter().println(objectMapper.writeValueAsString(errData));
        };
    }

    private RestAuthenticationFilter getRestAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(getJsonAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(getJsonLoginFailureHandler());
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

    private static AuthenticationSuccessHandler getJsonAuthenticationSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();

            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(auth));
            log.debug("Authentication Successful!!!!!");
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
//                .password(passwordEncoder().encode("12345678"))
                .password("{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S")
                .roles("USER", "ADMIN")
                .and()
                .withUser("kenny")
//                .password(new MessageDigestPasswordEncoder("SHA-1").encode("abcd1234"))
                .password("{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d")
                .roles("USER");;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForDefault = "bcrypt";
        Map<String, PasswordEncoder> encoderMap = Map.of(
                idForDefault, new BCryptPasswordEncoder(),
                "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        return new DelegatingPasswordEncoder(idForDefault, encoderMap);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**", "/error")
//                ;
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
