package com.abc.domain;

import com.abc.commands.CreditAccountCommand;
import com.abc.commands.DebitAccountCommand;
import com.abc.commands.OpenAccountCommand;
import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountDebitedEvent;
import com.abc.events.AccountOpenedEvent;
import com.abc.events.CreditLimitGrantedEvent;
import com.abc.exception.InsufficientFundsException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.UUID;

@Aggregate
public class Account {

    @AggregateIdentifier
    private UUID accountId;
    private MonetaryAmount currentBalance;
    private MonetaryAmount creditLimit;

    public Account() {
    }

    @CommandHandler
    public Account(OpenAccountCommand command) {
        AggregateLifecycle.apply(new AccountOpenedEvent(command.getAccountId(), command.getOwner()));
        AggregateLifecycle.apply(new CreditLimitGrantedEvent(command.getAccountId(), command.getCreditLine()));
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getAccountId(), LocalDate.now(), command.getDepositAmount()));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getAccountId(), LocalDate.now(), command.getAmount()));
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        MonetaryAmount availableBalance = currentBalance.add(creditLimit);
        if (!hasSufficientFunds(availableBalance, command.getAmount())) {
            throw new InsufficientFundsException("Could not complete transaction. The withdrawal amount would exceed your overdraft limit.");
        }

        AggregateLifecycle.apply(new AccountDebitedEvent(command.getAccountId(), LocalDate.now(), command.getAmount()));
    }

    private boolean hasSufficientFunds(MonetaryAmount availableBalance, MonetaryAmount debitAmount) {
        return availableBalance.subtract(debitAmount).isGreaterThanOrEqualTo(Money.zero(Currency.getDefault()));
    }

    @EventSourcingHandler
    public void handle(AccountOpenedEvent event) {
        this.accountId = event.getAccountId();
        this.currentBalance = Money.zero(Currency.getDefault());
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
