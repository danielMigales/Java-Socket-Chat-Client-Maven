package com.daniel.migales.client_with_maven;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import dataPaquete.DataPaquete;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author daniel migales puertas
 *
 */
public class SecondaryController {

    //variables para la conexion
    private String SERVER_IP; //cambiar esta variable segun la ip del servidor
    private final int SERVER_PORT = 40000;
    private final int CLIENT_PORT = 49999; //puerto diferente para que no se cruce si la ejecuto desde el mismo ordenador

     @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane AnchorPaneChat;

    @FXML
    private Button buttonSendChat;

    @FXML
    private TextArea textAreaWatchMessages;

    @FXML
    private TextField textFieldChatUsername;

    @FXML
    private TextField textFieldChatIPAddress;

    @FXML
    private TextField textFieldWriteArea;

    @FXML
    private TextField textAreaHostNameChat;

    @FXML
    private Button buttonLogOutChat;

    @FXML
    private TextField textFieldIP_Server;

    @FXML
    private TextField textFieldIPdestino;

     @FXML
    void initialize() {
        assert AnchorPaneChat != null : "fx:id=\"AnchorPaneChat\" was not injected: check your FXML file 'secondary.fxml'.";
        assert buttonSendChat != null : "fx:id=\"buttonSendChat\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textAreaWatchMessages != null : "fx:id=\"textAreaWatchMessages\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textFieldChatUsername != null : "fx:id=\"textFieldChatUsername\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textFieldChatIPAddress != null : "fx:id=\"textFieldChatIPAddress\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textFieldWriteArea != null : "fx:id=\"textFieldWriteArea\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textAreaHostNameChat != null : "fx:id=\"textAreaHostNameChat\" was not injected: check your FXML file 'secondary.fxml'.";
        assert buttonLogOutChat != null : "fx:id=\"buttonLogOutChat\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textFieldIP_Server != null : "fx:id=\"textFieldIP_Server\" was not injected: check your FXML file 'secondary.fxml'.";
        assert textFieldIPdestino != null : "fx:id=\"textFieldIPdestino\" was not injected: check your FXML file 'secondary.fxml'.";

        //Al iniciar la escena se realiza lo siguiente:
        getUserInfo();
        socketThread();

    }

    @FXML
    void sendMessage(ActionEvent event) {

         //obtener el mensaje del los campos de texto de la interfaz
        var userName = textFieldChatUsername.getText();
        var IPAddress = textFieldChatIPAddress.getText();
        var message = textFieldWriteArea.getText();
        var destinatario = textFieldIPdestino.getText();
        
        //encriptar mensaje
        Cryptography encryption = new Cryptography();
        String cryptoMessage = "";
        try {
            cryptoMessage = encryption.encrypt(message);
        } catch (Exception ex) {

        }
        System.out.println("El mensaje ha sido encriptado antes del envio: " + cryptoMessage);

        //se envia encriptado
         DataPaquete outputData = new DataPaquete(userName, IPAddress, cryptoMessage, destinatario );
        //System.out.println(mensaje);

        try {
            
            SERVER_IP = textFieldIP_Server.getText();
            
            //flujo de informacion y se asocia al socket
            try ( //crear el socket (conector con el servidor)
                    Socket socket = new Socket(SERVER_IP, SERVER_PORT); //flujo de informacion y se asocia al socket
                    java.io.ObjectOutputStream outFlow = new ObjectOutputStream(socket.getOutputStream())) {
                //enviar al dato
                outFlow.writeObject(outputData);
            }

            //el mensaje sin encriptar se muestra en el area de chat
            textAreaWatchMessages.appendText(message + "\n");
            //se limpia el area de envio de mensajes
            textFieldWriteArea.clear();

        } catch (IOException ex) {

            System.out.println("No se pudo crear el socket para conectar con el servidor en la direccion " + SERVER_IP);
        }

    }

    public void getUserInfo() {

        //obtener los datos para colocarlos en el las casillas del chat (datos usuario)
        try {
            //User user = null;
            //obtener el nombre de usuario
            //String username = user.getUsername();
            
            //obtener direccion IP y el nombre de host y colocarlos
            InetAddress address = InetAddress.getLocalHost();
            String hostName = address.getHostName();
            textAreaHostNameChat.setText(hostName);
            String localIpAddress = address.getHostAddress();
            textFieldChatIPAddress.setText(localIpAddress);

        } catch (UnknownHostException ex) {

        }

    }

    public void socketThread() {

        //lanzar el hilo del socket
        var thread = new Thread() {
            @Override
            public void run() {

                try {
                    DataPaquete inputData;
                    var serverClient = new ServerSocket(CLIENT_PORT);
                    Socket socket;

                    while (true) {
                        socket = serverClient.accept();

                        //crear el flujo de entrada asociado al socket
                        var inputFlow = new ObjectInputStream(socket.getInputStream());

                        //extraer el mensaje
                        inputData = (DataPaquete) inputFlow.readObject();

                        var userName = inputData.getNombreUsuario();
                        var ipAddress = inputData.getDireccionIP();
                        var message = inputData.getMensaje();
                        var concatenatedMessage = userName + "/" + ipAddress + " dice:\t" + message + "\n";
                        System.out.println(concatenatedMessage);

                        //visualizar los datos en la interfaz
                        Platform.runLater(() -> {
                            textAreaWatchMessages.appendText(concatenatedMessage);
                        });
                    }
                } catch (IOException | ClassNotFoundException ex) {

                    System.out.println("No se ha podido crear el servidor de escucha en el puerto " + CLIENT_PORT
                            + " en la aplicacion cliente");
                    System.out.println("No se ha encontrado la clase DataPaquete");
                    System.out.println(ex.getMessage());
                }
            }
        };
        //iniciar el hilo
        thread.start();
    }

    @FXML
    void logOut(ActionEvent event) throws IOException {

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        stage = new Stage();
        AnchorPane root = FXMLLoader.load(getClass().getResource("/view/Start.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

}
