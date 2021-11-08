package dz.webinar2.lesson6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    private Client client;

    @FXML
    private TextField textField;

    @FXML
    private TextArea textArea;

    public synchronized void moveMessage (String message, MessageType messageType) {
        if (message.isEmpty() || message.isBlank()) {
            return;
        } else {
            switch (messageType) {
                case SERVER: textArea.appendText("Сервер: " + message + "\n"); break;
                case CLIENT: textArea.appendText("Клиент: " + message + "\n"); break;
            }
        }
    }

    public void onClickButton(ActionEvent event) {
        String message = textField.getText();
        if (!message.isEmpty() && !message.isBlank()) {
            client.sendMessage(message);
        }
        textField.setText("");
        textField.requestFocus();
    }

    public void setClient(Client client) {
        this.client = client;
    }
}