module com.daniel.migales.client_with_maven {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.daniel.migales.client_with_maven to javafx.fxml;
    exports com.daniel.migales.client_with_maven;
    requires data.paquete;
    requires javafx.graphicsEmpty;
}
