package com.example.domain;

import com.example.domain.commands.DebitAccountCommand;
import com.example.domain.commands.OpenAccountCommand;
import com.example.domain.events.AccountCreditedEvent;
import com.example.domain.events.AccountDebitedEvent;
import com.example.domain.events.AccountOpenedEvent;
import com.example.domain.events.CreditLimitGrantedEvent;
import com.example.exception.InsufficientFundsException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

public class AccountTest {
    private final UUID ACCOUNT_ID = UUID.randomUUID();
    private final String OWNER = "Alice";

    private FixtureConfiguration<Account> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    public void openAccount() {
        Money depositAmount = Money.of(500, Currency.getDefault());
        fixture.given()
                .when(new OpenAccountCommand(ACCOUNT_ID, OWNER, depositAmount))
                .expectEvents(
                        new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                        new CreditLimitGrantedEvent(ACCOUNT_ID, Money.of(100, Currency.getDefault())),
                        new AccountCreditedEvent(ACCOUNT_ID, LocalDate.now(), Money.of(500, Currency.getDefault()))
                );
    }

    @Test
    public void debitAccount_successful() {
        Money debitAmount = Money.of(450, Currency.getDefault());
        fixture.given(
                    new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                    new CreditLimitGrantedEvent(ACCOUNT_ID, Money.of(100, Currency.getDefault())),
                    new AccountCreditedEvent(ACCOUNT_ID, LocalDate.now(), Money.of(500, Currency.getDefault()))
                )
                .when(new DebitAccountCommand(ACCOUNT_ID, debitAmount))
                .expectEvents(new AccountDebitedEvent(ACCOUNT_ID, LocalDate.now(), debitAmount));
    }

    @Test
    public void debitAccount_insufficientFunds() {
        Money debitAmount = Money.of(850, Currency.getDefault());
        fixture.given(
                    new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                    new CreditLimitGrantedEvent(ACCOUNT_ID, Money.of(100, Currency.getDefault()))
                )
                .when(new DebitAccountCommand(ACCOUNT_ID, debitAmount))
                .expectException(InsufficientFundsException.class);
    }
}
