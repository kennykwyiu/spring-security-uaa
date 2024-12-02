package com.kenny.uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username1")
                        .defaultSuccessUrl("/")
                        .successHandler(getJsonAuthenticationSuccessHandler())
                        .failureHandler(getAuthenticationFailureHandler())
                        .permitAll())
//                .httpBasic(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/perform_logout"))
                .rememberMe(rememberMe -> rememberMe
                        .tokenValiditySeconds(30 * 24 * 3600) // 30days
                        .rememberMeCookieName("someKeyToRemember"))
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

    private RestAuthenticationFilter getRestAuthenticationFilter() {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(getJsonAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler();
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
                .password(passwordEncoder().encode("12345678"))
                .roles("USER", "ADMIN");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**", "/error")
//                ;
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
