package accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Helper class to represent a single line in the General Journal.
 * A single Transaction object is split into two JournalEntry objects (one debit, one credit).
 */
public class JournalEntry {
    private LocalDate date;
    private String description;
    private String accountName;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public JournalEntry(LocalDate date, String description, String accountName, BigDecimal debitAmount, BigDecimal creditAmount) {
        this.date = date;
        this.description = description;
        this.accountName = accountName;
        this.debitAmount = debitAmount; // Will be null for a credit entry
        this.creditAmount = creditAmount; // Will be null for a debit entry
    }

    // --- Getters ---
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }
}

