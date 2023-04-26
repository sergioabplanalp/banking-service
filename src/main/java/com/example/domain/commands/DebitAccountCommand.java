package com.example.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import javax.money.MonetaryAmount;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class DebitAccountCommand {
    @TargetAggregateIdentifier
    private UUID accountId;
    private MonetaryAmount amount;
}
