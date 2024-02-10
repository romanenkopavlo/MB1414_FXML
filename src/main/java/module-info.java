module org.example.mb1414_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires jssc;


    opens org.example.mb1414_fx to javafx.fxml;
    exports org.example.mb1414_fx;
}