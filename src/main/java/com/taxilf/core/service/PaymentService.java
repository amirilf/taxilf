package com.taxilf.core.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.repository.WalletRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;

@Service
public class PaymentService {

    private final WalletRepository walletRepository;

    PaymentService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    
    public Double getBalance() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found.")).getBalance();
    }

}
