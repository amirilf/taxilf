package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
