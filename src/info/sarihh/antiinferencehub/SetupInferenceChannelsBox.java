package info.sarihh.antiinferencehub;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;

public class SetupInferenceChannelsBox extends javax.swing.JDialog {

    public SetupInferenceChannelsBox(java.awt.Frame parent) {
        super(parent);
        initComponents();
        addDialog.pack();
    }

    @Action
    public final void add() {
        databaseObjectsComboBox.setModel(new javax.swing.DefaultComboBoxModel(getDatabaseObjects()));
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    @Action
    public final void clear() {
        DefaultListModel model = (DefaultListModel) inferenceChannelObjectsList.getModel();
        model.clear();
    }

    @Action
    public final void exclude() {
        DefaultListModel model = (DefaultListModel) inferenceChannelObjectsList.getModel();
        Object[] selectedValues = inferenceChannelObjectsList.getSelectedValues();
        for (Object value : selectedValues) {
            model.removeElement(value);
        }
    }

    @Action
    public final void include() {
        Object object = databaseObjectsComboBox.getSelectedItem();
        DefaultListModel model = (DefaultListModel) inferenceChannelObjectsList.getModel();
        if (!model.contains(object)) {
            model.addElement(object);
        }
    }

    @Action
    public final void ok() {
        if (inferenceChannelNameTextField.getText().trim().length() < 1) {
            messageLabel.setText("Inference channel name is missing!");
            inferenceChannelNameTextField.setFocusable(true);
            inferenceChannelNameTextField.requestFocusInWindow();
            return;
        } else if (((DefaultListModel) inferenceChannelObjectsList.getModel()).getSize() < 1) {
            messageLabel.setText("Inference channel objects are missing!");
            inferenceChannelObjectsList.setFocusable(true);
            inferenceChannelObjectsList.requestFocusInWindow();
            return;
        }
        addChannel();
        addDialog.setVisible(false);
        fillChannelsTable();
        InitializeKeysBox.selectKeyScheme();
    }

    @Action
    public final void remove() {
        if (channelsTable.getSelectedRow() == -1) {
            return;
        }
        int channelID = Integer.parseInt(
                ((DefaultTableModel) channelsTable.getModel()).getValueAt(channelsTable.getSelectedRow(), 0).toString());
        deleteChannel(channelID);
        fillChannelsTable();
        InitializeKeysBox.selectKeyScheme();
    }

    @Action
    public final void removeAll() {
        int rowCount = ((DefaultTableModel) channelsTable.getModel()).getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int channelID = Integer.parseInt(
                    ((DefaultTableModel) channelsTable.getModel()).getValueAt(i, 0).toString());
            deleteChannel(channelID);
        }
        fillChannelsTable();
        InitializeKeysBox.selectKeyScheme();
    }

