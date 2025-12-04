package accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central class to manage the Chart of Accounts and all Transactions.
 * This is the main "brain" of the application.
 */
public class AccountingEngine {

    // These lists hold all the application's data
    private final List<Account> chartOfAccounts;
    private final List<Transaction> transactions;
    private final List<JournalEntry> generalJournal;

    public AccountingEngine() {
        this.chartOfAccounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.generalJournal = new ArrayList<>();
        initializeChartOfAccounts();
    }

    /**
     * Pre-loads the accounts seen in the video.
     */
    private void initializeChartOfAccounts() {
        chartOfAccounts.add(new Account("Cash", AccountType.ASSET));
        chartOfAccounts.add(new Account("Equipment", AccountType.ASSET));
        chartOfAccounts.add(new Account("Accounts Receivable", AccountType.ASSET));
        chartOfAccounts.add(new Account("Prepaid Expenses", AccountType.ASSET));
        chartOfAccounts.add(new Account("Inventory", AccountType.ASSET));
        chartOfAccounts.add(new Account("Accounts Payable", AccountType.LIABILITY));
        chartOfAccounts.add(new Account("Notes Payable", AccountType.LIABILITY));
        chartOfAccounts.add(new Account("Owner's Capital", AccountType.EQUITY));
        chartOfAccounts.add(new Account("Sales Revenue", AccountType.INCOME));
        chartOfAccounts.add(new Account("Service Revenue", AccountType.INCOME));
        chartOfAccounts.add(new Account("Cost of Goods Sold", AccountType.EXPENSE));
        chartOfAccounts.add(new Account("Rent Expense", AccountType.EXPENSE));
        chartOfAccounts.add(new Account("Salaries Expense", AccountType.EXPENSE));
        chartOfAccounts.add(new Account("Utilities Expense", AccountType.EXPENSE));
    }

    /**
     * Creates and adds a new transaction.
     * This method updates account balances and the general journal.
     */
    public void addTransaction(LocalDate date, String description, Account debitAccount, Account creditAccount, BigDecimal amount) {
        // 1. Create and add the transaction
        Transaction newTransaction = new Transaction(date, description, debitAccount, creditAccount, amount);
        this.transactions.add(newTransaction);

        // 2. Update account balances
        debitAccount.updateBalance(amount, true);  // true = isDebit
        creditAccount.updateBalance(amount, false); // false = isCredit

        // 3. Create General Journal entries
        // As seen in the video, the date and description are only on the first line (debit)
        JournalEntry debitEntry = new JournalEntry(date, description, debitAccount.getName(), amount, null);
        JournalEntry creditEntry = new JournalEntry(null, null, creditAccount.getName(), null, amount);
        this.generalJournal.add(debitEntry);
        this.generalJournal.add(creditEntry);
    }

    // --- Data Access Methods for UI ---

    public List<Account> getChartOfAccounts() {
        return Collections.unmodifiableList(chartOfAccounts);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public List<JournalEntry> getGeneralJournal() {
        return Collections.unmodifiableList(generalJournal);
    }

    /**
     * Gets all transactions for a specific account.
     * Used by the General Ledger.
     */
    public List<Transaction> getTransactionsForAccount(Account account) {
        return transactions.stream()
                .filter(t -> t.getDebitAccount().equals(account) || t.getCreditAccount().equals(account))
                .collect(Collectors.toList());
    }

    // --- Calculation Methods for Balance Sheet ---

    public BigDecimal getTotalAssets() {
        return chartOfAccounts.stream()
                .filter(a -> a.getType() == AccountType.ASSET)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLiabilities() {
        return chartOfAccounts.stream()
                .filter(a -> a.getType() == AccountType.LIABILITY)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalEquity() {
        // A proper system would roll up Income/Expense into Retained Earnings.
        // For simplicity, we just sum Equity accounts (e.g., Owner's Capital).
        // Note: Income increases equity, Expenses decrease it.
        BigDecimal equity = chartOfAccounts.stream()
                .filter(a -> a.getType() == AccountType.EQUITY)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal income = chartOfAccounts.stream()
                .filter(a -> a.getType() == AccountType.INCOME)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expense = chartOfAccounts.stream()
                .filter(a -> a.getType() == AccountType.EXPENSE)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Equity = Equity + Income - Expense
        return equity.add(income).subtract(expense);
    }
}

