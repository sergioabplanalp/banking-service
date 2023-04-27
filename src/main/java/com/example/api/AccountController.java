package com.example.api;

import com.example.api.model.*;
import com.example.domain.Currency;
import com.example.domain.commands.CreditAccountCommand;
import com.example.domain.commands.DebitAccountCommand;
import com.example.domain.commands.OpenAccountCommand;
import com.example.exception.InsufficientFundsException;
import com.example.query.account.AccountRepository;
import com.example.query.account.AccountView;
import com.example.query.transactions.TransactionRepository;
import com.example.query.transactions.TransactionView;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CommandGateway commandGateway;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionsRepository;

    @Autowired
    public AccountController(CommandGateway commandGateway, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.commandGateway = commandGateway;
        this.accountRepository = accountRepository;
        this.transactionsRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<UUID> openBankAccount(@RequestBody OpenAccountRequest request) {
        UUID id = UUID.randomUUID();
        Money depositAmount = Money.of(request.getDepositAmount(), Currency.getDefault());
        commandGateway.sendAndWait(new OpenAccountCommand(id, request.getOwner(), depositAmount));
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> retrieveAccounts() {
        List<AccountView> accounts = accountRepository.findAll();
        List<AccountResponse> response = accounts.stream().map(AccountResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/deposits")
    public ResponseEntity<Void> deposit(@PathVariable String accountId, @RequestBody PaymentRequest request) {
        Money depositAmount = Money.of(BigDecimal.valueOf(request.getAmount()), Currency.getDefault());
        commandGateway.sendAndWait(new CreditAccountCommand(UUID.fromString(accountId), depositAmount));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdrawals")
    public ResponseEntity<Void> withdraw(@PathVariable String accountId, @RequestBody PaymentRequest request) {
        Money depositAmount = Money.of(BigDecimal.valueOf(request.getAmount()), Currency.getDefault());
        commandGateway.sendAndWait(new DebitAccountCommand(UUID.fromString(accountId), depositAmount));
        return ResponseEntity.ok().build();
    }

    @GetMapping("{accountId}")
    public ResponseEntity<AccountResponse> retrieveAccount(@PathVariable String accountId) {
        AccountView accountView = accountRepository.findById(UUID.fromString(accountId)).orElseThrow(IllegalArgumentException::new);
        AccountResponse response = AccountResponse.from(accountView);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> retrieveTransactions(@PathVariable String accountId, @RequestParam LocalDate date) {
        UUID id = UUID.fromString(accountId);
        List<TransactionView> transactions = transactionsRepository.findAllByAccountIdAndDateAfter(id, date);
        if (CollectionUtils.isEmpty(transactions)) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<TransactionResponse> response = transactions.stream().map(TransactionResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/indebted")
    public ResponseEntity<List<AccountResponse>> retrieveIndebtedAccounts() {
        List<AccountView> accounts = accountRepository.findAllByBalanceAmountLessThan(BigDecimal.ZERO);
        List<AccountResponse> response = accounts.stream().map(AccountResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException e) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity.unprocessableEntity().body(error);
    }
}
