package com.project1.entity;

public class AccountDTO {
    private Integer id;
    private String email;
    private String role;

    public AccountDTO () {

    }

    public AccountDTO(Integer id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
