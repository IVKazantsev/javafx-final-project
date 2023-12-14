import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.*;

public class Main extends Application {
    Stage window;

    public static void main(String[] args) {

        launch(args); // Запуск JavaFX
    }

    // start - абстрактный метод класса Application
    @Override
    public void start(Stage primaryStage) throws IOException { // Stage - пользовательский интерфейс
        window = primaryStage;
        Repository repository = new Repository();
        LayoutScene layout = new LayoutScene(repository);
        window.setScene(layout.display());
        window.getScene().getStylesheets().add("style.css");
        window.setWidth(600);
        window.setMaxWidth(600);
        window.setHeight(700);
        window.setMaxHeight(700);
        Image icon = new Image("file:img/icon.png", 100, 100, true, true);
        window.getIcons().add(icon);
        window.setTitle("Todoist by Kazancev Korol");
        window.show();
    }
}