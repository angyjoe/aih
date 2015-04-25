package info.sarihh.antiinferencehub;

import java.io.IOException;
import java.net.Socket;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
/*
 * Author: Sari Haj Hussein
 */
public class QueryAnalyzer {

    public QueryAnalyzer(View server, Socket client) {
        this.server = server;
        this.client = client;
    }

    protected final void analyze(String query) throws IOException, SQLException, ParserConfigurationException {
        if (!query.startsWith("SELECT")) {
            executeDDL(query);
        } else if (InitializeKeysBox.getKeyScheme().equals("No Inference Channel Scheme")) {
            executeSelect(query);
        } else if (InitializeKeysBox.getKeyScheme().equals("Single Inference Channel Scheme")) {
            processSingleInferenceChannelScheme(query);
        } else if (InitializeKeysBox.getKeyScheme().equals("Multiple Inference Channel Scheme Without Repeated Objects")) {
            processMultipleInferenceChannelSchemeWithoutRepeatedObjects(query);
        } else if (InitializeKeysBox.getKeyScheme().equals("Multiple Inference Channel Scheme With Repeated Objects")) {
            processMultipleInferenceChannelSchemeWithRepeatedObjects(query);
        }
    }

    private final void executeDDL(String query) throws SQLException {
        Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.executeUpdate(query);
        statement.close();
    }

    private final void executeSelect(String query) throws IOException, SQLException, ParserConfigurationException {
        Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery(query);
        Document doc = JDBCUtil.toDocument(resultSet);
        server.sendTo(JDBCUtil.serialize(doc), client);
        resultSet.close();
        statement.close();
    }

    private final int findChannelContainingObject(String objectName) {
        try {
            String query = "SELECT INFERENCE_CHANNEL_ID, INFERENCE_CHANNEL_OBJECTS " +
                    "FROM INFERENCE_CHANNEL ORDER BY INFERENCE_CHANNEL_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            int channelID = 0;
            while (resultSet.next()) {
                if (resultSet.getString("INFERENCE_CHANNEL_OBJECTS").contains(objectName)) {
                    channelID = resultSet.getInt("INFERENCE_CHANNEL_ID");
                    break;
                }
            }
            resultSet.close();
            statement.close();
            return channelID;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return 0;
        }
    }

