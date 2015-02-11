package org.jtp.vlcplayer;

import com.sun.jna.NativeLibrary;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    private static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        try{
            NativeLibrary.addSearchPath("libvlc", getClass().getResource("/lib").toURI().getPath());
        }catch(Exception e){}
        //new NativeDiscovery(new DefaultWindowsNativeDiscoveryStrategy()).discover();
        //Parent root = FXMLLoader.load(getClass().getResource("JTPlayer.fxml"));
        Parent root = new MyVideoController();
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }

}
