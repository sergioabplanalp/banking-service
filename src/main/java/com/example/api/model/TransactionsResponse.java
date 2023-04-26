package com.example.api.model;

import com.example.query.transactions.TransactionView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TransactionsResponse {
    private UUID accountId;
    private List<Transaction> transactions;

    public static TransactionsResponse from(UUID accountId, List<TransactionView> transactionViews) {
        List<Transaction> transactions = CollectionUtils.isEmpty(transactionViews) ? Collections.emptyList() :
                transactionViews.stream().map(Transaction::from).collect(Collectors.toList());
        return TransactionsResponse.builder()
                .accountId(accountId)
                .transactions(transactions)
                .build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    private static class Transaction {
        private UUID transactionId;
        private LocalDate date;
        private MonetaryAmount amount;

        public static Transaction from(TransactionView transactionView) {
            return Transaction.builder()
                    .transactionId(transactionView.getTransactionId())
                    .date(transactionView.getDate())
                    .amount(MonetaryAmount.from(transactionView.getAmount()))
                    .build();
        }
    }
}
