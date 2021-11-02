package dz.webinar2.lesson4_javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField textField;

    @FXML
    private TextArea textArea;



    public void onClickButton(ActionEvent event) {
        if (textField.getText().isEmpty() || textField.getText().isBlank()) {
            textField.requestFocus();
            textField.setText("");
            return;
        } else {
            textArea.appendText(textField.getText() + "\n");
            textField.requestFocus();
            textField.setText("");
        }
    }
}