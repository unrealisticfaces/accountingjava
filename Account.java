package accounting;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single account in the Chart of Accounts.
 * Manages its own balance based on accounting rules.
 */
public class Account {
    private String name;
    private AccountType type;
    private BigDecimal balance;

    public Account(String name, AccountType type) {
        this.name = name;
        this.type = type;
        this.balance = BigDecimal.ZERO;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * The core accounting logic.
     * Updates the balance based on whether the account is debited or credited.
     * @param amount The amount of the transaction.
     * @param isDebit True if this account is being debited, false if credited.
     */
    public void updateBalance(BigDecimal amount, boolean isDebit) {
        // Debits increase Assets and Expenses
        if (type == AccountType.ASSET || type == AccountType.EXPENSE) {
            if (isDebit) {
                this.balance = this.balance.add(amount);
            } else {
                this.balance = this.balance.subtract(amount);
            }
        }
        // Credits increase Liabilities, Equity, and Income
        else {
            if (isDebit) {
                this.balance = this.balance.subtract(amount);
            } else {
                this.balance = this.balance.add(amount);
            }
        }
    }

    /**
     * This is used to display the account name in JComboBoxes (dropdowns).
     */
    @Override
    public String toString() {
        return String.format("%s [%s]", name, type.toString());
    }

    // Required for correctly identifying accounts in JComboBoxes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return name.equals(account.name) && type == account.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}

