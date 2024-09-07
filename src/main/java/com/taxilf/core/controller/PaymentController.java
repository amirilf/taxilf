package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.dto.response.PaymentDTO;
import com.taxilf.core.model.dto.response.TransactionDTO;
import com.taxilf.core.model.dto.response.TransactionsDTO;
import com.taxilf.core.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/balance")
    public PaymentDTO balance() {
        return paymentService.getBalance();
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/deposit/{amount}")
    public PaymentDTO deposit(@PathVariable Double amount) {
        return paymentService.deposit(amount);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/withdrawal/{amount}")
    public PaymentDTO withdrawal(@PathVariable Double amount) {
        return paymentService.withdrawal(amount);
    }

    @GetMapping("/transactions")
    public TransactionsDTO getTransactionHistory() {
        return paymentService.getTransactionHistory();
    }

    @GetMapping("/transactions/{id}")
    public TransactionDTO getBalance(@PathVariable Long id) {
        return paymentService.getTransaction(id);
    }

}
