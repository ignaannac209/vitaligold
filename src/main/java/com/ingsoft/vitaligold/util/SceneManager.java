package main.java.com.ingsoft.vitaligold.util;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.ingsoft.vitaligold.controller.LoginController;
import main.java.com.ingsoft.vitaligold.controller.MainMenuController;
import main.java.com.ingsoft.vitaligold.controller.RegistroController;
import main.java.com.ingsoft.vitaligold.model.Auth;
import main.java.com.ingsoft.vitaligold.repository.AuthRepository;
import main.java.com.ingsoft.vitaligold.service.AuthService;

/**
 * Controla el cambio de pantallas de la aplicación.
 */
public class SceneManager {

    private static final String VIEW_LOGIN = "/main/resources/view/login-view.fxml";
    private static final String VIEW_REGISTRO = "/main/resources/view/registro-view.fxml";
    private static final String VIEW_MAIN_MENU = "/main/resources/view/main-menu-view.fxml";

    private final Stage primaryStage;
    private final AuthService authService;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        AuthRepository authRepository = new AuthRepository();
        this.authService = new AuthService(authRepository);
    }

    /** Muestra la pantalla de Login. */
    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_LOGIN));
        loader.setControllerFactory(clazz -> {
            if (clazz == LoginController.class) {
                return new LoginController(authService, this);
            }
            return instantiate(clazz);
        });

        renderScene(loader, "VitaliGold Centro de Salud - Iniciar Sesión", 480, 560, false);
    }

    /** Muestra la pantalla de Registro. */
    public void showRegistroView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_REGISTRO));
        loader.setControllerFactory(clazz -> {
            if (clazz == RegistroController.class) {
                return new RegistroController(authService, this);
            }
            return instantiate(clazz);
        });

        renderScene(loader, "VitaliGold Centro de Salud - Crear Cuenta", 480, 620, false);
    }

    /**
     * Muestra el Menú Principal (Dashboard).
     */
    public void showMainMenuView(Auth usuarioAutenticado) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_MAIN_MENU));
        loader.setControllerFactory(clazz -> {
            if (clazz == MainMenuController.class) {
                return new MainMenuController(this, usuarioAutenticado);
            }
            return instantiate(clazz);
        });

        renderScene(loader, "VitaliGold Centro de Salud - Menú Principal", 1180, 760, true);
    }

 
    private void renderScene(FXMLLoader loader, String title, double width, double height, boolean resizable) throws IOException {
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setResizable(resizable);
        if (resizable) {
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
        }

        // Al reutilizar el mismo Stage para cambiar de pantalla, el ancho/alto
        // anteriores (por ejemplo, los 1180x760 del Menú Principal) todavía no
        // se han actualizado al nuevo tamaño de la Scene en el instante en que
        // se llama a centerOnScreen(), así que la ventana queda descentrada.
        // sizeToScene() fuerza el ajuste inmediato, y el runLater() asegura que
        // el centrado se calcule ya con el tamaño correcto en cualquier SO.
        primaryStage.sizeToScene();
        Platform.runLater(primaryStage::centerOnScreen);

        primaryStage.show();
    }


    private Object instantiate(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear el controlador " + clazz.getName(), e);
        }
    }
}
