package com.project1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project1.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{

    Account findByEmailAndPassword(String email, String password);
    Account findByEmailIgnoreCase(String email);
    Account findByPassword(String password);
    Account findById(int id);
}