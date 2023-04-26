package com.example.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.money.MonetaryAmount;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class CreditLimitGrantedEvent {
    private UUID accountId;
    private MonetaryAmount amount;
}
