package com.taxilf.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.dto.response.PaymentDTO;
import com.taxilf.core.model.dto.response.TransactionDTO;
import com.taxilf.core.model.dto.response.TransactionsDTO;
import com.taxilf.core.model.entity.Transaction;
import com.taxilf.core.model.entity.Wallet;
import com.taxilf.core.model.enums.TransactionStatus;
import com.taxilf.core.model.enums.TransactionType;
import com.taxilf.core.model.repository.TransactionRepository;
import com.taxilf.core.model.repository.WalletRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    PaymentService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    
    public PaymentDTO getBalance() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));
        return PaymentDTO.builder().final_amount(wallet.getBalance()).build();
    }

    public PaymentDTO deposit(Double amount) {

        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));
        Double newAmount = wallet.getBalance() + amount;
        wallet.setBalance(newAmount);
        walletRepository.save(wallet);

        TransactionType tt = cup.getRole().equals("PASSENGER") ? TransactionType.PASSENGER_DEPOSIT : TransactionType.DRIVER_DEPOSIT;
        TransactionStatus ts = TransactionStatus.SUCCESS;

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .status(ts)
            .type(tt)
            .wallet(wallet)
            .build();
        transactionRepository.save(transaction);

        return PaymentDTO.builder()
            .info("The wallet has been charged successfully.")
            .final_amount(newAmount)
            .amount(amount)
            .status(ts.name())
            .type(tt.name())
            .build();

    }

    public PaymentDTO withdrawal(Double amount) {
    
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));

        if (wallet.getBalance() < amount) {
            throw new CustomBadRequestException("Insufficient balance.");
        }

        Double newAmount = wallet.getBalance() - amount;
        wallet.setBalance(newAmount);
        walletRepository.save(wallet);

        TransactionType tt = cup.getRole().equals("PASSENGER") ? TransactionType.PASSENGER_WITHDRAWAL : TransactionType.DRIVER_WITHDRAWAL;
        TransactionStatus ts = TransactionStatus.SUCCESS;

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .status(ts)
            .type(tt)
            .wallet(wallet)
            .build();
        transactionRepository.save(transaction);

        return PaymentDTO.builder()
            .info("The amount has been successfully withdrawn from the wallet.")
            .final_amount(newAmount)
            .amount(amount)
            .status(ts.name())
            .type(tt.name())
            .build();

        }

    @Transactional
    public TransactionsDTO getTransactionHistory() {

        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));

        List<TransactionDTO> trs = wallet.getTransactions().stream().map(tr -> TransactionDTO.builder().amount(tr.getAmount()).id(tr.getId()).status(tr.getStatus().name()).type(tr.getType().name()).timestamp(tr.getTimestamp()).tripId((tr.getTrip() == null) ? null: tr.getTrip().getId()).build()).collect(Collectors.toList());

        return TransactionsDTO.builder()
            .amount(wallet.getBalance())
            .number_of_transactions(trs.size())
            .transactions(trs)
            .build();

    }

    public TransactionDTO getTransaction(Long id) {

        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet wallet = walletRepository.findByUserID(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("Wallet not found."));

        Transaction tr = transactionRepository.findByWallet_IdAndId(wallet.getId(), id).orElseThrow(() -> new CustomResourceNotFoundException("Transaction not found."));
        
        return TransactionDTO.builder()
            .amount(tr.getAmount())
            .id(tr.getId())
            .status(tr.getStatus().name())
            .type(tr.getType().name())
            .timestamp(tr.getTimestamp())
            .tripId(tr.getTrip() == null ? null : tr.getTrip().getId())
            .build();

    }

}
