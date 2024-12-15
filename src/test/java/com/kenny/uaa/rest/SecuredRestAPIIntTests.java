package com.kenny.uaa.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.config.http.MatcherType.mvc;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecuredRestAPIIntTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser
    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(
                get("/api/greeting")
        )
                .andExpect(status().isOk());
    }
    @WithMockUser(username = "kenny", roles = {"ADMIN"})
    @Test
    public void givenRoleUserOrAdmin_thenAccessSuccess() throws Exception {
        mockMvc.perform(get("/api/users/{username}", "user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "kenny", roles = {"USER"})
    @Test
    public void givenUserRole_whenQueryUserByEmail_shouldSuccess() throws Exception {
        mockMvc.perform(get("/api/users/by-email/{email}", "kenny@local.dev"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
