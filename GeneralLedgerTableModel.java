package accounting;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Connects a filtered List<Transaction> to the JTable on the "General Ledger" tab.
 * Also calculates a running balance.
 */
public class GeneralLedgerTableModel extends AbstractTableModel {
    private final Account account; // The account we are viewing
    private final List<Transaction> transactions;
    private final String[] columnNames = {"Date", "Description", "Debit", "Credit", "Balance"};
    private final List<BigDecimal> runningBalances;

    public GeneralLedgerTableModel(Account account, List<Transaction> transactions) {
        this.account = account;
        this.transactions = transactions;
        this.runningBalances = new ArrayList<>();
        calculateRunningBalances();
    }

    /**
     * Calculates the running balance for each transaction.
     */
    private void calculateRunningBalances() {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction tx : transactions) {
            boolean isDebit = tx.getDebitAccount().equals(account);
            // Apply same logic as Account.updateBalance
            if (account.getType() == AccountType.ASSET || account.getType() == AccountType.EXPENSE) {
                balance = isDebit ? balance.add(tx.getAmount()) : balance.subtract(tx.getAmount());
            } else {
                balance = isDebit ? balance.subtract(tx.getAmount()) : balance.add(tx.getAmount());
            }
            runningBalances.add(balance);
        }
    }

    @Override
    public int getRowCount() {
        return transactions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return LocalDate.class;
        if (columnIndex > 1) return BigDecimal.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction tx = transactions.get(rowIndex);
        boolean isDebit = tx.getDebitAccount().equals(account);

        switch (columnIndex) {
            case 0:
                return tx.getDate();
            case 1:
                return tx.getDescription();
            case 2: // Debit
                return isDebit ? tx.getAmount() : null;
            case 3: // Credit
                return !isDebit ? tx.getAmount() : null;
            case 4: // Balance
                return runningBalances.get(rowIndex);
            default:
                return null;
        }
    }
}

