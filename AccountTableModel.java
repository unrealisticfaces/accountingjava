package accounting;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.List;

/**
 * Connects the List<Account> to the JTable on the "Accounts" tab.
 */
public class AccountTableModel extends AbstractTableModel {
    private final List<Account> accounts;
    private final String[] columnNames = {"Account", "Type", "Balance"};

    public AccountTableModel(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public int getRowCount() {
        return accounts.size();
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
        if (columnIndex == 2) {
            return BigDecimal.class; // For correct number alignment
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Account account = accounts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return account.getName();
            case 1:
                return account.getType().toString();
            case 2:
                return account.getBalance();
            default:
                return null;
        }
    }
}

