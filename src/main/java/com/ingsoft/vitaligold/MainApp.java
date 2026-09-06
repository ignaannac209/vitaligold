package main.java.com.ingsoft.vitaligold;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.com.ingsoft.vitaligold.config.DataBaseConnection;
import main.java.com.ingsoft.vitaligold.util.SceneManager;

/**
 * Muestra primero la pantalla de Login.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showLoginView();
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            DataBaseConnection.getConnectionDataBase();
            System.out.println("Conectado a la base de datos!");
        } catch (Exception e) {
            System.out.println("No se pudo conectar a la base de datos: " + e.getMessage());
            System.out.println("La aplicación continuará; puedes iniciar sesión con el usuario de prueba admin/123.");
        }
        launch(args);
    }
}