    private final void addChannel() {
        try {
            String query = "INSERT INTO INFERENCE_CHANNEL values (?, ?, ?, ?)";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, maxChannelID() + 1);
            statement.setString(2, inferenceChannelNameTextField.getText());
            DefaultListModel model = (DefaultListModel) inferenceChannelObjectsList.getModel();
            Object[] objects = model.toArray();
            statement.setString(3, Arrays.toString(objects));
            statement.setInt(4, objects.length);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    private final void deleteChannel(int channelID) {
        try {
            String query = "DELETE FROM INFERENCE_CHANNEL WHERE INFERENCE_CHANNEL_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, channelID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    protected final void fillChannelsTable() {
        try {
            String query = "SELECT * FROM INFERENCE_CHANNEL ORDER BY INFERENCE_CHANNEL_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.last();
            int channelsCount = resultSet.getRow();
            resultSet.beforeFirst();
            Object[][] channels = new Object[channelsCount][4];
            int i = 0;
            while (resultSet.next()) {
                channels[i][0] = resultSet.getInt("INFERENCE_CHANNEL_ID");
                channels[i][1] = resultSet.getString("INFERENCE_CHANNEL_NAME");
                channels[i][2] = resultSet.getString("INFERENCE_CHANNEL_OBJECTS");
                channels[i][3] = resultSet.getString("INFERENCE_CHANNEL_LENGTH");
                i++;
            }
            channelsTable.setModel(new DefaultTableModel(channels, channelsTableColumns) {

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

    private final int maxChannelID() {
        try {
            String query = "SELECT MAX(INFERENCE_CHANNEL_ID) FROM INFERENCE_CHANNEL";
            Statement statement = View.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int maxChannelID = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            return maxChannelID;
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
        inferenceChannelNameLabel = new javax.swing.JLabel();
        inferenceChannelNameTextField = new javax.swing.JTextField();
        databaseObjectsLabel = new javax.swing.JLabel();
        databaseObjectsComboBox = new javax.swing.JComboBox();
        inferenceChannelObjectsLabel = new javax.swing.JLabel();
        inferenceChannelObjectsScrollPane = new javax.swing.JScrollPane();
        inferenceChannelObjectsList = new JList(new DefaultListModel());
        messageLabel = new javax.swing.JLabel();
        southPanel1 = new javax.swing.JPanel();
        includeButton = new javax.swing.JButton();
        excludeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        channelsScrollPane = new javax.swing.JScrollPane();
        channelsTable = new javax.swing.JTable();
        southPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        addDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getResourceMap(SetupInferenceChannelsBox.class);
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

        inferenceChannelNameLabel.setBackground(new java.awt.Color(255, 255, 255));
        inferenceChannelNameLabel.setText(resourceMap.getString("inferenceChannelNameLabel.text")); // NOI18N
        inferenceChannelNameLabel.setName("inferenceChannelNameLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(inferenceChannelNameLabel, gridBagConstraints);

        inferenceChannelNameTextField.setName("inferenceChannelNameTextField"); // NOI18N
        inferenceChannelNameTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(inferenceChannelNameTextField, gridBagConstraints);

        databaseObjectsLabel.setBackground(new java.awt.Color(255, 255, 255));
        databaseObjectsLabel.setText(resourceMap.getString("databaseObjectsLabel.text")); // NOI18N
        databaseObjectsLabel.setName("databaseObjectsLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(databaseObjectsLabel, gridBagConstraints);

        databaseObjectsComboBox.setName("databaseObjectsComboBox"); // NOI18N
        databaseObjectsComboBox.setPreferredSize(new java.awt.Dimension(250, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(databaseObjectsComboBox, gridBagConstraints);

        inferenceChannelObjectsLabel.setBackground(new java.awt.Color(255, 255, 255));
        inferenceChannelObjectsLabel.setText(resourceMap.getString("inferenceChannelObjectsLabel.text")); // NOI18N
        inferenceChannelObjectsLabel.setName("inferenceChannelObjectsLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(inferenceChannelObjectsLabel, gridBagConstraints);

        inferenceChannelObjectsScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        inferenceChannelObjectsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        inferenceChannelObjectsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inferenceChannelObjectsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inferenceChannelObjectsScrollPane.setName("inferenceChannelObjectsScrollPane"); // NOI18N

        inferenceChannelObjectsList.setFixedCellWidth(230);
        inferenceChannelObjectsList.setName("inferenceChannelObjectsList"); // NOI18N
        inferenceChannelObjectsList.setVisibleRowCount(3);
        inferenceChannelObjectsScrollPane.setViewportView(inferenceChannelObjectsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(inferenceChannelObjectsScrollPane, gridBagConstraints);

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageLabel.setName("messageLabel"); // NOI18N
        messageLabel.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel1.add(messageLabel, gridBagConstraints);

        mainPanel1.add(centerPanel1, java.awt.BorderLayout.CENTER);

        southPanel1.setBackground(new java.awt.Color(255, 255, 255));
        southPanel1.setName("southPanel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getActionMap(SetupInferenceChannelsBox.class, this);
        includeButton.setAction(actionMap.get("include")); // NOI18N
        includeButton.setBackground(new java.awt.Color(255, 255, 255));
        includeButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        includeButton.setName("includeButton"); // NOI18N
        includeButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel1.add(includeButton);

        excludeButton.setAction(actionMap.get("exclude")); // NOI18N
        excludeButton.setBackground(new java.awt.Color(255, 255, 255));
        excludeButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        excludeButton.setName("excludeButton"); // NOI18N
        excludeButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel1.add(excludeButton);

        clearButton.setAction(actionMap.get("clear")); // NOI18N
        clearButton.setBackground(new java.awt.Color(255, 255, 255));
        clearButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        clearButton.setName("clearButton"); // NOI18N
        clearButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel1.add(clearButton);

        okButton.setAction(actionMap.get("ok")); // NOI18N
        okButton.setBackground(new java.awt.Color(255, 255, 255));
        okButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        okButton.setName("okButton"); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel1.add(okButton);

        mainPanel1.add(southPanel1, java.awt.BorderLayout.SOUTH);

        addDialog.getContentPane().add(mainPanel1, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("setupInferenceChannelsBox.title")); // NOI18N
        setModal(true);
        setName("setupInferenceChannelsBox"); // NOI18N

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setName("centerPanel"); // NOI18N
        centerPanel.setLayout(new java.awt.BorderLayout());

        channelsScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        channelsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        channelsScrollPane.setName("channelsScrollPane"); // NOI18N

        channelsTable.setName("channelsTable"); // NOI18N
        channelsTable.setSelectionBackground(new java.awt.Color(212, 208, 200));
        channelsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelsScrollPane.setViewportView(channelsTable);

        centerPanel.add(channelsScrollPane, java.awt.BorderLayout.CENTER);

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
    private javax.swing.JScrollPane channelsScrollPane;
    private javax.swing.JTable channelsTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox databaseObjectsComboBox;
    private javax.swing.JLabel databaseObjectsLabel;
    private javax.swing.JButton excludeButton;
    private javax.swing.JButton includeButton;
    private javax.swing.JLabel inferenceChannelNameLabel;
    private javax.swing.JTextField inferenceChannelNameTextField;
    private javax.swing.JLabel inferenceChannelObjectsLabel;
    private javax.swing.JList inferenceChannelObjectsList;
    private javax.swing.JScrollPane inferenceChannelObjectsScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainPanel1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel southPanel1;
    // End of variables declaration//GEN-END:variables
    private static final String channelsTableColumns[] = {"ID", "NAME", "OBJECTS", "LENGTH"};
}
