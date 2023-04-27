package com.abc.domain;

import com.abc.commands.DebitAccountCommand;
import com.abc.commands.OpenAccountCommand;
import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountDebitedEvent;
import com.abc.events.AccountOpenedEvent;
import com.abc.events.CreditLimitGrantedEvent;
import com.abc.exception.InsufficientFundsException;
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
