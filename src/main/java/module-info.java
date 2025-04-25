module project.project_prog2_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens project.project_prog2_javafx to javafx.fxml;
    exports project.project_prog2_javafx;
}