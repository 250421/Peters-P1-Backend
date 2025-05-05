package com.project1;

import com.project1.entity.Account;
import com.project1.entity.Item;
import com.project1.repository.AccountRepository;
import com.project1.service.AccountService;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String email = "test@gmail.com";
    private final String rawPassword = "P@ssw0rd";

    private Account user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Account(2,"test@gmail.com", "P@ssw0rd");
    }

    @Test
    void findByAccountId_ValidId() {
        when(accountRepository.findById(2)).thenReturn(user);
        Account result = accountService.findByAccountId(2);
        assertEquals(2, result.getId());
    }

    @Test
    void findByAccountId_InValidId() {
        user.setAccountId(3);
        when(accountRepository.findById(2)).thenReturn(user);
        Account result = accountService.findByAccountId(2);
        assertNotEquals(2, result.getId());
    }

    @Test
    void findByEmail_validEmail() {
        when(accountService.findByUsername("test@gmail.com")).thenReturn(user);
        Account test = accountService.findByUsername("test@gmail.com");
        assertEquals("test@gmail.com", test.getEmail());
    }
    @Test
    void findByEmail_NonExistentEmail() {
        user.setEmail("user@gmail.com");
        when(accountService.findByUsername("test@gmail.com")).thenReturn(user);
        Account test = accountService.findByUsername("test@gmail.com");
        assertNotEquals("test@gmail.com", test.getEmail());
    }

    @Test
    void registersNewUser() {
        Account newUser = new Account();
        newUser.setEmail("test22@gmail.com");
        newUser.setPassword("P@ssw0rd1");

        when(passwordEncoder.encode("P@ssw0rd1")).thenReturn("hashedPassword");

        accountService.registerNewUser(newUser);

        assertEquals("test22@gmail.com", newUser.getEmail());
        assertEquals("hashedPassword", newUser.getPassword());

        verify(accountRepository).save(newUser);
    }

    @Test
    void testLogin_ValidCredentials() {
        //set hashed password
        user.setPassword("hashedPassword123");

        when(accountRepository.findByEmailIgnoreCase(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, "hashedPassword123")).thenReturn(true);
        Account result = accountService.login(email, rawPassword);

        assertNotNull(result);
        assertEquals(email, result.getEmail());

        verify(accountRepository).findByEmailIgnoreCase(email);
        verify(passwordEncoder).matches(rawPassword, "hashedPassword123");
    }

}

