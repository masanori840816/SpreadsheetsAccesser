/**
 * Created by masanori on 2016/12/22.
 * Main view class.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class MainForm extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        Alert alert = new Alert(Alert.AlertType.NONE , "問題が発生しました。" , ButtonType.CLOSE);
        try {
            Parent root = loader.load(getClass().getResourceAsStream("MainForm.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            alert.showAndWait()
                    .filter(response -> response == ButtonType.CLOSE)
                    .ifPresent(response -> Platform.exit());
        }
    }

}
