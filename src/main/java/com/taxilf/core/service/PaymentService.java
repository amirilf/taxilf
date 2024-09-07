package com.taxilf.core.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.entity.Transaction;
import com.taxilf.core.model.entity.Wallet;
import com.taxilf.core.model.enums.TransactionStatus;
import com.taxilf.core.model.enums.TransactionType;
import com.taxilf.core.model.repository.TransactionRepository;
import com.taxilf.core.model.repository.WalletRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;

@Service
public class PaymentService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    PaymentService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    
    public Double getBalance() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found.")).getBalance();
    }

    public ResponseEntity<String> deposit(Double amount) {

        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));
        Double newAmount = wallet.getBalance() + amount;
        wallet.setBalance(newAmount);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .status(TransactionStatus.SUCCESS)
            .type(cup.getRole().equals("PASSENGER") ? TransactionType.PASSENGER_DEPOSIT : TransactionType.DRIVER_DEPOSIT)
            .wallet(wallet)
            .build();
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("The wallet has been charged successfully. Final amount:" + newAmount);
    }

    public ResponseEntity<String> withdrawal(Double amount) {
    
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));

        if (wallet.getBalance() < amount) {
            throw new CustomBadRequestException("Insufficient balance.");
        }

        Double newAmount = wallet.getBalance() - amount;
        wallet.setBalance(newAmount);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .status(TransactionStatus.SUCCESS)
            .type(cup.getRole().equals("PASSENGER") ? TransactionType.PASSENGER_WITHDRAWAL : TransactionType.DRIVER_WITHDRAWAL)
            .wallet(wallet)
            .build();
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("The amount of " + amount + " has been successfully withdrawn from the wallet. Final amount:" + newAmount);
    }
}
