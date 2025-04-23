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
        accountRepository.save(account);
    }

    //logs in a user if the username and password combination are valid
    public Account login(String email, String rawPassword) {
        Account account = accountRepository.findByEmail(email);
        if (account != null && passwordEncoder.matches(rawPassword, account.getPassword())) {
            return account;
        }
        return null;
    }

    //checks to see if a username is associated with an account or not
    //returns an account object of the account with the username if it exists, null otherwise
    public Account findByUsername(String username) {
        if (accountRepository.findByEmail(username) != null) {
            return accountRepository.findByEmail(username);
        }
        return null;
    }

    //checks to see if a password is associated with an account or not
    //returns an account object of the account with the password if it exists, null otherwise
    public Account findByPassword(String password) {
        if (accountRepository.findByPassword(password) != null) {
            return accountRepository.findByEmail(password);
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
