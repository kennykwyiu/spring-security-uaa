package com.kenny.uaa.security.auth.ldap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@DataLdapTest
public class LDAPUserRepoIntTests {
    @Autowired
    LDAPUserRepo ldapUserRepo;

    @Test
    public void givenUsernameAndPassword_ThenFindUserSuccess() {
        String username = "zhaoliu";
        String password = "123";
        Optional<LDAPUser> user = ldapUserRepo.findByUsernameAndPassword(username, password);
        assertTrue(user.isPresent());
    }
    @Test
    public void givenUsernameAndWrongPassword_ThenFindUserSuccess() {
        String username = "zhaoliu";
        String password = "bad_password";
        Optional<LDAPUser> user = ldapUserRepo.findByUsernameAndPassword(username, password);
        assertTrue(user.isEmpty());
    }
}
