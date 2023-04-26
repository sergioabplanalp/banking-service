package com.example.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class AccountOpenedEvent {
    private UUID accountId;
    private String owner;
}
