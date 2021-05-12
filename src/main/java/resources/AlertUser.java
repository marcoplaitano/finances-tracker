package src.main.java.resources;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class AlertUser {
    public static void show(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.ERROR, title, ButtonType.OK);
        if (headerText != null)
            alert.setHeaderText(headerText);
        if (contentText != null)
            alert.setContentText(contentText);
        alert.showAndWait();
    }
}
