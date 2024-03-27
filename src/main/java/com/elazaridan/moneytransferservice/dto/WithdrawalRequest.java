package com.elazaridan.moneytransferservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {
    @NotNull(message = "amount can't be empty")
    @Positive(message = "amount should be a positive number")
    Double amount;
}
