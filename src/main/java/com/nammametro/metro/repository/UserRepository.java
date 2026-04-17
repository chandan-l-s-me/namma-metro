package com.nammametro.metro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nammametro.metro.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}