package com.abc.query.transactions;

import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountDebitedEvent;
import com.abc.query.model.MonetaryValue;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionProjection {

    private final TransactionRepository repository;

    @Autowired
    public TransactionProjection(TransactionRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void handle(AccountCreditedEvent event) {
        repository.save(new TransactionView(UUID.randomUUID(), event.getAccountId(), event.getDate(), MonetaryValue.from(event.getAmount())));
    }

    @EventHandler
    public void handle(AccountDebitedEvent event) {
        MonetaryValue value = MonetaryValue.from(event.getAmount().negate());
        repository.save(new TransactionView(UUID.randomUUID(), event.getAccountId(), event.getDate(), value));
    }
}
