package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);
    
    @Query(value = "SELECT u.id FROM users u WHERE u.phone = :phone", nativeQuery = true)
    Long findIdByPhone(String phone);

}
