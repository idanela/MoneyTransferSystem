package com.elazaridan.moneytransferservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest  {
    @NotNull(message = "amount can't be empty")
    @Positive(message = "amount should be a positive number")
    Double amount;
}
