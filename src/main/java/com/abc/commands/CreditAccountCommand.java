package com.abc.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import javax.money.MonetaryAmount;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class CreditAccountCommand {
    @TargetAggregateIdentifier
    private UUID accountId;
    private MonetaryAmount amount;
}
