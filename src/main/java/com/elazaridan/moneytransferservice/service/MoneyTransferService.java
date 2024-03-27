package com.elazaridan.moneytransferservice.service;

import com.elazaridan.moneytransferservice.dto.TransferRequest;
import com.elazaridan.moneytransferservice.dto.UserCreationRequest;
import com.elazaridan.moneytransferservice.excptions.PermissionDeniedException;
import com.elazaridan.moneytransferservice.model.Customer;
import com.elazaridan.moneytransferservice.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

    private final CustomerRepository customerRepository;


    public Double getRecipientBalance(Long recipientId) {
        Customer recipient = customerRepository.findById(recipientId).
                orElseThrow(() -> new EntityNotFoundException("customer with ID " + recipientId + " not found"));

        if (recipient.getIsSender()) {
            throw new PermissionDeniedException("customer with ID " + recipientId + " is not a recipient");
        }

        return recipient.getBalance();
    }

    @Transactional
    public boolean withdrawMoney(Long recipientId, Double amount) {
        Customer recipient = customerRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        if (recipient.getIsSender() || recipient.getBalance() < amount) {
            return false; // Insufficient funds or invalid recipient ID
        }
        double newBalance = recipient.getBalance() - amount;
        recipient.setBalance(newBalance);

        customerRepository.save(recipient);

        return true; // Withdrawal successful

    }

    public boolean sendMoneyToRecipient(Long senderId, TransferRequest transferRequest) {

        Customer sender = customerRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        if (!sender.getIsSender()) {
            return false; //invalid Sender ID
        }
        Customer recipient = customerRepository.findById(transferRequest.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        if (recipient.getIsSender()) {
            return false; //invalid recipient ID
        }
        double newBalance = recipient.getBalance() + transferRequest.getAmount();
        recipient.setBalance(newBalance);

        customerRepository.save(recipient);

        return true; // Wire successful

    }

    public void createUser(UserCreationRequest userCreationRequest) {
        customerRepository.save(Customer.builder()
                .balance(0d)
                .email(userCreationRequest.getEmail())
                .name(userCreationRequest.getName())
                .isSender(userCreationRequest.getIsSender()).
                residencyCountry(userCreationRequest.getResidencyCountry())
                .build());
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
