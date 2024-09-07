package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Wallet;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query(value = "SELECT * FROM wallets WHERE user_id = :userId", nativeQuery = true)
    Optional<Wallet> findByUserID(Long userId);

    
}