    private final Vector<Integer> findChannelsContainingObject(String objectName) {
        try {
            String query = "SELECT INFERENCE_CHANNEL_ID, INFERENCE_CHANNEL_OBJECTS " +
                    "FROM INFERENCE_CHANNEL ORDER BY INFERENCE_CHANNEL_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            Vector<Integer> channelsIDS = new Vector<Integer>();
            while (resultSet.next()) {
                if (resultSet.getString("INFERENCE_CHANNEL_OBJECTS").contains(objectName)) {
                    channelsIDS.add(resultSet.getInt("INFERENCE_CHANNEL_ID"));
                }
            }
            resultSet.close();
            statement.close();
            return channelsIDS;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private final Vector<String> getChannelObjects(int channelID) {
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
            Vector<String> channelObjects = new Vector<String>();
            while (st.hasMoreTokens()) {
                channelObjects.add(st.nextToken().trim());
            }
            return channelObjects;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
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

    private final Vector<UUID> getObjectKeys(String objectName, int channelID) {
        try {
            String query = "SELECT OBJECT_KEYS FROM THE_KEY WHERE OBJECT_NAME = ? AND INFERENCE_CHANNEL_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setString(1, objectName);
            statement.setInt(2, channelID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String objects = resultSet.getString(1).substring(1, resultSet.getString(1).length() - 1);
            resultSet.close();
            statement.close();
            StringTokenizer st = new StringTokenizer(objects, ",");
            Vector<UUID> objectKeys = new Vector<UUID>();
            int i = 0;
            while (st.hasMoreTokens()) {
                objectKeys.add(UUID.fromString(st.nextToken().trim()));
                i++;
            }
            return objectKeys;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private final Vector<String> getObjectsInQuery(String query) {
        Vector<String> objectsInQuery = new Vector<String>();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }
        String afterFromCluase = null;
        if (query.contains("WHERE")) {
            afterFromCluase = query.substring(query.indexOf("FROM") + 5, query.indexOf("WHERE"));
        } else {
            afterFromCluase = query.substring(query.indexOf("FROM") + 5);
        }
        StringTokenizer st = new StringTokenizer(afterFromCluase, ",");
        while (st.hasMoreTokens()) {
            objectsInQuery.add(st.nextToken().trim());
        }
        return objectsInQuery;
    }

    private static final String[] getSuperClientsAddresses() {
        try {
            String query = "SELECT SUPER_CLIENT_ADDRESS FROM SUPER_CLIENT ORDER BY SUPER_CLIENT_ID";
            Statement statement = View.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.last();
            int superClientsCount = resultSet.getRow();
            resultSet.beforeFirst();
            String[] superClientsAddresses = new String[superClientsCount];
            int i = 0;
            while (resultSet.next()) {
                superClientsAddresses[i] = resultSet.getString("SUPER_CLIENT_ADDRESS");
                i++;
            }
            resultSet.close();
            statement.close();
            return superClientsAddresses;
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private final boolean isSuperClient() {
        String[] superClientsAddresses = getSuperClientsAddresses();
        for (String superClientAddress : superClientsAddresses) {
            if (client.getInetAddress().getHostAddress().equals(superClientAddress)) {
                return true;
            }
        }
        return false;
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

    private final void processMultipleInferenceChannelSchemeWithRepeatedObjects(String query) throws IOException, SQLException, ParserConfigurationException {
        Vector<String> objectsInQuery = getObjectsInQuery(query);
        boolean isSuper = isSuperClient();
        boolean blockQuery = false;
        String reservedObject = null;
        for (String object : objectsInQuery) {
            int channelID = findChannelContainingObject(object);
            if (channelID != 0) {
                Vector<UUID> objectKeys = getObjectKeys(object, channelID);
                if (objectKeys.size() == 0) {
                    blockQuery = true;
                    reservedObject = object;
                    break;
                } else {
                    Vector<Integer> channelsIDS = findChannelsContainingObject(object);
                    for (Integer ID : channelsIDS) {
                        objectKeys = getObjectKeys(object, ID);
                        int randomIndex = randomGenerator.nextInt(objectKeys.size());
                        UUID randomKey = objectKeys.elementAt(randomIndex);
                        Vector<UUID> newObjectKeys = new Vector<UUID>();
                        newObjectKeys.add(randomKey);
                        updateObjectKeys(object, newObjectKeys.toString(), ID);
                        Vector<String> channelObjects = getChannelObjects(ID);
                        for (String channelObject : channelObjects) {
                            if (!channelObject.equals(object)) {
                                objectKeys = getObjectKeys(channelObject, ID);
                                objectKeys.remove(randomKey);
                                updateObjectKeys(channelObject, objectKeys.toString(), ID);

                                objectKeys = getObjectKeys(channelObject, ID);
                                if (objectKeys.size() == 0) {
                                    Vector<Integer> channelsIDSS = findChannelsContainingObject(channelObject);

                                    for (Integer IDD : channelsIDSS) {
                                        objectKeys = getObjectKeys(channelObject, IDD);
                                        objectKeys.removeAllElements();
                                        updateObjectKeys(channelObject, objectKeys.toString(), IDD);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        if (blockQuery) {
            if (isSuper) {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".", client);
                executeSelect(query);
            } else {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".", client);
            }
        } else {
            executeSelect(query);
        }
    }

    private final void processMultipleInferenceChannelSchemeWithoutRepeatedObjects(String query) throws IOException, SQLException, ParserConfigurationException {
        Vector<String> objectsInQuery = getObjectsInQuery(query);
        boolean isSuper = isSuperClient();
        boolean blockQuery = false;
        String reservedObject = null;
        for (String object : objectsInQuery) {
            int channelID = findChannelContainingObject(object);
            if (channelID != 0) {
                Vector<UUID> objectKeys = getObjectKeys(object, channelID);
                if (objectKeys.size() == 0) {
                    blockQuery = true;
                    reservedObject = object;
                    break;
                } else {
                    int randomIndex = randomGenerator.nextInt(objectKeys.size());
                    UUID randomKey = objectKeys.elementAt(randomIndex);
                    Vector<UUID> newObjectKeys = new Vector<UUID>();
                    newObjectKeys.add(randomKey);
                    updateObjectKeys(object, newObjectKeys.toString(), channelID);
                    Vector<String> channelObjects = getChannelObjects(channelID);
                    for (String channelObject : channelObjects) {
                        if (!channelObject.equals(object)) {
                            objectKeys = getObjectKeys(channelObject, channelID);
                            objectKeys.remove(randomKey);
                            updateObjectKeys(channelObject, objectKeys.toString(), channelID);
                        }
                    }
                }
            }
        }
        if (blockQuery) {
            if (isSuper) {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".", client);
                executeSelect(query);
            } else {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".", client);
            }
        } else {
            executeSelect(query);
        }
    }

    private final void processSingleInferenceChannelScheme(String query) throws IOException, SQLException, ParserConfigurationException {
        Vector<String> objectsInQuery = getObjectsInQuery(query);
        Vector<String> channelObjects = getChannelObjects(maxChannelID());
        boolean isSuper = isSuperClient();
        boolean blockQuery = false;
        String reservedObject = null;
        for (String object : objectsInQuery) {
            if (channelObjects.contains(object)) {
                Vector<UUID> objectKeys = getObjectKeys((String) object, maxChannelID());
                if (objectKeys.size() == 0) {
                    blockQuery = true;
                    reservedObject = object;
                    break;
                } else {
                    int randomIndex = randomGenerator.nextInt(objectKeys.size());
                    UUID randomKey = objectKeys.elementAt(randomIndex);
                    Vector<UUID> newObjectKeys = new Vector<UUID>();
                    newObjectKeys.add(randomKey);
                    updateObjectKeys((String) object, newObjectKeys.toString(), maxChannelID());
                    for (String channelObject : channelObjects) {
                        if (!channelObject.equals(object)) {
                            objectKeys = getObjectKeys(channelObject, maxChannelID());
                            objectKeys.remove(randomKey);
                            updateObjectKeys(channelObject, objectKeys.toString(), maxChannelID());
                        }
                    }
                }
            }
        }
        if (blockQuery) {
            if (isSuper) {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT FROM A SUPER CLIENT: Access permitted to reserved object " + reservedObject + ".", client);
                executeSelect(query);
            } else {
                View.setOutputText("[" + client.getInetAddress().getHostAddress() + "] -> INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".");
                server.sendTo("INFERENCE ATTEMPT: Access denied to reserved object " + reservedObject + ".", client);
            }
        } else {
            executeSelect(query);
        }
    }

    private final void updateObjectKeys(String objectName, String objectKeys, int channelID) {
        try {
            String query = "UPDATE THE_KEY SET OBJECT_KEYS = ? WHERE OBJECT_NAME = ? " +
                    "AND INFERENCE_CHANNEL_ID = ?";
            PreparedStatement statement = View.getConnection().prepareStatement(query);
            statement.setString(1, objectKeys);
            statement.setString(2, objectName);
            statement.setInt(3, channelID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }
    private View server;
    private Socket client;
    private Random randomGenerator = new Random();
}
