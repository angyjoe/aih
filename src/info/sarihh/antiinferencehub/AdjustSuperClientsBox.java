package info.sarihh.antiinferencehub;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;

public class AdjustSuperClientsBox extends javax.swing.JDialog {

    public AdjustSuperClientsBox(java.awt.Frame parent) {
        super(parent);
        initComponents();
        addDialog.pack();
    }

    @Action
    public final void add() {
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    @Action
    public final void ok() {
        if (superClientAddressFormattedTextField.getText().trim().length() < 1) {
            superClientAddressFormattedTextField.setFocusable(true);
            superClientAddressFormattedTextField.requestFocusInWindow();
            return;
        }
        addSuperClient();
        addDialog.setVisible(false);
        fillSuperClientsTable();
    }

    @Action
    public final void remove() {
        if (superClientsTable.getSelectedRow() == -1) {
            return;
        }
        int superClientID = Integer.parseInt(
                ((DefaultTableModel) superClientsTable.getModel()).getValueAt(superClientsTable.getSelectedRow(), 0).toString());
        deleteSuperClient(superClientID);
        fillSuperClientsTable();
    }

    @Action
    public final void removeAll() {
        int rowCount = ((DefaultTableModel) superClientsTable.getModel()).getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int superClientID = Integer.parseInt(
                    ((DefaultTableModel) superClientsTable.getModel()).getValueAt(i, 0).toString());
            deleteSuperClient(superClientID);
        }
        fillSuperClientsTable();
    }

