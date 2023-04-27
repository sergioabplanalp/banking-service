package com.abc.query.account;

import com.abc.events.AccountCreditedEvent;
import com.abc.events.AccountDebitedEvent;
import com.abc.events.AccountOpenedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountProjection {

    private final AccountRepository repository;

    @Autowired
    public AccountProjection(AccountRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void handle(AccountOpenedEvent event) {
        repository.save(new AccountView(event.getAccountId(), event.getOwner()));
    }

    @EventHandler
    public void handle(AccountCreditedEvent event) {
        AccountView accountView = repository.findById(event.getAccountId()).orElseThrow(IllegalArgumentException::new);
        accountView.credit(event.getAmount());
        repository.save(accountView);
    }

    @EventHandler
    public void handle(AccountDebitedEvent event) {
        AccountView accountView = repository.findById(event.getAccountId()).orElseThrow(IllegalArgumentException::new);
        accountView.debit(event.getAmount());
        repository.save(accountView);
    }
}
