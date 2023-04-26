package com.example.query.transactions;

import com.example.query.model.MonetaryValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionView {
    @Id
    private UUID transactionId;
    private UUID accountId;
    private LocalDate date;
    @Embedded
    private MonetaryValue amount;
}
