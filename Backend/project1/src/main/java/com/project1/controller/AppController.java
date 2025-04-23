package com.project1.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project1.entity.Account;
import com.project1.service.AccountService;

@RestController
public class AppController {

    private AccountService accountService;

    @Autowired
    public AppController(AccountService accountService) {
        this.accountService = accountService;
    }

    //@param: An account object of an account that needs to be registered
    //registers a new user if the account info does not already exist and the username and password
    //are the appropriate length
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        if(accountService.findByUsername(account.getEmail()) != null) {
            return ResponseEntity.status(409).build();
        }
        if (account.getEmail().length() < 1){
            return ResponseEntity.status(400).body("Email cant be empty");
        }
        if (account.getPassword().length() < 8) {
            return ResponseEntity.status(400).body("Password must be at least 8 characters long");
        }
        if (!account.getPassword().matches(".*[a-z].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one lowercase letter");
        }
        if (!account.getPassword().matches(".*[A-Z].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one uppercase letter");
        }
        if (!account.getPassword().matches(".*\\d.*")) {
            return ResponseEntity.status(400).body("Password must contain at least one number");
        }
        if (!account.getPassword().matches(".*[@$!%*?&].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one special character (@$!%*?&)");
        }
        
        System.out.println("Register called: " + account.getEmail());

        accountService.registerNewUser(account);
        return ResponseEntity.status(200)
            .body(accountService.findByUsername(account.getEmail()));

    }

    //@param: account object of the account that is trying to login
    //"logs in" the account if the username and password combination are valid
    /*@PostMapping("/login")
    public ResponseEntity<Account> login (@RequestBody Account account) {
        Account found = accountService.login(account.getEmail(), account.getPassword());
        System.out.println("Login attempt: " + account.getEmail() + ", " + account.getPassword());
        System.out.println("Found account: " + found);

        if (found == null) {
            System.out.println("NULL");
            return ResponseEntity.status(401).build();
        } else {
            System.out.println("NOT NULL");
            return ResponseEntity.status(200).body(found);
        }
    }*/

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Account account, HttpServletRequest request) {
        Account found = accountService.login(account.getEmail(), account.getPassword());
        if (found == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Ensure session creation
        HttpSession session = request.getSession(true); // Creates a new session if none exists
        session.setAttribute("user", found);

        return ResponseEntity.ok("Login successful");
    }


    @GetMapping("/profile")
    public ResponseEntity<String> profile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Account user = (Account) session.getAttribute("user");
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(403).body("Access denied: User not logged in.");
        }
        //Account user = (Account) session.getAttribute("user");
        return ResponseEntity.ok("User profile: " + user.getEmail());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // Invalidate the session
        }
        return ResponseEntity.ok("Logged out successfully.");
    }


}