package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.Locale;

public class Main extends Application {
    @FXML
    Label countryLabel, cityLabel, weatherLabel, ratesLabes, zlotyLabel;


    private static void initAndShowGUI(Service s, String weatherJson, Double rate1, Double rate2) {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Country Info");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(705, 900);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, s, weatherJson, rate1, rate2);
            }
        });
    }

    private static void initFX(JFXPanel fxPanel,Service s, String weatherJson, Double rate1, Double rate2) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene(s, weatherJson, rate1, rate2);
        fxPanel.setScene(scene);
//0c0a608bb739e0d24c9e02dee048b0c6
    }
    private static Scene createScene(Service s, String weatherJson, Double rate1, Double rate2) {
        Group root  =  new  Group();
        Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
        Text  text  =  new Text();

        Label countryLab = new Label("Selected country: " + s.locCountry.getDisplayCountry(Locale.ENGLISH));
        countryLab.setLayoutX(67);
        countryLab.setLayoutY(5);
        countryLab.prefHeight(22.9);

        Label rateLab = new Label("Currency rate between " + s.currComparison + " and " + s.curr.getCurrencyCode() + ": " + rate1);
        rateLab.setLayoutX(67);
        rateLab.setLayoutY(20);

        Label nbpLab = new Label("Currency rate between PLN and " + s.curr.getCurrencyCode() + ": " + rate2);
        nbpLab.setLayoutX(67);
        nbpLab.setLayoutY(35);

        Text weatherLab = new Text("Weather: " + weatherJson);
        weatherLab.setLayoutY(70);
        weatherLab.setLayoutX(20);
        weatherLab.wrappingWidthProperty().bind(scene.widthProperty().subtract(30));
        weatherLab.requestFocus();

        WebView wikiView = new WebView();
        wikiView.setLayoutX(0);
        wikiView.setLayoutY(183);
        wikiView.setPrefSize(700, 900);
        WebEngine we = wikiView.getEngine();
        we.load("https://en.wikipedia.org/wiki/" + s.country);


        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));

        root.getChildren().add(text);
        root.getChildren().add(countryLab);
        root.getChildren().add(rateLab);
        root.getChildren().add(nbpLab);
        root.getChildren().add(wikiView);
        root.getChildren().add(weatherLab);
        return scene;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Country Info");
        primaryStage.setScene(new Scene(root, 300, 275));
        countryLabel.setText(countryLabel.getText() + " ");


        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            Service s = new Service("Spain");

            String weatherJson = s.getWeather("Tokyo");
            Double rate1 = s.getRateFor("USD");
            Double rate2 = s.getNBPRate();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    initAndShowGUI(s, weatherJson, rate1, rate2);
                }
            });
//            launch(args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
