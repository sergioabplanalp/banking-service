package com.abc.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class AccountDebitedEvent {
    private UUID accountId;
    private LocalDate date;
    private MonetaryAmount amount;
}
