package files.project_prog2_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUIController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}