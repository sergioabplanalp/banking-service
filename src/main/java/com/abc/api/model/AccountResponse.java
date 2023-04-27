package com.abc.api.model;

import com.abc.query.account.AccountView;
import com.abc.query.model.MonetaryValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountResponse {
    private UUID accountId;
    private String owner;
    private Balance balance;

    public static AccountResponse from(AccountView accountView) {
        return AccountResponse.builder()
                .accountId(accountView.getId())
                .owner(accountView.getOwner())
                .balance(Balance.from(accountView.getBalance()))
                .build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Balance {
        private BigDecimal amount;
        private String currency;

        public static Balance from(MonetaryValue balance) {
            return Balance.builder()
                    .amount(balance.getAmount())
                    .currency(balance.getCurrency())
                    .build();
        }
    }
}
