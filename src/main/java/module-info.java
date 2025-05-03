module project.project_prog2_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens files.project_prog2_javafx to javafx.fxml;
    exports files.project_prog2_javafx;
}