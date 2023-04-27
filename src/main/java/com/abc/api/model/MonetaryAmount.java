package com.abc.api.model;

import com.abc.query.model.MonetaryValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MonetaryAmount {
    private BigDecimal amount;
    private String currency;

    public static MonetaryAmount from(MonetaryValue amount) {
        return MonetaryAmount.builder()
                .amount(amount.getAmount())
                .currency(amount.getCurrency())
                .build();
    }
}
