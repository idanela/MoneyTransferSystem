package com.elazaridan.moneytransferservice.dto;

import lombok.Data;

@Data
public class UserCreationRequest {
    private String name;
    private String email;
    private String residencyCountry;
    private Boolean isSender;
}