    private final void addSuperClient() {
        try {
            String query = "INSERT INTO SUPER_CLIENT values (?, ?)";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, maxSuperClientID() + 1);
            statement.setString(2, superClientAddressFormattedTextField.getText());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    private final void deleteSuperClient(int superClientID) {
        try {
            String query = "DELETE FROM SUPER_CLIENT WHERE SUPER_CLIENT_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, superClientID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    protected final void fillSuperClientsTable() {
        try {
            String query = "SELECT * FROM SUPER_CLIENT ORDER BY SUPER_CLIENT_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.last();
            int channelsCount = resultSet.getRow();
            resultSet.beforeFirst();
            Object[][] channels = new Object[channelsCount][2];
            int i = 0;
            while (resultSet.next()) {
                channels[i][0] = resultSet.getInt("SUPER_CLIENT_ID");
                channels[i][1] = resultSet.getString("SUPER_CLIENT_ADDRESS");
                i++;
            }
            superClientsTable.setModel(new DefaultTableModel(channels, channelsTableColumns) {

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            });
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    private final Object[] getDatabaseObjects() {
        try {
            DatabaseMetaData meta = View.getConnection().getMetaData();
            ResultSet resultSet = null;
            if (SetupDatabaseConnectionBox.getDriver().equals("Oracle Thin Driver")) {
                resultSet = meta.getTables(null, "INFERENCE", null, new String[]{"TABLE"});
            } else {
                resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
            }
            Vector<String> databaseObjectsVector = new Vector<String>();
            while (resultSet.next()) {
                if (!resultSet.getString(3).equalsIgnoreCase("INFERENCE_CHANNEL") &&
                        !resultSet.getString(3).equalsIgnoreCase("THE_KEY") &&
                        !resultSet.getString(3).equalsIgnoreCase("SUPER_CLIENT")) {
                    databaseObjectsVector.add(resultSet.getString(3).toUpperCase());
                }
            }
            resultSet.close();
            return databaseObjectsVector.toArray();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private final int maxSuperClientID() {
        try {
            String query = "SELECT MAX(SUPER_CLIENT_ID) FROM SUPER_CLIENT";
            Statement statement = View.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int maxSuperClientID = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            return maxSuperClientID;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return 0;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        addDialog = new javax.swing.JDialog();
        mainPanel1 = new javax.swing.JPanel();
        centerPanel1 = new javax.swing.JPanel();
        superClientAddressLabel = new javax.swing.JLabel();
        superClientAddressFormattedTextField = new javax.swing.JFormattedTextField(new IPAddressFormatter());
        southPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        superClientsScrollPane = new javax.swing.JScrollPane();
        superClientsTable = new javax.swing.JTable();
        southPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        addDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getResourceMap(AdjustSuperClientsBox.class);
        addDialog.setTitle(resourceMap.getString("addDialog.title")); // NOI18N
        addDialog.setModal(true);
        addDialog.setName("addDialog"); // NOI18N
        addDialog.setResizable(false);

        mainPanel1.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel1.setName("mainPanel1"); // NOI18N
        mainPanel1.setLayout(new java.awt.BorderLayout());

        centerPanel1.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel1.setName("centerPanel1"); // NOI18N
        centerPanel1.setLayout(new java.awt.GridBagLayout());

        superClientAddressLabel.setBackground(new java.awt.Color(255, 255, 255));
        superClientAddressLabel.setText(resourceMap.getString("superClientAddressLabel.text")); // NOI18N
        superClientAddressLabel.setName("superClientAddressLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(superClientAddressLabel, gridBagConstraints);

        superClientAddressFormattedTextField.setText(resourceMap.getString("superClientAddressFormattedTextField.text")); // NOI18N
        superClientAddressFormattedTextField.setName("superClientAddressFormattedTextField"); // NOI18N
        superClientAddressFormattedTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(superClientAddressFormattedTextField, gridBagConstraints);

        mainPanel1.add(centerPanel1, java.awt.BorderLayout.CENTER);

        southPanel1.setBackground(new java.awt.Color(255, 255, 255));
        southPanel1.setName("southPanel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getActionMap(AdjustSuperClientsBox.class, this);
        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setBackground(new java.awt.Color(255, 255, 255));
        okButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        okButton.setName("okButton"); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel1.add(okButton);

        mainPanel1.add(southPanel1, java.awt.BorderLayout.SOUTH);

        addDialog.getContentPane().add(mainPanel1, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("adjustSuperClientsBox.title")); // NOI18N
        setModal(true);
        setName("adjustSuperClientsBox"); // NOI18N

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setName("centerPanel"); // NOI18N
        centerPanel.setLayout(new java.awt.BorderLayout());

        superClientsScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        superClientsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        superClientsScrollPane.setName("superClientsScrollPane"); // NOI18N

        superClientsTable.setName("superClientsTable"); // NOI18N
        superClientsTable.setSelectionBackground(new java.awt.Color(212, 208, 200));
        superClientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        superClientsScrollPane.setViewportView(superClientsTable);

        centerPanel.add(superClientsScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setName("southPanel"); // NOI18N

        addButton.setAction(actionMap.get("add")); // NOI18N
        addButton.setBackground(new java.awt.Color(255, 255, 255));
        addButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        addButton.setName("addButton"); // NOI18N
        addButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(addButton);

        removeButton.setAction(actionMap.get("remove")); // NOI18N
        removeButton.setBackground(new java.awt.Color(255, 255, 255));
        removeButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        removeButton.setName("removeButton"); // NOI18N
        removeButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(removeButton);

        removeAllButton.setAction(actionMap.get("removeAll")); // NOI18N
        removeAllButton.setBackground(new java.awt.Color(255, 255, 255));
        removeAllButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        removeAllButton.setName("removeAllButton"); // NOI18N
        removeAllButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(removeAllButton);

        mainPanel.add(southPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JDialog addDialog;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel centerPanel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainPanel1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel southPanel1;
    private javax.swing.JFormattedTextField superClientAddressFormattedTextField;
    private javax.swing.JLabel superClientAddressLabel;
    private javax.swing.JScrollPane superClientsScrollPane;
    private javax.swing.JTable superClientsTable;
    // End of variables declaration//GEN-END:variables
    private static final String channelsTableColumns[] = {"ID", "ADDRESS"};
}
