import javafx.application.Application;
import javafx.scene.Scene;
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
import javafx.scene.control.CheckBox;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {

    public static void main(String[] args) {

        launch(args); // Запуск JavaFX
    }

    // start - абстрактный метод класса Application
    @Override
    public void start(Stage stage) throws IOException { // Stage - пользовательский интерфейс

        Group root = new Group(); // Корневой узел
        // Картинка заголовка
        Image icon = new Image("file:img/icon.png", 50, 50, true, true);
        ImageView iconView = new ImageView(icon);

        // Заголовок
        Text title = new Text("Todoist");

        FlowPane titleContainer = new FlowPane(iconView, title);

        root.getChildren().add(titleContainer);

        // Список дел
        Group todos = new Group();
        Todo[] todosArray = getTodos();

        Text nothingTodo = new Text("Nothing to do here");
        if (todosArray.length == 0) {
            todos.getChildren().add(nothingTodo);
        }

        for (int i = 0; i < todosArray.length; i++) {
            CheckBox todo = new CheckBox(todosArray[i].getTitle());
            todo.setSelected(todosArray[i].isCompleted());
            todos.getChildren().add(todo);
            todo.setLayoutY(todos.getLayoutY() + i * 20);
        }
        root.getChildren().add(todos);

        // Ввод нового дела
        TextField input = new TextField("What to do?");

        // Кнопка добавления
        Button saveButton = new Button("Save");

//        int todosCount = getTodos().length; // Подсчет количества тудушек для корректного отображения отступов

        FlowPane newTodoForm = new FlowPane(input, saveButton);

        System.out.println(getTodos().length);

        root.getChildren().add(newTodoForm);

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

        titleContainer.setLayoutX(80);    // установка положения надписи по оси X
        titleContainer.setLayoutY(80);    // установка положения надписи по оси Y
        titleContainer.setHgap(5);

        todos.setLayoutX(80);   // установка положения надписи по оси X
        todos.setLayoutY(titleContainer.getLayoutY() + titleContainer.getHeight() + 80);    // установка положения надписи по оси Y

        newTodoForm.setLayoutX(80);   // установка положения надписи по оси X
        newTodoForm.setLayoutY(todos.getLayoutY() + getTodos().length * 20 + 20);    // установка положения надписи по оси Y
        newTodoForm.setHgap(5);

        footer.setLayoutX(80);
        footer.setLayoutY(newTodoForm.getLayoutY() + 30);
        footer.setHgap(5);

        root.getChildren().add(footer);

        saveButton.setOnAction(actionEvent -> {
            Todo todo = new Todo(input.getText());
            CheckBox todoCkeckBox = new CheckBox(todo.getTitle());
            todoCkeckBox.setSelected(todo.isCompleted());

            try {
                if (getTodos().length != 0) {
                    todoCkeckBox.setLayoutY(todos.getChildren().get(getTodos().length - 1).getLayoutY() + 20);
                } else {
                    todos.getChildren().clear();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                addTodo(todo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            todos.getChildren().add(todoCkeckBox);

            try {
                if (getTodos().length != 0) {
                    newTodoForm.setLayoutY(todos.getLayoutY() + getTodos().length * 20 + 20);
                } else {
                    newTodoForm.setLayoutY(todos.getLayoutY() + 20 + 20);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            newTodoForm.setHgap(5);
            footer.setLayoutY(newTodoForm.getLayoutY() + 30);
            footer.setHgap(5);
        });

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

    public void addTodo(Todo todo) throws IOException {
        File todosFile = getTodosFile();
        try {
            FileWriter writer = new FileWriter(todosFile, true);
            writer.write(todo.saveTodo() + "\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Todo[] getTodos() throws IOException {
        File todosFile = getTodosFile();
        int fileLength = 0; // счетчик строк

        try (BufferedReader reader = new BufferedReader(new FileReader(todosFile))) {
            while (reader.readLine() != null) {
                fileLength++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        Todo[] todos = new Todo[fileLength];
        if (todos.length == 0) {
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