package accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single, complete transaction (a balanced debit and credit).
 */
public class Transaction {
    private LocalDate date;
    private String description;
    private Account debitAccount;
    private Account creditAccount;
    private BigDecimal amount;

    public Transaction(LocalDate date, String description, Account debitAccount, Account creditAccount, BigDecimal amount) {
        this.date = date;
        this.description = description;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
    }

    // --- Getters ---
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Account getDebitAccount() {
        return debitAccount;
    }

    public Account getCreditAccount() {
        return creditAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

