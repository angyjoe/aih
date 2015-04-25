package info.sarihh.antiinferencehub;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/*
 * Author: Sari Haj Hussein
 */
public class View extends FrameView {

    public View(SingleFrameApplication app) {
        super(app);
        initComponents();
    }

    @Action
    public final void clearLog() {
        logTextArea.setText("");
    }

    @Action
    public final void launchHub() {
        hub = new Thread() {

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(12345);
                    setOutputText("Waiting for connection on " + serverSocket + ".");
                    while (true) {
                        Socket client = serverSocket.accept();
                        setOutputText("A connection from " + client + ".");
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        outputStreams.put(client, out);
                        new ClientHandler(View.this, client);
                    }
                } catch (IOException ioe) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        hub.start();
        launchHubButton.setEnabled(false);
        stopHubButton.setEnabled(true);
    }

    @Action
    public final void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = App.getApplication().getMainFrame();
            aboutBox = new AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        App.getApplication().show(aboutBox);
    }

    @Action
    public final void showAdjustSuperClients() {
        if (adjustSuperClientsBox == null) {
            JFrame mainFrame = App.getApplication().getMainFrame();
            adjustSuperClientsBox = new AdjustSuperClientsBox(mainFrame);
            adjustSuperClientsBox.setLocationRelativeTo(mainFrame);
        }
        adjustSuperClientsBox.fillSuperClientsTable();
        App.getApplication().show(adjustSuperClientsBox);
    }

    @Action
    public final void showInitializeKeysBox() {
        if (initializeKeysBox == null) {
            JFrame mainFrame = App.getApplication().getMainFrame();
            initializeKeysBox = new InitializeKeysBox(mainFrame);
            initializeKeysBox.setLocationRelativeTo(mainFrame);
        }
        initializeKeysBox.fillKeysTable();
        App.getApplication().show(initializeKeysBox);
    }

    @Action
    public final void showSetupDatabaseConnectionBox() {
        if (setupDatabaseConnectionBox == null) {
            JFrame mainFrame = App.getApplication().getMainFrame();
            setupDatabaseConnectionBox = new SetupDatabaseConnectionBox(mainFrame);
            setupDatabaseConnectionBox.setLocationRelativeTo(mainFrame);
        }
        App.getApplication().show(setupDatabaseConnectionBox);
    }

    @Action
    public final void showSetupInferenceChannelBox() {
        if (setupInferenceChannelBox == null) {
            JFrame mainFrame = App.getApplication().getMainFrame();
            setupInferenceChannelBox = new SetupInferenceChannelsBox(mainFrame);
            setupInferenceChannelBox.setLocationRelativeTo(mainFrame);
        }
        setupInferenceChannelBox.fillChannelsTable();
        App.getApplication().show(setupInferenceChannelBox);
    }

    @Action
    public final void showUsersGuide() throws IOException {
        Desktop.getDesktop().open(new File("documentation/users-guide-1.0-revision-1.pdf"));
    }

    @Action
    public final void stopHub() throws IOException {
        sendToAll("QUIT");
        removeAllConnection();
        serverSocket.close();
        setOutputText("Hub stopped.");
        launchHubButton.setEnabled(true);
        stopHubButton.setEnabled(false);
    }

    protected static final void enableComponents() {
        setupInferenceChannelMenuItem.setEnabled(true);
        initializeKeysMenuItem.setEnabled(true);
        adjustSuperClientsMenuItem.setEnabled(true);
        launchHubButton.setEnabled(true);
    }

    protected static final Connection getConnection() {
        return connection;
    }

    protected final void removeConnection(Socket client) throws IOException {
        setOutputText("Removing connection with " + client + ".");
        client.close();
        outputStreams.remove(client);
    }

    private final void removeAllConnection() throws IOException {
        for (Socket client : outputStreams.keySet()) {
            setOutputText("Removing connection with " + client + ".");
            client.close();
            outputStreams.remove(client);
        }
    }

    protected final void sendTo(String message, Socket client) {
        try {
            ObjectOutputStream out = outputStreams.get(client);
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
        }
    }

    private final void sendToAll(String message) throws IOException {
        try {
            for (Socket client : outputStreams.keySet()) {
                ObjectOutputStream out = outputStreams.get(client);
                out.writeObject(message);
                out.flush();
            }
        } catch (Exception e) {
        }
    }

    protected static final void setConnection(Connection conn) {
        connection = conn;
    }

    protected static final void setOutputText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                logTextArea.append(dateFormat.format(new Date()) + " -> " + text + "\n");
                logTextArea.setCaretPosition(logTextArea.getText().length());
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        centerPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        setupDatabaseConnectionMenuItem = new javax.swing.JMenuItem();
        setupInferenceChannelMenuItem = new javax.swing.JMenuItem();
        initializeKeysMenuItem = new javax.swing.JMenuItem();
        adjustSuperClientsMenuItem = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem usersGuideMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        southPanel = new javax.swing.JPanel();
        launchHubButton = new javax.swing.JButton();
        stopHubButton = new javax.swing.JButton();

        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setName("centerPanel"); // NOI18N
        centerPanel.setLayout(new java.awt.BorderLayout());

        logScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getResourceMap(View.class);
        logScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("logScrollPane.border.title"))); // NOI18N
        logScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setName("logScrollPane"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setFont(new java.awt.Font("Tahoma", 0, 12));
        logTextArea.setLineWrap(true);
        logTextArea.setRows(5);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        logTextArea.setName("logTextArea"); // NOI18N
        logTextArea.setSelectionColor(new java.awt.Color(212, 208, 200));
        logScrollPane.setViewportView(logTextArea);

        centerPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        menuBar.setBackground(new java.awt.Color(255, 255, 255));
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setBackground(new java.awt.Color(255, 255, 255));
        fileMenu.setMnemonic('F');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(info.sarihh.antiinferencehub.App.class).getContext().getActionMap(View.class, this);
        setupDatabaseConnectionMenuItem.setAction(actionMap.get("showSetupDatabaseConnectionBox")); // NOI18N
        setupDatabaseConnectionMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        setupDatabaseConnectionMenuItem.setName("setupDatabaseConnectionMenuItem"); // NOI18N
        fileMenu.add(setupDatabaseConnectionMenuItem);

        setupInferenceChannelMenuItem.setAction(actionMap.get("showSetupInferenceChannelBox")); // NOI18N
        setupInferenceChannelMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        setupInferenceChannelMenuItem.setName("setupInferenceChannelMenuItem"); // NOI18N
        fileMenu.add(setupInferenceChannelMenuItem);

        initializeKeysMenuItem.setAction(actionMap.get("showInitializeKeysBox")); // NOI18N
        initializeKeysMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        initializeKeysMenuItem.setName("initializeKeysMenuItem"); // NOI18N
        fileMenu.add(initializeKeysMenuItem);

        adjustSuperClientsMenuItem.setAction(actionMap.get("showAdjustSuperClients")); // NOI18N
        adjustSuperClientsMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        adjustSuperClientsMenuItem.setName("adjustSuperClientsMenuItem"); // NOI18N
        fileMenu.add(adjustSuperClientsMenuItem);

        separator1.setName("separator1"); // NOI18N
        fileMenu.add(separator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setBackground(new java.awt.Color(255, 255, 255));
        helpMenu.setMnemonic('H');
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        usersGuideMenuItem.setAction(actionMap.get("showUsersGuide")); // NOI18N
        usersGuideMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        usersGuideMenuItem.setName("usersGuideMenuItem"); // NOI18N
        helpMenu.add(usersGuideMenuItem);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setBackground(new java.awt.Color(255, 255, 255));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setName("southPanel"); // NOI18N

        launchHubButton.setAction(actionMap.get("launchHub")); // NOI18N
        launchHubButton.setBackground(new java.awt.Color(255, 255, 255));
        launchHubButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        launchHubButton.setName("launchHubButton"); // NOI18N
        launchHubButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(launchHubButton);

        stopHubButton.setAction(actionMap.get("stopHub")); // NOI18N
        stopHubButton.setBackground(new java.awt.Color(255, 255, 255));
        stopHubButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        stopHubButton.setName("stopHubButton"); // NOI18N
        stopHubButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(stopHubButton);

        clearLogButton.setAction(actionMap.get("clearLog")); // NOI18N
        clearLogButton.setBackground(new java.awt.Color(255, 255, 255));
        clearLogButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        clearLogButton.setName("clearLogButton"); // NOI18N
        clearLogButton.setPreferredSize(new java.awt.Dimension(100, 21));
        southPanel.add(clearLogButton);

        setComponent(centerPanel);
        setMenuBar(menuBar);
        setStatusBar(southPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JMenuItem adjustSuperClientsMenuItem;
    private javax.swing.JPanel centerPanel;
    private final javax.swing.JButton clearLogButton = new javax.swing.JButton();
    private static javax.swing.JMenuItem initializeKeysMenuItem;
    private static javax.swing.JButton launchHubButton;
    private javax.swing.JScrollPane logScrollPane;
    private static javax.swing.JTextArea logTextArea;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JSeparator separator1;
    private javax.swing.JMenuItem setupDatabaseConnectionMenuItem;
    private static javax.swing.JMenuItem setupInferenceChannelMenuItem;
    private javax.swing.JPanel southPanel;
    private static javax.swing.JButton stopHubButton;
    // End of variables declaration//GEN-END:variables
    private AboutBox aboutBox;
    private SetupDatabaseConnectionBox setupDatabaseConnectionBox;
    private SetupInferenceChannelsBox setupInferenceChannelBox;
    private InitializeKeysBox initializeKeysBox;
    private AdjustSuperClientsBox adjustSuperClientsBox;
    private static Connection connection;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Socket, ObjectOutputStream> outputStreams = new ConcurrentHashMap<Socket, ObjectOutputStream>();
    private Thread hub;
}
