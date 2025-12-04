package accounting;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Main application class.
 * Creates the JFrame, JTabbedPane, and all UI components.
 * This is the entry point of the application.
 */
public class MainApp {

    // --- Core Data ---
    private final AccountingEngine engine;

    // --- UI Components ---
    private JFrame frame;
    private JTabbedPane tabbedPane;

    // --- Tab 1: New Transaction ---
    private JSpinner dateSpinner;
    private JTextField txtDescription;
    private JComboBox<Account> cmbDebitAccount;
    private JComboBox<Account> cmbCreditAccount;
    private JSpinner amountSpinner;
    private JButton btnAddTransaction;

    // --- Tab 2: Transactions ---
    private JTable tblTransactions;
    private TransactionTableModel transactionTableModel;

    // --- Tab 3: Accounts ---
    private JTable tblAccounts;
    private AccountTableModel accountTableModel;

    // --- Tab 4: General Journal ---
    private JTable tblGeneralJournal;
    private GeneralJournalTableModel generalJournalTableModel;

    // --- Tab 5: General Ledger ---
    private JComboBox<Account> cmbLedgerAccount;
    private JTable tblGeneralLedger;
    private JPanel pnlGeneralLedger; // Panel to hold the table

    // --- Tab 6: Balance Sheet ---
    private JTextArea txtAssets;
    private JTextArea txtLiabilitiesEquity;
    private JLabel lblTotalAssets;
    private JLabel lblTotalLiabilitiesEquity;

    // Define Philippine Locale
    private static final Locale phLocale = new Locale("en", "PH");


    public MainApp() {
        // Initialize the accounting engine
        engine = new AccountingEngine();
    }

    /**
     * Creates and displays the main application window.
     */
    public void createAndShowGUI() {
        // --- Setup Frame ---
        frame = new JFrame("Accounting System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600); 
        frame.setLocationRelativeTo(null); // Center screen

        // --- Setup Tabbed Pane ---
        tabbedPane = new JTabbedPane();

        // --- Create Tabs ---
        tabbedPane.addTab("New Transaction", createNewTransactionTab());
        tabbedPane.addTab("Transactions", createTransactionsTab());
        tabbedPane.addTab("Accounts", createAccountsTab());
        tabbedPane.addTab("General Journal", createGeneralJournalTab());
        tabbedPane.addTab("General Ledger", createGeneralLedgerTab());
        tabbedPane.addTab("Balance Sheet", createBalanceSheetTab());

        // --- Add to Frame ---
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);

        // --- Initial Data Load ---
        loadComboBoxes();
        refreshAllTables();
        refreshBalanceSheet();
    }

    // =========================================================================
    // TAB CREATION METHODS
    // =========================================================================

    private JPanel createNewTransactionTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Row 0: Date ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Use JSpinner for date selection
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        panel.add(dateSpinner, gbc);

        // --- Row 1: Description ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDescription = new JTextField(30);
        panel.add(txtDescription, gbc);

        // --- Row 2: Debit Account ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Debit Account:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbDebitAccount = new JComboBox<>();
        panel.add(cmbDebitAccount, gbc);

        // --- Row 3: Credit Account ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Credit Account:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbCreditAccount = new JComboBox<>();
        panel.add(cmbCreditAccount, gbc);

