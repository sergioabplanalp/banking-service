package com.abc.query.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionView, UUID> {
    List<TransactionView> findAllByAccountIdAndDateAfter(UUID accountId, LocalDate date);
}
