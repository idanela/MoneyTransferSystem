package com.elazaridan.moneytransferservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @NotNull
    Long recipientId;

    @NotNull
    @Positive(message = "amount must be a positive number")
    @Max(value = 1000,message = "The number must be less than or equal to {value}")
    Double amount;
}
