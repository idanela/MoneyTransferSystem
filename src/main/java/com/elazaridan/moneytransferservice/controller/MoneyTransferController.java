package com.elazaridan.moneytransferservice.controller;

import com.elazaridan.moneytransferservice.dto.TransferRequest;
import com.elazaridan.moneytransferservice.dto.UserCreationRequest;
import com.elazaridan.moneytransferservice.dto.WithdrawalRequest;
import com.elazaridan.moneytransferservice.excptions.PermissionDeniedException;
import com.elazaridan.moneytransferservice.model.Customer;
import com.elazaridan.moneytransferservice.service.MoneyTransferService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/moneyManagement")
@RequiredArgsConstructor
public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    @GetMapping("{recipientId}")
    public ResponseEntity<String> getRecipientBalance(@PathVariable Long recipientId) {
        try {
            Double recipientBalance = moneyTransferService.getRecipientBalance(recipientId);
            return ResponseEntity.ok("Recipient current balance is: " + recipientBalance);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Customer with ID of " + recipientId + " is not authorized to view balance");
        }
    }

    @PutMapping("withdraw/{recipientId}")
    public ResponseEntity<String> withdrawMoney(@PathVariable Long recipientId, @Valid @RequestBody WithdrawalRequest request) {
        try {
            boolean successfulWithdrawal = moneyTransferService.withdrawMoney(recipientId, request.getAmount());
            if (successfulWithdrawal) {
                return ResponseEntity.ok("Withdrawal successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient funds or unAuthorized customer");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
    }

    @PutMapping("transfer/{senderId}")
    public ResponseEntity<String> sendMoneyToRecipient(@PathVariable Long senderId, @Valid @RequestBody TransferRequest transferRequest) {
        try {
            boolean transactionSuccessful = moneyTransferService.sendMoneyToRecipient(senderId, transferRequest);
            if (transactionSuccessful) {
                return ResponseEntity.ok("Money wired successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sender or recipient is not authorized to receive/send money");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender or recipient not found");
        }

    }

    @PostMapping
    public void createUser(@RequestBody UserCreationRequest userCreationRequest) {
        moneyTransferService.createUser(userCreationRequest);
    }

    @GetMapping
    List<Customer> getAllUsers() {
        return moneyTransferService.getAllCustomers();
    }


}
