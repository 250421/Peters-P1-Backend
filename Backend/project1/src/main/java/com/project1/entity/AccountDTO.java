package com.project1.entity;

public class AccountDTO {
    private Integer accountId;
    private String email;

    private String role;


    public AccountDTO(Integer accountId, String email, String role) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
    }

    // Getters only
    public Integer getAccountId() { return accountId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}