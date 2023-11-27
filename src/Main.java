import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.Charset;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Flow;

public class Main extends Application {

    public static void main(String[] args) {

        launch(args); // Запуск JavaFX
    }

    // start - абстрактный метод класса Application
    @Override
    public void start(Stage stage) throws IOException { // Stage - пользовательский интерфейс

        // Картинка заголовка
        Image icon = new Image("file:img/icon.png", 50, 50, true, true);
        ImageView iconView = new ImageView(icon);

        // Заголовок
        Text title = new Text("Todoist");

        FlowPane titleContainer = new FlowPane(iconView, title);
        titleContainer.setLayoutX(80);    // установка положения надписи по оси X
        titleContainer.setLayoutY(80);    // установка положения надписи по оси Y
        titleContainer.setHgap(5);

        // Список дел
        StringBuilder todosText = new StringBuilder();
        for (Todo todo:
             getTodos()) {
            todosText.append(todo.saveTodo()).append("\n");
        }
        Text todos = new Text(todosText.toString());

        todos.setLayoutX(80);   // установка положения надписи по оси X
        todos.setLayoutY(titleContainer.getLayoutY() + titleContainer.getHeight() + 80);    // установка положения надписи по оси Y

        // Ввод нового дела
        TextField input = new TextField("What to do?");

        // Кнопка добавления
        Button saveButton = new Button("Save");

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Todo todo = new Todo(input.getText());

                addTodo(todo);
            }
        });

        FlowPane newTodoForm = new FlowPane(input, saveButton);
        newTodoForm.setLayoutX(80);   // установка положения надписи по оси X
        newTodoForm.setLayoutY(todos.getLayoutY() + todos.computeAreaInScreen() + 20);    // установка положения надписи по оси Y
        System.out.println(todos.computeAreaInScreen());
        newTodoForm.setHgap(5);

        // Ввод нового дела
        Label copyright = new Label("© 2023 Todoist Kazancev Korol");
        // Переход по вкладкам
        Button navButton1 = new Button("-1 day");
        Button navButton2 = new Button("+1 day");
        Button navButton3 = new Button("Today");
        Button navButton4 = new Button("Reporting");

        FlowPane footer = new FlowPane(copyright,
                navButton1,
                navButton2,
                navButton3,
                navButton4);
        footer.setLayoutX(80);
        footer.setLayoutY(newTodoForm.getLayoutY() + newTodoForm.getHeight() + 30);
        footer.setHgap(5);

        Group root = new Group(titleContainer,
                todos,
                newTodoForm,
                footer
        ); // Корневой узел

        // Все визуальные элементы помещаем в контейнер, который хранится в сцене
        Scene scene = new Scene(root, Color.rgb(220, 220, 220));

        // Настройка окна
        stage.setScene(scene);
        stage.setTitle("Todoist");
        stage.setWidth(700);
        stage.setHeight(700);
        stage.show();
    }

    public File getTodosFile() {
        Date today = new Date();
        File todos = new File("./data/" + new SimpleDateFormat("yyyy-MM-dd").format(today) + ".txt");

        try {
            if (todos.createNewFile()) {
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return todos;
    }

    public void addTodo(Todo todo) {
        File todosFile = getTodosFile();

        try {
            FileWriter writer = new FileWriter(todosFile, true);
            writer.write(todo.saveTodo() + "\n");

            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Todo[] getTodos() throws IOException {
        File todosFile = getTodosFile();

        int fileLength = 0; // счетчик строк

        try (BufferedReader reader = new BufferedReader(new FileReader(todosFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLength++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        Todo[] todos = new Todo[fileLength];
        if(todos.length == 0) {
            return todos;
        }

        int counter = 0;
        BufferedReader reader = new BufferedReader(new FileReader(todosFile));
        String line;
        while ((line = reader.readLine()) != null) {
            todos[counter] = Todo.loadTodo(line);
            counter++;
        }
        reader.close();

        return todos;
    }
}