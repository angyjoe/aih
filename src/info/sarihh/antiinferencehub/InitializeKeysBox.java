package info.sarihh.antiinferencehub;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
/*
 * Author: Sari Haj Hussein
 */
public class InitializeKeysBox extends javax.swing.JDialog {

    public InitializeKeysBox(java.awt.Frame parent) {
        super(parent);
        initComponents();
    }

    @Action
    public final void initialize() {
        keySet.clear();
        for (int i = 0; i < maxChannelLength() - 1; i++) {
            keySet.add(UUID.randomUUID());
        }
        selectKeyScheme();
        initializeKeys();
    }

    @Action
    public final void remove() {
        if (keysTable.getSelectedRow() == -1) {
            return;
        }
        int objectID = Integer.parseInt(
                ((DefaultTableModel) keysTable.getModel()).getValueAt(keysTable.getSelectedRow(), 0).toString());
        deleteKey(objectID);
        fillKeysTable();
    }

    @Action
    public final void removeAll() {
        int rowCount = ((DefaultTableModel) keysTable.getModel()).getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int objectID = Integer.parseInt(
                    ((DefaultTableModel) keysTable.getModel()).getValueAt(i, 0).toString());
            deleteKey(objectID);
        }
        fillKeysTable();
    }

    private final void addKey(String objectName, String objectKeys, int channelID) {
        try {
            String query = "INSERT INTO THE_KEY VALUES (?, ?, ?, ?)";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, maxObjectID() + 1);
            statement.setString(2, objectName);
            statement.setString(3, objectKeys);
            statement.setInt(4, channelID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    private final void deleteKey(int objectID) {
        try {
            String query = "DELETE FROM THE_KEY WHERE OBJECT_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, objectID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    protected final void fillKeysTable() {
        try {
            String query = "SELECT * FROM THE_KEY ORDER BY OBJECT_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.last();
            int keysCount = resultSet.getRow();
            resultSet.beforeFirst();
            Object[][] keys = new Object[keysCount][4];
            int i = 0;
            while (resultSet.next()) {
                keys[i][0] = resultSet.getInt("OBJECT_ID");
                keys[i][1] = resultSet.getString("OBJECT_NAME");
                keys[i][2] = resultSet.getString("OBJECT_KEYS");
                keys[i][3] = resultSet.getInt("INFERENCE_CHANNEL_ID");
                i++;
            }
            keysTable.setModel(new DefaultTableModel(keys, keysTableColumns) {

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

    private final String[] getChannelObjects(int channelID) {
        try {
            String query = "SELECT INFERENCE_CHANNEL_OBJECTS FROM INFERENCE_CHANNEL WHERE INFERENCE_CHANNEL_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setInt(1, channelID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String objects = resultSet.getString(1).substring(1, resultSet.getString(1).length() - 1);
            resultSet.close();
            statement.close();
            StringTokenizer st = new StringTokenizer(objects, ",");
            String[] objectsArray = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                objectsArray[i] = st.nextToken().trim();
                i++;
            }
            return objectsArray;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private static final String[] getChannelsObjects() {
        try {
            String query = "SELECT INFERENCE_CHANNEL_OBJECTS FROM INFERENCE_CHANNEL ORDER BY INFERENCE_CHANNEL_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.last();
            int channelsCount = resultSet.getRow();
            resultSet.beforeFirst();
            String[] channelsObjects = new String[channelsCount];
            int i = 0;
            while (resultSet.next()) {
                channelsObjects[i] = resultSet.getString("INFERENCE_CHANNEL_OBJECTS");
                i++;
            }
            resultSet.close();
            statement.close();
            return channelsObjects;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private static final Object[] getDatabaseObjects() {
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

    protected static final String getKeyScheme() {
        return keyScheme;
    }

    private final void initializeKeys() {
        if (keyScheme.equals("Single Inference Channel Scheme")) {
            String[] objects = getChannelObjects(maxChannelID());
            for (String object : objects) {
                addKey(object, keySet.toString(), maxChannelID());
            }
        } else {
            try {
                String query = "SELECT INFERENCE_CHANNEL_ID, INFERENCE_CHANNEL_LENGTH " +
                        "FROM INFERENCE_CHANNEL ORDER BY INFERENCE_CHANNEL_ID";
                Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    channelKeySet.clear();
                    int channelLength = resultSet.getInt("INFERENCE_CHANNEL_LENGTH");
                    int randomIndex = 0;
                    UUID randomKey = null;
                    for (int i = 0; i < channelLength - 1; i++) {
                        do {
                            randomIndex = randomGenerator.nextInt(keySet.size());
                            randomKey = keySet.elementAt(randomIndex);
                        } while (channelKeySet.contains(randomKey));
                        channelKeySet.add(randomKey);
                    }
                    int channelID = resultSet.getInt("INFERENCE_CHANNEL_ID");
                    String[] objects = getChannelObjects(channelID);
                    for (String object : objects) {
                        addKey(object, channelKeySet.toString(), channelID);
                    }
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                for (Throwable t : e) {
                    t.printStackTrace();
                }
            }
        }
        fillKeysTable();
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

    private final int maxChannelLength() {
        try {
            String query = "SELECT MAX(INFERENCE_CHANNEL_LENGTH) FROM INFERENCE_CHANNEL";
            Statement statement = View.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int maxChannelLength = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            return maxChannelLength;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return 0;
        }
    }

    private final int maxObjectID() {
        try {
            String query = "SELECT MAX(OBJECT_ID) FROM THE_KEY";
            Statement statement = View.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            int maxObjectID = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            return maxObjectID;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return 0;
        }
    }

    protected static final void selectKeyScheme() {
        String[] channelsObjects = getChannelsObjects();
        if (channelsObjects.length == 0) {
            keyScheme = "No Inference Channel Scheme";
        } else if (channelsObjects.length == 1) {
            keyScheme = "Single Inference Channel Scheme";
        } else {
            Object[] databaseObjects = getDatabaseObjects();
            for (Object databaseObject : databaseObjects) {
                int counter = 0;
                for (String channelObjects : channelsObjects) {
                    if (channelObjects.contains((String) databaseObject)) {
                        counter++;
                    }
                }
                if (counter > 1) {
                    keyScheme = "Multiple Inference Channel Scheme With Repeated Objects";
                    return;
                }
            }
            keyScheme = "Multiple Inference Channel Scheme Without Repeated Objects";
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        keysScrollPane = new javax.swing.JScrollPane();
        keysTable = new javax.swing.JTable();
        southPanel = new javax.swing.JPanel();
        initializeButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getResourceMap(InitializeKeysBox.class);
        setTitle(resourceMap.getString("initializeKeysBox.title")); // NOI18N
        setModal(true);
        setName("initializeKeysBox"); // NOI18N

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setName("centerPanel"); // NOI18N
        centerPanel.setLayout(new java.awt.BorderLayout());

        keysScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        keysScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        keysScrollPane.setName("keysScrollPane"); // NOI18N

        keysTable.setName("keysTable"); // NOI18N
        keysTable.setSelectionBackground(new java.awt.Color(212, 208, 200));
        keysTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keysScrollPane.setViewportView(keysTable);

        centerPanel.add(keysScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setName("southPanel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getActionMap(InitializeKeysBox.class, this);
        initializeButton.setAction(actionMap.get("initialize")); // NOI18N
        initializeButton.setBackground(new java.awt.Color(255, 255, 255));
        initializeButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        initializeButton.setName("initializeButton"); // NOI18N
        initializeButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(initializeButton);

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
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton initializeButton;
    private javax.swing.JScrollPane keysScrollPane;
    private javax.swing.JTable keysTable;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel southPanel;
    // End of variables declaration//GEN-END:variables
    private static final String keysTableColumns[] = {"OBJECT ID", "OBJECT NAME", "OBJECT KEYS", "CHANNEL ID"};
    private static Vector<UUID> keySet = new Vector<UUID>();
    private static Vector<UUID> channelKeySet = new Vector<UUID>();
    private Random randomGenerator = new Random();
    private static String keyScheme;
}
