package com.abc.domain;

import com.abc.commands.CreditAccountCommand;
import com.abc.commands.DebitAccountCommand;
import com.abc.commands.OpenAccountCommand;
import com.abc.query.account.AccountRepository;
import com.abc.query.account.AccountView;
import com.abc.query.transactions.TransactionRepository;
import com.abc.query.transactions.TransactionView;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ApplicationService {
    private final CommandGateway commandGateway;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionsRepository;

    @Autowired
    public ApplicationService(CommandGateway commandGateway, AccountRepository accountRepository, TransactionRepository transactionsRepository) {
        this.commandGateway = commandGateway;
        this.accountRepository = accountRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public UUID openBankAccount(String owner, MonetaryAmount depositAmount, MonetaryAmount creditLine) {
        UUID id = UUID.randomUUID();
        commandGateway.sendAndWait(new OpenAccountCommand(id, owner, depositAmount, creditLine));
        return id;
    }

    public void makeDeposit(UUID accountId, MonetaryAmount amount) {
        commandGateway.sendAndWait(new CreditAccountCommand(accountId, amount));
    }

    public void makeWithdrawal(UUID accountId, MonetaryAmount amount) {
        commandGateway.sendAndWait(new DebitAccountCommand(accountId, amount));
    }

    public List<AccountView> retrieveAccounts(boolean indebted) {
        if (indebted) {
            return accountRepository.findAllByBalanceAmountLessThan(BigDecimal.ZERO);
        }

        return accountRepository.findAll();
    }

    public AccountView retrieveAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);
    }

    public List<TransactionView> retrieveTransactions(UUID accountId, LocalDate date) {
        return Optional.ofNullable(transactionsRepository.findAllByAccountIdAndDateAfter(accountId, date))
                .orElse(Collections.emptyList());
    }
}
