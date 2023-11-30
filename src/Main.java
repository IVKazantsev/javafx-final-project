import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

/////////////// Заголовок ///////////////
        Image icon = new Image("file:img/icon.png", 50, 50, true, true);
        ImageView iconView = new ImageView(icon);

        Text title = new Text("Todoist");

        FlowPane titleContainer = new FlowPane(iconView, title);
/////////////// Список дел ///////////////
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
/////////////// Форма добавления дела ///////////////

        TextField input = new TextField();
        input.setPromptText("What to do?");

        Button saveButton = new Button("Save");

        FlowPane newTodoForm = new FlowPane(input, saveButton);
/////////////// Копирайт и навигация ///////////////
        Label copyright = new Label("© 2023 Todoist Kazancev Korol");
        // Переход по вкладкам
        Button navButton1 = new Button("-1 day");
        Button navButton2 = new Button("+1 day");
        Button navButton3 = new Button("Today");
        Button navButton4 = new Button("Reporting");

        FlowPane menu = new FlowPane(
                navButton1,
                navButton2,
                navButton3,
                navButton4);
        FlowPane footer = new FlowPane(menu, copyright);

/////////////// Корневой узел ///////////////
        Group root = new Group(titleContainer,
                todos,
                newTodoForm,
                footer);
/////////////// Настройка отступов ///////////////
        root.setLayoutX(80);
        titleContainer.setLayoutY(80);
        titleContainer.setHgap(5);

        todos.setLayoutY(titleContainer.getLayoutY() + 80);

        newTodoForm.setLayoutY(todos.getLayoutY() + getTodos().length * 20 + 20);
        newTodoForm.setHgap(5);
        menu.setLayoutY(newTodoForm.getLayoutY() + 30);
        menu.setHgap(5);
        footer.setLayoutY(newTodoForm.getLayoutY() + 30);
        footer.setHgap(5);
        footer.setVgap(10);

/////////////// Нажатие на кнопку Save ///////////////
        saveButton.setOnAction(actionEvent -> {
            if(!input.getText().trim().isEmpty()) {
                Todo todo = new Todo(input.getText());
                input.clear();
                input.setPromptText("What to do?");
                CheckBox todoCheckBox = new CheckBox(todo.getTitle());
                todoCheckBox.setSelected(todo.isCompleted());

                try {
                    if (getTodos().length != 0) {
                        todoCheckBox.setLayoutY(todos.getChildren().get(getTodos().length - 1).getLayoutY() + 20);
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
                todos.getChildren().add(todoCheckBox);

                todoCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        try {
                            Todo[] tempTodosArray = getTodos();
                            tempTodosArray[tempTodosArray.length - 1].done();

                            File todosFile = getTodosFile();
                            FileWriter writer = new FileWriter(todosFile, false);
                            for (Todo todo1 :
                                    tempTodosArray) {
                                writer.write(todo1.saveTodo() + "\n");
                                writer.flush();
                            }
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = getTodos();
                            tempTodosArray[tempTodosArray.length - 1].undone();

                            File todosFile = getTodosFile();
                            FileWriter writer = new FileWriter(todosFile, false);
                            for (Todo todo1 :
                                    tempTodosArray) {
                                writer.write(todo1.saveTodo() + "\n");
                                writer.flush();
                            }
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                try {
                    if (getTodos().length != 0) {
                        newTodoForm.setLayoutY(todos.getLayoutY() + getTodos().length * 20 + 20);
                    } else {
                        newTodoForm.setLayoutY(todos.getLayoutY() + 40);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                footer.setLayoutY(newTodoForm.getLayoutY() + 30);
            }
        });

/////////////// Изменение галочки в чекбоксе ///////////////
        for (int i = 0; i < todos.getChildren().size(); i++) {
            Node nodeOut = todos.getChildren().get(i);
            if (nodeOut instanceof CheckBox) {
                int finalI = i;
                ((CheckBox) nodeOut).selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        try {
                            Todo[] tempTodosArray = getTodos();
                            tempTodosArray[finalI].done();

                            File todosFile = getTodosFile();
                            FileWriter writer = new FileWriter(todosFile, false);
                            for (Todo todo:
                                 tempTodosArray) {
                                writer.write(todo.saveTodo() + "\n");
                                writer.flush();
                            }
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = getTodos();
                            tempTodosArray[finalI].undone();

                            File todosFile = getTodosFile();
                            FileWriter writer = new FileWriter(todosFile, false);
                            for (Todo todo:
                                    tempTodosArray) {
                                writer.write(todo.saveTodo() + "\n");
                                writer.flush();
                            }
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

/////////////// Настройка окна ///////////////
        // Все визуальные элементы помещаем в контейнер, который хранится в сцене
        Scene scene = new Scene(root, Color.rgb(220, 220, 220));
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
                System.out.println("Created new file with todos");
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