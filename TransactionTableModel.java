package accounting;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Connects the List<Transaction> to the JTable on the "Transactions" tab.
 */
public class TransactionTableModel extends AbstractTableModel {
    private final List<Transaction> transactions;
    private final String[] columnNames = {"Date", "Description", "Debit Account", "Credit Account", "Amount"};

    public TransactionTableModel(List<Transaction> transactions) {
        this.transactions = transactions;
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
        if (columnIndex == 4) return BigDecimal.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction tx = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return tx.getDate();
            case 1:
                return tx.getDescription();
            case 2:
                return tx.getDebitAccount().getName();
            case 3:
                return tx.getCreditAccount().getName();
            case 4:
                return tx.getAmount();
            default:
                return null;
        }
    }
}

