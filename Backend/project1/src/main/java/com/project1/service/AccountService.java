package com.project1.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project1.entity.Account;
import com.project1.repository.AccountRepository;



@Service
public class AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void registerNewUser(Account account) {
        String rawPassword = account.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        account.setPassword(encodedPassword);
        account.setRole("USER");
        accountRepository.save(account);
    }

    //logs in a user if the username and password combination are valid
    public Account login(String email, String rawPassword) {
        Account account = accountRepository.findByEmailIgnoreCase(email);
        if (account != null && passwordEncoder.matches(rawPassword, account.getPassword())) {
            return account;
        }
        return null;
    }

    //checks to see if a username is associated with an account or not
    //returns an account object of the account with the username if it exists, null otherwise
    public Account findByUsername(String username) {
        if (accountRepository.findByEmailIgnoreCase(username) != null) {
            return accountRepository.findByEmailIgnoreCase(username);
        }
        return null;
    }

    //checks to see if a accountId is associated with an account or not
    //returns an account object of the account with the accountId if it exists, null otherwise
    public Account findByAccountId(int accountId) {
        if (accountRepository.findById(accountId) == null) {
            return null;
        } else {
            return accountRepository.findById(accountId);
        }
    }
}
