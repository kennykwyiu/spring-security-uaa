package com.kenny.uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenny.uaa.security.auth.ldap.LDAPMultiAuthenticationProvider;
import com.kenny.uaa.security.auth.ldap.LDAPUserRepo;
import com.kenny.uaa.security.filter.RestAuthenticationFilter;
import com.kenny.uaa.security.jwt.JwtFilter;
import com.kenny.uaa.security.userdetails.UserDetailsPasswordServiceImpl;
import com.kenny.uaa.security.userdetails.UserDetailsServiceImpl;
import com.kenny.uaa.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Order(99)
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;
    //    private final DataSource dataSource;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserDetailsPasswordServiceImpl userDetailsPasswordService;
    private final LDAPUserRepo ldapUserRepo;
    private final JwtFilter jwtFilter;
    private final Environment environment;

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
                .requestMatchers(req -> req.mvcMatchers("/authorize/**", "/admin/**", "/api/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .exceptionHandling(exp -> exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .authorizeRequests(req -> req.antMatchers("/api/**").authenticated())
                .authorizeRequests(req -> req
                                .mvcMatchers( "/authorize/**").permitAll()
                                .mvcMatchers("/admin/**").hasRole(Constants.AUTHORITY_STAFF)
//                        .antMatchers("/api/**").access("hasRole('USER') or hasRole('ADMIN')")
                                .mvcMatchers("/api/users/manager").hasRole(Constants.AUTHORITY_MANAGER)
                                .mvcMatchers("/api/users/by-email/{email}").hasRole(Constants.AUTHORITY_USER)
                                .mvcMatchers("/api/users/{username}/**").access("hasRole('" + Constants.AUTHORITY_ADMIN + "') or @userService.isValidUser(authentication, #username)")
                                .mvcMatchers("/api/**").authenticated()
                                .anyRequest().denyAll()
                )
//                        .anyRequest().authenticated())
//                .addFilterAt(getRestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .csrf(csrf -> csrf.ignoringAntMatchers("/authorize/**", "/admin/**", "/api/**"))
                .csrf(csrf -> csrf.disable())

                .httpBasic(Customizer.withDefaults()) // allow auth header
//                .csrf(Customizer.withDefaults())
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapMultiAuthenticationProvider());
        auth.authenticationProvider(daoAuthenticationProvider());


//                .jdbcAuthentication()
////                .withDefaultSchema() // delete after added h2
//                .dataSource(dataSource)
//
////                .inMemoryAuthentication()
//                .usersByUsernameQuery("select username, password, enable from uaa_users where username = ?")
//                .authoritiesByUsernameQuery("select username, authority from uaa_authorities where username = ?")
//

//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder())
//        ;
//                .withUser("user")
////                .password(passwordEncoder().encode("12345678"))
//                .password("{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S")
//                .roles("USER", "ADMIN")
//                .and()
//                .withUser("kenny")
////                .password(new MessageDigestPasswordEncoder("SHA-1").encode("abcd1234"))
//                .password("{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d")
//                .roles("USER");
    }

    @Bean
    LDAPMultiAuthenticationProvider ldapMultiAuthenticationProvider() {
        return new LDAPMultiAuthenticationProvider(ldapUserRepo);
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider
                .setUserDetailsService(userDetailsService);
        daoAuthenticationProvider
                .setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsPasswordService(userDetailsPasswordService);
        return daoAuthenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/error")
        ;
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
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

    private LogoutSuccessHandler getJsonLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication != null && authentication.getDetails() != null) {
                request.getSession().invalidate();
            }
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().println();
            log.debug("Successfully logged out");
        };
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

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Allowed hosts for cross-origin access
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            corsConfiguration.addAllowedOrigin("http://localhost:4001");
        } else {
            corsConfiguration.addAllowedOrigin("https://uaa.kenny.com");
        }
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.addExposedHeader("X-Authenticate");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

     @Bean
    public RoleHierarchy roleHierarchy() {
         RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
         StringBuilder stringBuilder = new StringBuilder();
         stringBuilder.append(Constants.ROLE_ADMIN)
                 .append(" > ")
                 .append(Constants.ROLE_MANAGER)
                 .append("\n")
                 .append(Constants.ROLE_MANAGER)
                 .append(" > ")
                 .append(Constants.ROLE_USER);
         roleHierarchy.setHierarchy(stringBuilder.toString());
//         roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER\nROLE_MANAGER > ROLE_USER");
         return roleHierarchy;
     }
}
