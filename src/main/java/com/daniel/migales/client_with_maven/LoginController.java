package com.daniel.migales.client_with_maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author daniel migales puertas
 *
 */
public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TabPane tabPaneLoginRegister;

    @FXML
    private Tab tabLogin;

    @FXML
    private Button buttonLogin;

    @FXML
    private TextField textAreaUsernameLogin;

    @FXML
    private PasswordField textFieldPasswordLogin;

    @FXML
    private ImageView imageviewIconUserLogin;

    @FXML
    private ImageView imageviewIconPasswordLogin;

    @FXML
    private Label labelLoginOk;

    @FXML
    private Tab tabAyuda;

    @FXML
    private TextArea textAreaHelpStart;

    @FXML
    private ChoiceBox<?> ChoiBoxHelpStart;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    void initialize() {
        assert tabPaneLoginRegister != null : "fx:id=\"tabPaneLoginRegister\" was not injected: check your FXML file 'Login.fxml'.";
        assert tabLogin != null : "fx:id=\"tabLogin\" was not injected: check your FXML file 'Login.fxml'.";
        assert buttonLogin != null : "fx:id=\"buttonLogin\" was not injected: check your FXML file 'Login.fxml'.";
        assert textAreaUsernameLogin != null : "fx:id=\"textAreaUsernameLogin\" was not injected: check your FXML file 'Login.fxml'.";
        assert textFieldPasswordLogin != null : "fx:id=\"textFieldPasswordLogin\" was not injected: check your FXML file 'Login.fxml'.";
    }

    @FXML
    void login(ActionEvent event) throws IOException {

        //EN ESTA APLICACION NO HAY VALIDACION POR SER IMPOSIBLE AÑADIR LAS LIBRERIAS MYSQL
        //obtencion de los textos
        String username = textAreaUsernameLogin.getText();
        String password = textFieldPasswordLogin.getText();

        if (!username.isEmpty() && (!password.isEmpty())) {

            User user = new User(username, password);
            //se guardan los datos del login
            guardarArchivo(user);
            //se llama a la siguiente ventana
            switchToChat(event);

        } else {
            System.out.println("Se necesita introducir texto");
        }

    }

    @FXML
    private void switchToChat(Event event) throws IOException {
        //App.setRoot("secondary"); //lo quito porque me llama a la ventana con el tamaño de la primera

        //cerrar ventana
        closeWindow(event);
        //se inicia la ventana de chat instanciando la clase Stage y FXMLLoader
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(App.class.getResource("Chat.fxml"));

        Scene scene = new Scene(loader.load());
        secondStage.setScene(scene);
        //se añaden los parametros de aspecto a la nueva ventana
        //secondStage.initStyle(StageStyle.UTILITY); //aparece el boton minimizar y cerrar en el marco
        secondStage.setTitle("Java Socket Chat");
        //secondStage.setAlwaysOnTop(true); //siempre encima
        secondStage.setResizable(false); //no modificable de tamaño
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.show();
    }

    @FXML
    private void closeWindow(Event event) {

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void guardarArchivo(User user) throws IOException {

        //guarda los resultados en un archivo txt
        String ruta = "user.txt";
        File file = new File(ruta);

        // Si el archivo no existe es creado
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(file, false);
            bw = new BufferedWriter(fw);
            bw.write(user.username + " " + user.password);
        } catch (IOException ex) {

        } finally {
            try {
                bw.close();
            } catch (IOException ex2) {

            }
        }
    }
}
