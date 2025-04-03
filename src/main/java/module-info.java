module org.example.jobtitletask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.jobtitletask to javafx.fxml;
    exports org.example.jobtitletask;
}