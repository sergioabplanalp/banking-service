package com.abc.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public class AbstractAccountTest {
    protected final UUID ACCOUNT_ID = UUID.randomUUID();
    protected final String OWNER = "Alice";

    protected FixtureConfiguration<Account> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(Account.class);
    }
}
