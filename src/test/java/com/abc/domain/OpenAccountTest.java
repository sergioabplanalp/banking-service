package com.abc.domain;

import com.abc.commands.OpenAccountCommand;
import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountOpenedEvent;
import com.abc.events.CreditLimitGrantedEvent;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class OpenAccountTest extends AbstractAccountTest {

    @Test
    public void successful() {
        Money depositAmount = Money.of(500, Currency.getDefault());
        Money creditLine = Money.of(100, Currency.getDefault());
        fixture.given()
                .when(new OpenAccountCommand(ACCOUNT_ID, OWNER, depositAmount, creditLine))
                .expectEvents(
                        new AccountOpenedEvent(ACCOUNT_ID, OWNER),
                        new CreditLimitGrantedEvent(ACCOUNT_ID, creditLine),
                        new AccountCreditedEvent(ACCOUNT_ID, LocalDate.now(), depositAmount)
                );
    }
}
