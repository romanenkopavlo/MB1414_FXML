package org.example.mb1414_fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import jssc.SerialPortException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {
    @FXML
    Button button;
    @FXML
    CheckBox checkBox;
    @FXML
    CategoryAxis tempsX;
    @FXML
    NumberAxis vitesseY;
    @FXML
    LineChart<String, Number> lineChart;
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    String port = "COM3";
    Mb1414 mb1414;
    Timer timer;
    boolean stop = false;
    boolean accident = false;
    final int POINTS = 20;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mb1414 = new Mb1414();
        lineChart.setAnimated(false);
        lineChart.setTitle("MB1414");
        checkBox.setSelected(false);
        series.setName("MB1414");
        lineChart.getData().add(series);
        base_De_Temps();

        button.setOnAction(event -> {
            lineChart.getData().clear();
            series.getData().clear();
            lineChart.getData().add(series);
        });

        checkBox.setOnAction(event -> stop = checkBox.isSelected());
    }

    public void base_De_Temps() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!stop && !accident) {
                    Platform.runLater(() -> {
                        try {
                            mb1414.initialisationCapteur(port);
                            Thread.sleep(500);
                            if (mb1414.getDistanceDelta() != 0) {
                                series.getData().add(new XYChart.Data<>(simpleDateFormat.format(new Date()), mb1414.getVitesseFinal()));
                            }
                            if (series.getData().size() > POINTS) {
                                series.getData().remove(0);
                            }
                            if (mb1414.getVitesseFinal() >= 200 || mb1414.getVitesseFinal() <= -200) {
                                accident = true;
                                mb1414.fermerLiaison();
                                error();
                            }
                            mb1414.fermerLiaison();
                        } catch (SerialPortException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void error() {
        Alert dialogWindow = new Alert(Alert.AlertType.ERROR);
        dialogWindow.setTitle("Accident");
        dialogWindow.setHeaderText(null);
        dialogWindow.setContentText("Vous avez detruit votre voiture!");
        dialogWindow.showAndWait();
        Platform.exit();
    }
}