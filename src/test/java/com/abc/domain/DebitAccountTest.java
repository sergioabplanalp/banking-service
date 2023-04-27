package com.abc.domain;

import com.abc.commands.DebitAccountCommand;
import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountDebitedEvent;
import com.abc.events.AccountOpenedEvent;
import com.abc.events.CreditLimitGrantedEvent;
import com.abc.exception.InsufficientFundsException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DebitAccountTest extends AbstractAccountTest {

    @Test
    public void successful_lessThanLimit() {
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
    public void successful_equalToLimit() {
        Money debitAmount = Money.of(600, Currency.getDefault());
        fixture.given(
                    new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                    new CreditLimitGrantedEvent(ACCOUNT_ID, Money.of(100, Currency.getDefault())),
                    new AccountCreditedEvent(ACCOUNT_ID, LocalDate.now(), Money.of(500, Currency.getDefault()))
                )
                .when(new DebitAccountCommand(ACCOUNT_ID, debitAmount))
                .expectEvents(new AccountDebitedEvent(ACCOUNT_ID, LocalDate.now(), debitAmount));
    }

    @Test
    public void unsuccessful_insufficientFunds() {
        Money debitAmount = Money.of(850, Currency.getDefault());
        fixture.given(
                    new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                    new CreditLimitGrantedEvent(ACCOUNT_ID, Money.of(100, Currency.getDefault()))
                )
                .when(new DebitAccountCommand(ACCOUNT_ID, debitAmount))
                .expectException(InsufficientFundsException.class);
    }
}
