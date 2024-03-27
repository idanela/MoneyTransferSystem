package com.elazaridan.moneytransferservice.contoller;

import com.elazaridan.moneytransferservice.controller.MoneyTransferController;
import com.elazaridan.moneytransferservice.dto.TransferRequest;
import com.elazaridan.moneytransferservice.dto.WithdrawalRequest;
import com.elazaridan.moneytransferservice.excptions.PermissionDeniedException;
import com.elazaridan.moneytransferservice.model.Customer;
import com.elazaridan.moneytransferservice.service.MoneyTransferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MoneyTransferController.class)

public class CustomersControllerTest {
    @MockBean
    private MoneyTransferService moneyTransferService;

    private MockMvc mockMvc;
    private List<Customer> customers;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MoneyTransferController(moneyTransferService)).defaultRequest(get("/").accept(MediaType.APPLICATION_JSON)).build();
    }

    @Test
    void getBalanceOfRecipient() throws Exception {
        Mockito.when(this.moneyTransferService.getRecipientBalance(anyLong())).thenReturn(100d);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/moneyManagement/1255"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void getBalanceOfNonExistingRecipient() throws Exception {
        Mockito.when(this.moneyTransferService.getRecipientBalance(anyLong())).thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/moneyManagement/1255"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void getBalanceOfNonRecipient() throws Exception {
        Mockito.when(this.moneyTransferService.getRecipientBalance(anyLong())).thenThrow(new PermissionDeniedException(""));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/moneyManagement/1255"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void withdrawMoney() throws Exception {
        Mockito.when(this.moneyTransferService.withdrawMoney(anyLong(),anyDouble())).thenReturn(true);

        WithdrawalRequest  request= new WithdrawalRequest(15.5d);

        mockMvc.perform(put("/api/moneyManagement/withdraw/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void withdrawMoneyInsufficientFundsOrUnAuthorizedCustomer() throws Exception {
        Mockito.when(this.moneyTransferService.withdrawMoney(anyLong(),anyDouble())).thenReturn(false);

        WithdrawalRequest  request= new WithdrawalRequest(150d);

        mockMvc.perform(put("/api/moneyManagement/withdraw/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void withdrawMoneyCustomerNotFound() throws Exception {
        Mockito.when(this.moneyTransferService.withdrawMoney(anyLong(),anyDouble())).thenThrow(new EntityNotFoundException());

        WithdrawalRequest  request= new WithdrawalRequest(15.5d);

        mockMvc.perform(put("/api/moneyManagement/withdraw/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void sendMoneyToRecipient() throws Exception {
        Mockito.when(this.moneyTransferService.sendMoneyToRecipient(anyLong(),any())).thenReturn(true);

        TransferRequest  request= new TransferRequest(123L,150d);

        mockMvc.perform(put("/api/moneyManagement/transfer/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void sendMoneyToNonRecipientOrSentByNonSender() throws Exception {
        Mockito.when(this.moneyTransferService.sendMoneyToRecipient(anyLong(),any())).thenReturn(false);

        TransferRequest  request= new TransferRequest(123L,150d);

        mockMvc.perform(put("/api/moneyManagement/transfer/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void sendMoneySenderOrRecipientNotFound() throws Exception {
        Mockito.when(this.moneyTransferService.sendMoneyToRecipient(anyLong(),any())).thenThrow(new EntityNotFoundException());

        TransferRequest  request= new TransferRequest(123L,150d);

        mockMvc.perform(put("/api/moneyManagement/transfer/1255")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }






    private String asJsonString(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }


}
