package accounting;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Connects the List<JournalEntry> to the JTable on the "General Journal" tab.
 */
public class GeneralJournalTableModel extends AbstractTableModel {
    private final List<JournalEntry> journal;
    private final String[] columnNames = {"Date", "Description", "Account", "Debit", "Credit"};

    public GeneralJournalTableModel(List<JournalEntry> journal) {
        this.journal = journal;
    }

    @Override
    public int getRowCount() {
        return journal.size();
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
        if (columnIndex == 3 || columnIndex == 4) return BigDecimal.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        JournalEntry entry = journal.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return entry.getDate(); // Will be null for credit line
            case 1:
                return entry.getDescription(); // Will be null for credit line
            case 2:
                return entry.getAccountName();
            case 3:
                return entry.getDebitAmount(); // Will be null for credit line
            case 4:
                return entry.getCreditAmount(); // Will be null for debit line
            default:
                return null;
        }
    }
}

