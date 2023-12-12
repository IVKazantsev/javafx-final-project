import javafx.application.Application;
import javafx.stage.Stage;
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

        window.setWidth(700);
        window.setHeight(700);
        window.show();
    }
}