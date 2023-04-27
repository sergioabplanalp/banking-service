package com.abc.query.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountView, UUID> {
    List<AccountView> findAllByBalanceAmountLessThan(BigDecimal amount);
}
