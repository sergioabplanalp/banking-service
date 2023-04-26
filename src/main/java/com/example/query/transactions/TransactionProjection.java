package com.example.query.transactions;

import com.example.domain.events.AccountCreditedEvent;
import com.example.domain.events.AccountDebitedEvent;
import com.example.query.model.MonetaryValue;
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
