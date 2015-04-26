package info.sarihh.antiinferencehub;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class App extends SingleFrameApplication {

    @Override
    protected final void startup() {
        if (param.equals("-Hub")) {
            show(new View(this));
        } else if (param.equals("-HubClient")) {
            show(new ClientView(this));
        }
    }

    @Override
    protected final void configureWindow(java.awt.Window root) {
    }

    public static final App getApplication() {
        return Application.getInstance(App.class);
    }

    public static final void main(String[] args) {
        param = args[0];
        launch(App.class, args);
    }
    private static String param;
}