        // --- Row 4: Amount ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // FIX: Configure JSpinner to handle commas (e.g., 1,000.00)
        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000000.0, 0.01)); // Increased max value
        JSpinner.NumberEditor amountEditor = new JSpinner.NumberEditor(amountSpinner, "#,##0.00");
        amountSpinner.setEditor(amountEditor);
        panel.add(amountSpinner, gbc);

        // --- Row 5: Add Button ---
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        btnAddTransaction = new JButton("Add Transaction");
        panel.add(btnAddTransaction, gbc);

        // --- Add Action Listener ---
        btnAddTransaction.addActionListener(e -> addTransaction());

        return panel;
    }

    private JScrollPane createTransactionsTab() {
        transactionTableModel = new TransactionTableModel(engine.getTransactions());
        tblTransactions = new JTable(transactionTableModel);
        setupCurrencyRenderer(tblTransactions, 4); 
        return new JScrollPane(tblTransactions);
    }

    private JScrollPane createAccountsTab() {
        accountTableModel = new AccountTableModel(engine.getChartOfAccounts());
        tblAccounts = new JTable(accountTableModel);
        setupCurrencyRenderer(tblAccounts, 2); 
        return new JScrollPane(tblAccounts);
    }

    private JScrollPane createGeneralJournalTab() {
        generalJournalTableModel = new GeneralJournalTableModel(engine.getGeneralJournal());
        tblGeneralJournal = new JTable(generalJournalTableModel);
        setupCurrencyRenderer(tblGeneralJournal, 3); 
        setupCurrencyRenderer(tblGeneralJournal, 4); 
        return new JScrollPane(tblGeneralJournal);
    }

    private JPanel createGeneralLedgerTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // --- Top: Account Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Account:"));
        cmbLedgerAccount = new JComboBox<>();
        topPanel.add(cmbLedgerAccount);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Table ---
        tblGeneralLedger = new JTable();
        pnlGeneralLedger = new JPanel(new BorderLayout());
        pnlGeneralLedger.add(new JScrollPane(tblGeneralLedger), BorderLayout.CENTER);
        panel.add(pnlGeneralLedger, BorderLayout.CENTER);

        // --- Action Listener ---
        cmbLedgerAccount.addActionListener(e -> refreshGeneralLedgerTable());

        return panel;
    }

    private JPanel createBalanceSheetTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10)); 
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Left Panel: Assets ---
        JPanel pnlAssets = new JPanel(new BorderLayout());
        pnlAssets.add(new JLabel("Assets"), BorderLayout.NORTH);
        txtAssets = new JTextArea();
        txtAssets.setEditable(false);
        txtAssets.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlAssets.add(new JScrollPane(txtAssets), BorderLayout.CENTER);
        lblTotalAssets = new JLabel("Total Assets: ₱0.00");
        pnlAssets.add(lblTotalAssets, BorderLayout.SOUTH);
        panel.add(pnlAssets);

        // --- Right Panel: Liabilities & Equity ---
        JPanel pnlLiabilities = new JPanel(new BorderLayout());
        pnlLiabilities.add(new JLabel("Liabilities & Equity"), BorderLayout.NORTH);
        txtLiabilitiesEquity = new JTextArea();
        txtLiabilitiesEquity.setEditable(false);
        txtLiabilitiesEquity.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlLiabilities.add(new JScrollPane(txtLiabilitiesEquity), BorderLayout.CENTER);
        lblTotalLiabilitiesEquity = new JLabel("Total L & E: ₱0.00");
        pnlLiabilities.add(lblTotalLiabilitiesEquity, BorderLayout.SOUTH);
        panel.add(pnlLiabilities);

        return panel;
    }

    // =========================================================================
    // UI LOGIC METHODS
    // =========================================================================

    private void addTransaction() {
        try {
            Date date = (Date) dateSpinner.getValue();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String description = txtDescription.getText();
            Account debitAccount = (Account) cmbDebitAccount.getSelectedItem();
            Account creditAccount = (Account) cmbCreditAccount.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountSpinner.getValue().toString());

            if (description.trim().isEmpty() || debitAccount == null || creditAccount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (debitAccount.equals(creditAccount)) {
                JOptionPane.showMessageDialog(frame, "Debit and Credit accounts cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            engine.addTransaction(localDate, description, debitAccount, creditAccount, amount);
            refreshAllTables();
            refreshBalanceSheet();
            clearInputFields();
            JOptionPane.showMessageDialog(frame, "Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadComboBoxes() {
        java.util.List<Account> accounts = engine.getChartOfAccounts();
        cmbDebitAccount.removeAllItems();
        cmbCreditAccount.removeAllItems();
        cmbLedgerAccount.removeAllItems();

        for (Account acc : accounts) {
            cmbDebitAccount.addItem(acc);
            cmbCreditAccount.addItem(acc);
            cmbLedgerAccount.addItem(acc);
        }
    }

    private void clearInputFields() {
        txtDescription.setText("");
        amountSpinner.setValue(0.0);
        cmbDebitAccount.setSelectedIndex(0);
        cmbCreditAccount.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
    }

    private void refreshAllTables() {
        // --- Tab 2: Transactions ---
        transactionTableModel = new TransactionTableModel(engine.getTransactions());
        tblTransactions.setModel(transactionTableModel);
        setupCurrencyRenderer(tblTransactions, 4);

        // --- Tab 3: Accounts ---
        accountTableModel = new AccountTableModel(engine.getChartOfAccounts());
        tblAccounts.setModel(accountTableModel);
        setupCurrencyRenderer(tblAccounts, 2);

        // --- Tab 4: General Journal ---
        generalJournalTableModel = new GeneralJournalTableModel(engine.getGeneralJournal());
        tblGeneralJournal.setModel(generalJournalTableModel);
        setupCurrencyRenderer(tblGeneralJournal, 3);
        setupCurrencyRenderer(tblGeneralJournal, 4);
        
        // --- Tab 5: General Ledger ---
        refreshGeneralLedgerTable();
    }

    private void refreshGeneralLedgerTable() {
        Account selectedAccount = (Account) cmbLedgerAccount.getSelectedItem();
        if (selectedAccount == null) {
            tblGeneralLedger.setModel(new GeneralLedgerTableModel(null, Collections.emptyList()));
            return;
        }

        java.util.List<Transaction> accountTransactions = engine.getTransactionsForAccount(selectedAccount);
        GeneralLedgerTableModel ledgerModel = new GeneralLedgerTableModel(selectedAccount, accountTransactions);
        tblGeneralLedger.setModel(ledgerModel);

        // Apply formatting
        setupCurrencyRenderer(tblGeneralLedger, 2); 
        setupCurrencyRenderer(tblGeneralLedger, 3);
        setupCurrencyRenderer(tblGeneralLedger, 4);
    }

    private void refreshBalanceSheet() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(phLocale);

        // FIX: Force Parentheses for negative numbers in the text area
        if (currencyFormat instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) currencyFormat;
            df.setNegativePrefix("(");
            df.setNegativeSuffix(")");
        }

        StringBuilder sbAssets = new StringBuilder();
        StringBuilder sbLiabilities = new StringBuilder();

        for (Account acc : engine.getChartOfAccounts()) {
            String line = String.format("%-25s %15s\n", acc.getName(), currencyFormat.format(acc.getBalance()));
            if (acc.getType() == AccountType.ASSET) {
                sbAssets.append(line);
            } else if (acc.getType() == AccountType.LIABILITY) {
                sbLiabilities.append(line);
            }
        }
        
        BigDecimal totalEquity = engine.getTotalEquity();
        sbLiabilities.append("\n--- Equity ---\n");
        for (Account acc : engine.getChartOfAccounts()) {
             if (acc.getType() == AccountType.EQUITY) {
                sbLiabilities.append(String.format("%-25s %15s\n", acc.getName(), currencyFormat.format(acc.getBalance())));
            }
        }
        
        BigDecimal netIncome = engine.getTotalEquity().subtract(
             engine.getChartOfAccounts().stream()
                .filter(a -> a.getType() == AccountType.EQUITY)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        // FIX: Change label to "Net Loss" if negative
        if (netIncome.compareTo(BigDecimal.ZERO) < 0) {
             sbLiabilities.append(String.format("%-25s %15s\n", "Net Loss", currencyFormat.format(netIncome)));
        } else {
             sbLiabilities.append(String.format("%-25s %15s\n", "Net Income", currencyFormat.format(netIncome)));
        }
        
        txtAssets.setText(sbAssets.toString());
        txtLiabilitiesEquity.setText(sbLiabilities.toString());

        BigDecimal totalAssets = engine.getTotalAssets();
        BigDecimal totalLiabilities = engine.getTotalLiabilities();
        BigDecimal totalLiabilitiesAndEquity = totalLiabilities.add(totalEquity);

        lblTotalAssets.setText("Total Assets: " + currencyFormat.format(totalAssets));
        lblTotalLiabilitiesEquity.setText("Total L & E: " + currencyFormat.format(totalLiabilitiesAndEquity));
    }

    private void setupCurrencyRenderer(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new CurrencyRenderer());
    }

    // =========================================================================
    // INTERNAL CLASS: CurrencyRenderer
    // =========================================================================
    static class CurrencyRenderer extends DefaultTableCellRenderer {
        private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(phLocale);

        // FIX: Static initialization to ensure parentheses formatting on the JTables
        static {
            if (FORMAT instanceof DecimalFormat) {
                DecimalFormat df = (DecimalFormat) FORMAT;
                df.setNegativePrefix("(");
                df.setNegativeSuffix(")");
            }
        }

        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT); 
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof BigDecimal) {
                value = FORMAT.format(value);
            } else if (value == null) {
                value = ""; 
            }
            super.setValue(value);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.createAndShowGUI();
        });
    }
}