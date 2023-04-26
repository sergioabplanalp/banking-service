package com.example.domain;

import com.example.domain.commands.CreditAccountCommand;
import com.example.domain.commands.DebitAccountCommand;
import com.example.domain.commands.OpenAccountCommand;
import com.example.domain.events.AccountCreditedEvent;
import com.example.domain.events.AccountDebitedEvent;
import com.example.domain.events.AccountOpenedEvent;
import com.example.domain.events.CreditLimitGrantedEvent;
import com.example.exception.InsufficientFundsException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Aggregate
public class Account {

    private static final MonetaryAmount DEFAULT_CREDIT_LIMIT = Money.of(BigDecimal.valueOf(100), Currency.getDefault());

    @AggregateIdentifier
    private UUID accountId;
    private MonetaryAmount currentBalance = Money.zero(Currency.getDefault());
    private MonetaryAmount creditLimit;

    public Account() {
    }

    @CommandHandler
    public Account(OpenAccountCommand command) {
        AggregateLifecycle.apply(new AccountOpenedEvent(command.getAccountId(), command.getOwner()));
        AggregateLifecycle.apply(new CreditLimitGrantedEvent(command.getAccountId(), DEFAULT_CREDIT_LIMIT));
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getAccountId(), LocalDate.now(), command.getDepositAmount()));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getAccountId(), LocalDate.now(), command.getAmount()));
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        MonetaryAmount availableBalance = currentBalance.add(creditLimit);
        if (availableBalance.subtract(command.getAmount()).isLessThan(Money.zero(Currency.getDefault()))) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        AggregateLifecycle.apply(new AccountDebitedEvent(command.getAccountId(), LocalDate.now(), command.getAmount()));
    }

    @EventSourcingHandler
    public void handle(AccountOpenedEvent event) {
        this.accountId = event.getAccountId();
    }

    @EventSourcingHandler
    public void handle(CreditLimitGrantedEvent event) {
        this.creditLimit = event.getAmount();
    }

    @EventSourcingHandler
    public void handle(AccountCreditedEvent event) {
        this.currentBalance = this.currentBalance.add(event.getAmount());
    }

    @EventSourcingHandler
    public void handle(AccountDebitedEvent event) {
        this.currentBalance = this.currentBalance.subtract(event.getAmount());
    }
}
