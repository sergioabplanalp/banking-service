package com.example.api.model;

import com.example.query.transactions.TransactionView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TransactionResponse {
    private UUID transactionId;
    private LocalDate date;
    private MonetaryAmount amount;

    public static TransactionResponse from(TransactionView transactionView) {
        return TransactionResponse.builder()
                .transactionId(transactionView.getTransactionId())
                .date(transactionView.getDate())
                .amount(MonetaryAmount.from(transactionView.getAmount()))
                .build();
    }
}
