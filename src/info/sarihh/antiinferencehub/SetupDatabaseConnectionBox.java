package info.sarihh.antiinferencehub;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

public class SetupDatabaseConnectionBox extends javax.swing.JDialog {

    public SetupDatabaseConnectionBox(java.awt.Frame parent) {
        super(parent);
        initComponents();
    }

    @Action
    public final void cancel() {
        setVisible(false);
    }

    @Action
    public final void ok() {
        if (conn != null) {
            View.setConnection(conn);
            View.setOutputText("Connection made with database: " +
                    urlTextField.getText() + " using driver: " +
                    driversComboBox.getSelectedItem() + ".");
            try {
                createInferenceChannelTable();
                createTheKeyTable();
                createSuperClientTable();
            } catch (SQLException e) {
                for (Throwable t : e) {
                    t.printStackTrace();
                }
            }
            InitializeKeysBox.selectKeyScheme();
            View.enableComponents();
        }
        setVisible(false);
    }

    @Action
    public final void test() {
        conn = DatabaseConnection.getDatabaseConnection(
                driversComboBox.getSelectedItem().toString(), urlTextField.getText(),
                userNameTextField.getText(), new String(passwordTextField.getPassword()));
        if (conn != null) {
            JOptionPane.showMessageDialog(this, "Connection succeeded!", "Info", JOptionPane.INFORMATION_MESSAGE);
            driver = driversComboBox.getSelectedItem().toString();
        } else {
            JOptionPane.showMessageDialog(this, "Connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final void createInferenceChannelTable() throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet resultSet = null;
        if (driver.equals("Oracle Thin Driver")) {
            resultSet = meta.getTables(null, "INFERENCE", null, new String[]{"TABLE"});
        } else {
            resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
        }
        boolean exist = false;
        while (resultSet.next()) {
            if (resultSet.getString(3).equalsIgnoreCase("INFERENCE_CHANNEL")) {
                exist = true;
                break;
            }
        }
        resultSet.close();
        if (!exist) {
            String query = "CREATE TABLE INFERENCE_CHANNEL (" +
                    "INFERENCE_CHANNEL_ID INT," +
                    "INFERENCE_CHANNEL_NAME VARCHAR(1000)," +
                    "INFERENCE_CHANNEL_OBJECTS VARCHAR(4000)," +
                    "INFERENCE_CHANNEL_LENGTH INT," +
                    "PRIMARY KEY (INFERENCE_CHANNEL_ID))";
            Statement statement = conn.createStatement();
            statement.execute(query);
            statement.close();
            View.setOutputText("Table INFERENCE_CHANNEL created.");
        }
    }

    private final void createSuperClientTable() throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet resultSet = null;
        if (driver.equals("Oracle Thin Driver")) {
            resultSet = meta.getTables(null, "INFERENCE", null, new String[]{"TABLE"});
        } else {
            resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
        }
        boolean exist = false;
        while (resultSet.next()) {
            if (resultSet.getString(3).equalsIgnoreCase("SUPER_CLIENT")) {
                exist = true;
                break;
            }
        }
        resultSet.close();
        if (!exist) {
            String query = "CREATE TABLE SUPER_CLIENT (" +
                    "SUPER_CLIENT_ID INT," +
                    "SUPER_CLIENT_ADDRESS VARCHAR(15)," +
                    "PRIMARY KEY (SUPER_CLIENT_ID))";
            Statement statement = conn.createStatement();
            statement.execute(query);
            statement.close();
            View.setOutputText("Table SUPER_CLIENT created.");
        }
    }

    private final void createTheKeyTable() throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet resultSet = null;
        if (driver.equals("Oracle Thin Driver")) {
            resultSet = meta.getTables(null, "INFERENCE", null, new String[]{"TABLE"});
        } else {
            resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
        }
        boolean exist = false;
        while (resultSet.next()) {
            if (resultSet.getString(3).equalsIgnoreCase("THE_KEY")) {
                exist = true;
                break;
            }
        }
        resultSet.close();
        if (!exist) {
            String query = "CREATE TABLE THE_KEY (" +
                    "OBJECT_ID INT," +
                    "OBJECT_NAME VARCHAR(1000)," +
                    "OBJECT_KEYS VARCHAR(4000)," +
                    "INFERENCE_CHANNEL_ID INT," +
                    "PRIMARY KEY (OBJECT_ID))";
            Statement statement = conn.createStatement();
            statement.execute(query);
            statement.close();
            View.setOutputText("Table THE_KEY created.");
        }
    }

