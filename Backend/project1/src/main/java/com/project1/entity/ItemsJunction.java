package com.project1.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "itemsjunction")
public class ItemsJunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    // Constructors
    public ItemsJunction() {}

    public ItemsJunction(Item item, Account user) {
        this.item = item;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }
}