import javafx.application.Application;
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
import javafx.scene.control.CheckBox;

import java.io.*;

public class Main extends Application {
    Stage window;
    Scene todayScene, reportingScene;

    public static void main(String[] args) {

        launch(args); // Запуск JavaFX
    }

    // start - абстрактный метод класса Application
    @Override
    public void start(Stage primaryStage) throws IOException { // Stage - пользовательский интерфейс

        window = primaryStage;

        Repository repository = new Repository();

/////////////// Заголовок ///////////////

        Image icon = new Image("file:img/icon.png", 50, 50, true, true);
        ImageView iconView = new ImageView(icon);

        Text title = new Text("Todoist");

        FlowPane titleContainer = new FlowPane(iconView, title);

/////////////// Список дел ///////////////

        Group todos = new Group();
        Todo[] todosArray = repository.getTodos(null);

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

/////////////// Добавление дела ///////////////

        Button newTodoButton = new Button("New task");

/////////////// Копирайт и навигация ///////////////

        Label copyright = new Label("© 2023 Todoist Kazancev Korol");
        // Переход по вкладкам
        Button minusDayButton = new Button("-1 day");
        Button plusDayButton = new Button("+1 day");
        Button todayTodosButton = new Button("Today");
        Button reportingPageButton = new Button("Reporting");
        FlowPane menu = new FlowPane(minusDayButton,
                plusDayButton,
                todayTodosButton,
                reportingPageButton);

        FlowPane footer = new FlowPane(menu,
                copyright);

/////////////// Корневой узел ///////////////

        Group root = new Group(titleContainer,
                todos,
                newTodoButton,
                footer);

/////////////// Настройка отступов ///////////////

        root.setLayoutX(80);
        titleContainer.setLayoutY(80);
        titleContainer.setHgap(5);

        todos.setLayoutY(titleContainer.getLayoutY() + 80);

        newTodoButton.setLayoutY(todos.getLayoutY() + repository.getTodos(null).length * 20 + 20);

        menu.setLayoutY(newTodoButton.getLayoutY() + 30);
        menu.setHgap(5);
        footer.setLayoutY(newTodoButton.getLayoutY() + 30);
        footer.setHgap(5);
        footer.setVgap(10);

/////////////// Нажатие на кнопку New Task ///////////////

        newTodoButton.setOnAction(actionEvent -> {
            if (AddTodoBox.display("Adding a task")) {
                Todo todo = AddTodoBox.todo;

                CheckBox todoCkeckBox = new CheckBox(todo.getTitle());
                todoCkeckBox.setSelected(todo.isCompleted());

                try {
                    if (repository.getTodos(null).length != 0) {
                        todoCkeckBox.setLayoutY(todos.getChildren().get(repository.getTodos(null).length - 1).getLayoutY() + 20);
                    } else {
                        todos.getChildren().clear();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                repository.addTodo(todo);
                todos.getChildren().add(todoCkeckBox);

                todoCkeckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        try {
                            Todo[] tempTodosArray = repository.getTodos(null);
                            tempTodosArray[tempTodosArray.length - 1].done();
                            repository.updateTodo(tempTodosArray[tempTodosArray.length - 1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = repository.getTodos(null);
                            tempTodosArray[tempTodosArray.length - 1].undone();
                            repository.updateTodo(tempTodosArray[tempTodosArray.length - 1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                try {
                    if (repository.getTodos(null).length != 0) {
                        newTodoButton.setLayoutY(todos.getLayoutY() + repository.getTodos(null).length * 20 + 20);
                    } else {
                        newTodoButton.setLayoutY(todos.getLayoutY() + 40);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                footer.setLayoutY(newTodoButton.getLayoutY() + 30);
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
                            Todo[] tempTodosArray = repository.getTodos(null);
                            tempTodosArray[finalI].done();
                            repository.updateTodo(tempTodosArray[finalI]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = repository.getTodos(null);
                            tempTodosArray[finalI].undone();
                            repository.updateTodo(tempTodosArray[finalI]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

/////////////// Навигация ///////////////

        todayTodosButton.setOnAction(actionEvent -> window.setScene(todayScene));
        reportingPageButton.setOnAction(actionEvent -> window.setScene(reportingScene));

/////////////// Страница репорта ///////////////

        Label copyright2 = new Label("© 2023 Todoist Kazancev Korol");
        // Переход по вкладкам
        Button minusDayButton2 = new Button("-1 day");
        Button plusDayButton2 = new Button("+1 day");
        Button todayTodosButton2 = new Button("Today");
        Button reportingPageButton2 = new Button("Reporting");

        todayTodosButton2.setOnAction(actionEvent -> window.setScene(todayScene));
        reportingPageButton2.setOnAction(actionEvent -> window.setScene(reportingScene));

        FlowPane footer2 = new FlowPane(copyright2,
                minusDayButton2,
                plusDayButton2,
                todayTodosButton2,
                reportingPageButton2);

        footer2.setHgap(5);
        footer2.setLayoutY(footer.getLayoutY());

        Group layout2 = new Group();
        layout2.getChildren().add(footer2);

        layout2.setLayoutX(80);

        reportingScene = new Scene(layout2, Color.rgb(220, 220, 220));

/////////////// Настройка окна ///////////////

        // Все визуальные элементы помещаем в контейнер, который хранится в сцене
        todayScene = new Scene(root, Color.rgb(220, 220, 220));

        window.setScene(todayScene);
        window.setTitle("Todoist");
        window.setWidth(700);
        window.setHeight(700);
        window.show();
    }
}