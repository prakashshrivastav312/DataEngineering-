package com.periNimble.custom.processors.sample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private double user_wallet;

    // Constructors, getters, and setters

    public User() {
    }

    public User(double user_wallet) {
        this.user_wallet = user_wallet;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public double getUserWallet() {
        return user_wallet;
    }

    public void setUserWallet(double user_wallet) {
        this.user_wallet = user_wallet;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_wallet=" + user_wallet +
                '}';
    }
}
