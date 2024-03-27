package com.elazaridan.moneytransferservice.service;

import com.elazaridan.moneytransferservice.dto.TransferRequest;
import com.elazaridan.moneytransferservice.excptions.PermissionDeniedException;
import com.elazaridan.moneytransferservice.model.Customer;
import com.elazaridan.moneytransferservice.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MoneyTransferServiceTest.class)
class MoneyTransferServiceTest {

    @Mock
    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);


    @InjectMocks
    private MoneyTransferService moneyTransferService;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        customers = new ArrayList<>();
        customers.add(new Customer(1243L, "Idan", "Idan@gmail.com", "Utah", true, 50d));
        customers.add(new Customer(1255L, "Roy", "Roy@Yahoo.com", "New York", false, 100d));

    }

    @Test
    void getRecipientBalance() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customers.get(1)));
        Double amount = moneyTransferService.getRecipientBalance(1255L);
        assertThat(amount).isNotNull();
        assertThat(amount).isEqualTo(customers.get(1).getBalance());
    }

    @Test
    void getRecipientBalanceForNonExistingCustomer() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> moneyTransferService.getRecipientBalance(12055L))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void getRecipientBalanceForNonRecipient() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customers.get(0)));
        assertThatThrownBy(() -> moneyTransferService.getRecipientBalance(12055L))
                .isInstanceOf(PermissionDeniedException.class);

    }

    @Test
    void withdrawMoney() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customers.get(1)));
        boolean successfulWithdrawal = moneyTransferService.withdrawMoney(1255L, 50.5);
        assertThat(successfulWithdrawal).isTrue();

    }

    @Test
    void withdrawMoneyInsufficientFunds() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customers.get(1)));
        boolean successfulWithdrawal = moneyTransferService.withdrawMoney(1255L, 630.5);
        assertThat(successfulWithdrawal).isFalse();
    }

    @Test
    void withdrawMoneyNonRecipient() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(customers.get(0)));
        boolean successfulWithdrawal = moneyTransferService.withdrawMoney(1255L, 60.5);
        assertThat(successfulWithdrawal).isFalse();
    }

    @Test
    void withdrawMoneyCustomerNotExists() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> moneyTransferService.withdrawMoney(12055L, 10d))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void sendMoneyToRecipient() {
        when(customerRepository.findById(1234L)).thenReturn(Optional.ofNullable(customers.get(0)));
        when(customerRepository.findById(1255L)).thenReturn(Optional.ofNullable(customers.get(1)));

        // Create transfer request
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setRecipientId(1255L); // Set recipient ID
        transferRequest.setAmount(100.0); // Set amount

        // Perform the transaction
        boolean result = moneyTransferService.sendMoneyToRecipient(1234L, transferRequest);

        // Verify that the transaction was successful
        assertThat(result).isTrue();
    }

    @Test
    void sendMoneyToNonExistingRecipient() {
        when(customerRepository.findById(1234L)).thenReturn(Optional.ofNullable(customers.get(0)));
        when(customerRepository.findById(1255L)).thenReturn(Optional.empty());

        // Create transfer request
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setRecipientId(1255L); // Set recipient ID
        transferRequest.setAmount(100.0); // Set amount

        assertThatThrownBy(() -> moneyTransferService.sendMoneyToRecipient(1234L, transferRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void sendMoneyFromNonSender() {
        when(customerRepository.findById(1234L)).thenReturn(Optional.empty());
        when(customerRepository.findById(1255L)).thenReturn(Optional.ofNullable(customers.get(1)));

        // Create transfer request
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setRecipientId(1255L); // Set recipient ID
        transferRequest.setAmount(100.0); // Set amount

        assertThatThrownBy(() -> moneyTransferService.sendMoneyToRecipient(1234L, transferRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }
}