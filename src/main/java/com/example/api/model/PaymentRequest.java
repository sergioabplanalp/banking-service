package com.example.api.model;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @Min(value = 0)
    private Double amount;
}