    protected static final String getDriver() {
        return driver;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        driversLabel = new javax.swing.JLabel();
        driversComboBox = new javax.swing.JComboBox();
        urlTextField = new javax.swing.JTextField();
        urlLabel = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        passwordTextField = new javax.swing.JPasswordField();
        passwordLabel = new javax.swing.JLabel();
        southPanel = new javax.swing.JPanel();
        testButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getResourceMap(SetupDatabaseConnectionBox.class);
        setTitle(resourceMap.getString("setupDatabaseConnectionBox.title")); // NOI18N
        setModal(true);
        setName("setupDatabaseConnectionBox"); // NOI18N
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setName("centerPanel"); // NOI18N
        centerPanel.setLayout(new java.awt.GridBagLayout());

        driversLabel.setLabelFor(driversComboBox);
        driversLabel.setText(resourceMap.getString("driversLabel.text")); // NOI18N
        driversLabel.setName("driversLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(driversLabel, gridBagConstraints);

        driversComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MySQL Driver", "Oracle Thin Driver", "PostgreSQL Driver" }));
        driversComboBox.setName("driversComboBox"); // NOI18N
        driversComboBox.setPreferredSize(new java.awt.Dimension(340, 22));
        driversComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                driversComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(driversComboBox, gridBagConstraints);

        urlTextField.setText(resourceMap.getString("urlTextField.text")); // NOI18N
        urlTextField.setName("urlTextField"); // NOI18N
        urlTextField.setPreferredSize(new java.awt.Dimension(340, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(urlTextField, gridBagConstraints);

        urlLabel.setBackground(new java.awt.Color(255, 255, 255));
        urlLabel.setLabelFor(urlTextField);
        urlLabel.setText(resourceMap.getString("urlLabel.text")); // NOI18N
        urlLabel.setName("urlLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(urlLabel, gridBagConstraints);

        userNameLabel.setBackground(new java.awt.Color(255, 255, 255));
        userNameLabel.setLabelFor(userNameTextField);
        userNameLabel.setText(resourceMap.getString("userNameLabel.text")); // NOI18N
        userNameLabel.setName("userNameLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(userNameLabel, gridBagConstraints);

        userNameTextField.setText(resourceMap.getString("userNameTextField.text")); // NOI18N
        userNameTextField.setName("userNameTextField"); // NOI18N
        userNameTextField.setPreferredSize(new java.awt.Dimension(340, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(userNameTextField, gridBagConstraints);

        passwordTextField.setText(resourceMap.getString("passwordTextField.text")); // NOI18N
        passwordTextField.setName("passwordTextField"); // NOI18N
        passwordTextField.setPreferredSize(new java.awt.Dimension(340, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(passwordTextField, gridBagConstraints);

        passwordLabel.setBackground(new java.awt.Color(255, 255, 255));
        passwordLabel.setLabelFor(passwordTextField);
        passwordLabel.setText(resourceMap.getString("passwordLabel.text")); // NOI18N
        passwordLabel.setName("passwordLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(passwordLabel, gridBagConstraints);

        mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setName("southPanel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getActionMap(SetupDatabaseConnectionBox.class, this);
        testButton.setAction(actionMap.get("test")); // NOI18N
        testButton.setBackground(new java.awt.Color(255, 255, 255));
        testButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        testButton.setName("testButton"); // NOI18N
        testButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(testButton);

        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setBackground(new java.awt.Color(255, 255, 255));
        okButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        okButton.setName("okButton"); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(okButton);

        cancelButton.setAction(actionMap.get("cancel")); // NOI18N
        cancelButton.setBackground(new java.awt.Color(255, 255, 255));
        cancelButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(cancelButton);

        mainPanel.add(southPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void driversComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_driversComboBoxItemStateChanged
        // TODO add your handling code here:
        switch (driversComboBox.getSelectedIndex()) {
            case 0:
                urlTextField.setText("jdbc:mysql://<hostname>[,<failoverhost>][<:3306>]/<dbname>");
                break;
            case 1:
                urlTextField.setText("jdbc:oracle:thin:@<server>[:<1521>]:<database_name>");
                break;
            case 2:
                urlTextField.setText("jdbc:postgresql:[<//host>[:<5432>/]]<database>");
                break;
            default:
                break;
        }
    }//GEN-LAST:event_driversComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox driversComboBox;
    private javax.swing.JLabel driversLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTextField;
    private javax.swing.JPanel southPanel;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JTextField urlTextField;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTextField;
    // End of variables declaration//GEN-END:variables
    private static Connection conn;
    private static String driver;
}
