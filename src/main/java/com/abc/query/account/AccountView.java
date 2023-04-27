package com.abc.query.account;

import com.abc.domain.Currency;
import com.abc.query.model.MonetaryValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class AccountView {
    @Id
    private UUID id;
    private String owner;
    @Embedded
    private MonetaryValue balance;

    public AccountView(UUID accountId, String owner) {
        this.id = accountId;
        this.owner = owner;
        this.balance = MonetaryValue.from(Money.zero(Currency.getDefault()));
    }

    public void credit(MonetaryAmount amount) {
        MonetaryAmount updatedBalance = balance.toMonetaryAmount().add(amount);
        balance = MonetaryValue.from(updatedBalance);
    }

    public void debit(MonetaryAmount amount) {
        MonetaryAmount updatedBalance = balance.toMonetaryAmount().subtract(amount);
        balance = MonetaryValue.from(updatedBalance);
    }
}
