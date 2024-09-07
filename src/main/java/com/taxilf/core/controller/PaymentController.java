package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @GetMapping("/balance")
    public Double balance() {
        return paymentService.getBalance();
    }

    @GetMapping("/deposit/{amount}")
    public ResponseEntity<String> deposit(@RequestParam Double amount) {
        return null;
    }

    @GetMapping("/withdrawal/{amount}")
    public ResponseEntity<String> withdrawal(@RequestParam Double amount) {
        return null;
    }

    @GetMapping("/transactions")
    public Double getBalance() {
        return null;
    }

    @GetMapping("/transactions/{id}")
    public Double getBalance(@RequestParam Long id) {
        return null;
    }

}
