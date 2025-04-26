package com.project1.controller;

import com.project1.ErrorResponse;
import com.project1.entity.AccountDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project1.entity.Account;
import com.project1.service.AccountService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class AppController {
    private AccountService accountService;

    @Autowired
    public AppController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody Account account, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Check if the account already exists
        if (accountService.findByUsername(account.getEmail()) != null) {
            ErrorResponse e = new ErrorResponse("Email already exists");
            return ResponseEntity.status(409).body(e);
        }

        // Validate account fields
        if (account.getEmail().length() < 1) {
            ErrorResponse e = new ErrorResponse("Email can't be empty");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getEmail().matches(".*[@].*")) {
            ErrorResponse e = new ErrorResponse("Invalid Email");
            return ResponseEntity.status(400).body(e);
        }
        if (account.getPassword().length() < 8) {
            ErrorResponse e = new ErrorResponse("Password must be at least 8 characters long");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[a-z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one lowercase letter\"");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[A-Z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one uppercase letter");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*\\d.*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one number");
            return ResponseEntity.status(400).body(e);
        }
        if (!account.getPassword().matches(".*[@$!%*?&].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one special character (@$!%*?&)");
            return ResponseEntity.status(400).body(e);
        }

        // Register the new user
        accountService.registerNewUser(account);
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getEmail(), account.getRole());
        // Return success response
        return ResponseEntity.status(201)
                .body(accountDTO);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody Account account, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false); // false = don't create a new session if none exists
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Attempt login
        Account found = accountService.login(account.getEmail(), account.getPassword());
        if (found == null) {
            ErrorResponse e = new ErrorResponse("Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }

        // Create a session and store user information
        session = request.getSession(true); // Create a new session if none exists
        AccountDTO dto = new AccountDTO(found.getId(), found.getEmail(), found.getRole());
        session.setAttribute("user", dto);

        // Return success response with user information
        //AccountDTO accountDTO = new AccountDTO(found.getAccountId(), found.getEmail(), found.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

   // @GetMapping("/profile")
    @GetMapping("")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Session is null");
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        Object user = session.getAttribute("user");
        if (user == null) {
            System.out.println("User session attribute is null");
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        AccountDTO accountDTO = (AccountDTO) user;
        return ResponseEntity.ok(accountDTO);
    }

    /*@PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // Invalidate the session
            return ResponseEntity.ok("Logged out successfully.");
        }
        return ResponseEntity.ok("No active session to log out.");
    }*/


}