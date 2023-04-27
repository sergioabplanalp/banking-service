package com.abc.api;

import com.abc.domain.ApplicationService;
import com.abc.api.model.*;
import com.abc.domain.Currency;
import com.abc.exception.InsufficientFundsException;
import com.abc.query.account.AccountView;
import com.abc.query.transactions.TransactionView;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final ApplicationService applicationService;

    @Autowired
    public AccountController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<UUID> openBankAccount(@RequestBody OpenAccountRequest request) {
        MonetaryAmount depositAmount = Money.of(request.getDepositAmount(), Currency.getDefault());
        UUID id = applicationService.openBankAccount(request.getOwner(), depositAmount);
        return ResponseEntity.ok(id);
    }

    @GetMapping()
    public ResponseEntity<List<AccountResponse>> retrieveAccounts(@RequestParam(required = false) boolean indebted) {
        List<AccountView> accounts = applicationService.retrieveAccounts(indebted);
        List<AccountResponse> response = accounts.stream().map(AccountResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/deposits")
    public ResponseEntity<Void> deposit(@PathVariable String accountId, @RequestBody PaymentRequest request) {
        MonetaryAmount amount = Money.of(BigDecimal.valueOf(request.getAmount()), Currency.getDefault());
        applicationService.makeDeposit(UUID.fromString(accountId), amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdrawals")
    public ResponseEntity<Void> withdraw(@PathVariable String accountId, @RequestBody PaymentRequest request) {
        MonetaryAmount amount = Money.of(BigDecimal.valueOf(request.getAmount()), Currency.getDefault());
        applicationService.makeWithdrawal(UUID.fromString(accountId), amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{accountId}")
    public ResponseEntity<AccountResponse> retrieveAccount(@PathVariable String accountId) {
        AccountView accountView = applicationService.retrieveAccount(UUID.fromString(accountId));
        AccountResponse response = AccountResponse.from(accountView);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> retrieveTransactions(@PathVariable String accountId, @RequestParam LocalDate date) {
        List<TransactionView> transactions = applicationService.retrieveTransactions(UUID.fromString(accountId), date);
        List<TransactionResponse> response = transactions.stream().map(TransactionResponse::from).collect(Collectors.toList());
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
