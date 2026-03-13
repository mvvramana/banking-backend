package com.banking.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banking.app.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}