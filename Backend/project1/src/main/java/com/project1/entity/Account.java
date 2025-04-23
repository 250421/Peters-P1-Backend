package com.project1.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class Account {
    
    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
   
    public Account(){

    }

    public Account(String email, String password){
        this.email = email;
        this.password = password;
    }
    
    public Account(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
    
    public Integer getAccountId() {
        return id;
    }
   
    public void setAccountId(Integer accountId) {
        this.id = accountId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}