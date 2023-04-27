package com.abc.query.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class MonetaryValue {
    private BigDecimal amount;
    private String currency;

    public static MonetaryValue from(MonetaryAmount amount) {
        return MonetaryValue.builder()
                .amount(amount.getNumber().numberValueExact(BigDecimal.class))
                .currency(amount.getCurrency().getCurrencyCode())
                .build();
    }

    public MonetaryAmount toMonetaryAmount() {
        return Money.of(amount, currency);
    }
}
