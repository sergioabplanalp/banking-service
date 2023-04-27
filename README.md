# ABC Banking Service
ABC Banking Service is a simple banking service that simulates opening an account, making deposits, making withdrawals and viewing different types of information.

It is written in Java using Spring Boot and Axon frameworks and makes use of event sourcing and the CQRS architectural pattern.

---

## Usage

### Option 1 - Backend only

#### Prerequisites
- Java 17
- Maven

#### Installation
```
mvn clean package
```

#### Running the application
```
mvn spring-boot:run
```

#### API
```
# Open bank account
POST /api/accounts
{
    "owner": string,
    "depositAmount": double
}

# Retrieve list of accounts (option to only retrieve indebted accounts)
GET /api/accounts?indebted=boolean

# Make a deposit
POST /api/accounts/{accountId}/deposits
{
    "amount": double
}

# Make a withdrawal
POST /api/accounts/{accountId}/withdrawals
{
    "amount": double
}

# Retrieve account details for an account
GET /api/accounts/{accountId}

# Retrieve transaction history for an account from a specified date
GET /api/accounts/{accountId}/transactions?date=string[YYYY-MM-DD]
```

---

### Option 2 - Full application

#### Prerequisites
- Java 17
- Maven
- Node
- NPM

#### Installation
```
# This will install node v16.17.1 and NPM 8.15.0
mvn clean package -Papplication
```

#### Running the application
```
mvn spring-boot:run
```

Full react frontend will be available: http://localhost:8080
