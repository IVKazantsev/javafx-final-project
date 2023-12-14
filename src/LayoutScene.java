import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LayoutScene {
    private final Repository m_repository;

    public LayoutScene(Repository repository) {
        m_repository = repository;
    }

    public Scene display() throws IOException {
        /////////////// Заголовок ///////////////

        Image icon = new Image("file:img/icon.png", 100, 100, true, true);
        ImageView iconView = new ImageView(icon);

        Label name = new Label("Todoist");
        name.setId("title");

        FlowPane title = new FlowPane(iconView, name);

        title.setLayoutY(80);
        title.setHgap(5);

        /////////////// Копирайт и навигация ///////////////

        Label copyright = new Label("© 2023 Todoist Kazancev Korol");
        copyright.setId("copyright");
        // Переход по вкладкам
        Button minusDayButton = new Button("-1 day");
        Button plusDayButton = new Button("+1 day");
        Button todayTodosButton = new Button("Today");
        Button reportingPageButton = new Button("Reporting");

        FlowPane menu = new FlowPane();
        menu.getChildren().addAll(minusDayButton,
                plusDayButton,
                todayTodosButton,
                reportingPageButton);

        menu.setHgap(5);

        FlowPane footer = new FlowPane();
        footer.getChildren().addAll(menu, copyright);

        footer.setHgap(8);
        footer.setVgap(20);

        /////////////// Контент ///////////////

        Date dayForTodo = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dayForTodo);

        FlowPane content = new FlowPane(50, 50);
        content.setLayoutY(title.getLayoutY() + 120);

        content.getChildren().add(todosPage(false, null));

        footer.setLayoutY(550);

        /////////////// Навигация ///////////////

        minusDayButton.setOnAction(actionEvent -> {
            content.getChildren().clear();
            c.add(Calendar.DATE, -1);
            try {
                content.getChildren().add(todosPage(true, c.getTime()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        plusDayButton.setOnAction(actionEvent -> {
            content.getChildren().clear();
            c.add(Calendar.DATE, 1);
            try {
                content.getChildren().add(todosPage(true, c.getTime()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        todayTodosButton.setOnAction(actionEvent -> {
            content.getChildren().clear();
            c.setTime(dayForTodo);
            try {
                content.getChildren().add(todosPage(false, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        reportingPageButton.setOnAction(actionEvent -> {
            content.getChildren().clear();
            try {
                content.getChildren().add(reportingPage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        /////////////// Настройка шаблона ///////////////
        Group layout = new Group();
        layout.getChildren().addAll(title, content, footer);

        layout.setLayoutX(80);



        return new Scene(layout);
    }

    private Group todosPage(boolean isHistory, Date day) throws IOException {

        ScrollPane todosPane = new ScrollPane();
        todosPane.setMaxHeight(250);
        todosPane.setPrefWidth(400);

        /////////////// Список дел ///////////////
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Group content = new Group();
        content.setId("content");
        Group todos = new Group();
        todos.setId("todos");
        Todo[] todosArray = m_repository.getTodosByDay(day);

        if(day == null) {
            day = new Date();
        }
        Label date = new Label(sdf.format(day));
        date.setId("date");
        content.getChildren().add(date);

        Label nothingTodo = new Label("Nothing to do here");
        nothingTodo.setId("nothingTodo");
        if (todosArray.length == 0) {
            todos.getChildren().add(nothingTodo);
        }

        todosPane.setLayoutY(content.getLayoutY() + 40);
        for (int i = 0; i < todosArray.length; i++) {
            CheckBox todo = new CheckBox(todosArray[i].getTitle());
            todo.setSelected(todosArray[i].isCompleted());
            todo.setId("todo");
            if (isHistory && !sdf.format(day).equals(sdf.format(new Date()))) {
                todo.setDisable(true);
            }
            todos.getChildren().add(todo);
            todo.setLayoutY(content.getLayoutY() + i * 25);
        }


        if (isHistory && !sdf.format(day).equals(sdf.format(new Date()))) {
            todosPane.setContent(todos);
            content.getChildren().add(todosPane);
            return content;
        }


        /////////////// Добавление новой тудушки ///////////////

        Button newTodoButton = getNewTodoButton(todos);

        /////////////// Изменение галочки в чекбоксе ///////////////

        for (int i = 0; i < todos.getChildren().size(); i++) {
            Node nodeOut = todos.getChildren().get(i);
            if (nodeOut instanceof CheckBox) {
                int finalI = i;
                ((CheckBox) nodeOut).selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        try {
                            Todo[] tempTodosArray = m_repository.getTodosByDay(null);
                            tempTodosArray[finalI].done();
                            m_repository.updateTodo(tempTodosArray[finalI]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = m_repository.getTodosByDay(null);
                            tempTodosArray[finalI].undone();
                            m_repository.updateTodo(tempTodosArray[finalI]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

        todosPane.setContent(todos);
        content.getChildren().addAll(todosPane, newTodoButton);
        return content;
    }

    private Button getNewTodoButton(Group todos) {
        Button newTodoButton = new Button("New task");

        newTodoButton.setOnAction(actionEvent -> {
            if (AddTodoBox.display("Adding a task")) {
                Todo todo = AddTodoBox.todo;

                CheckBox todoCheckBox = new CheckBox(todo.getTitle());
                todoCheckBox.setSelected(todo.isCompleted());

                try {
                    if (m_repository.getTodosByDay(null).length != 0) {
                        todoCheckBox
                                .setLayoutY(todos.getChildren()
                                        .get(m_repository.getTodosByDay(null).length - 1)
                                        .getLayoutY() + 25);

                    } else {
                        todos.getChildren().clear();
                        todoCheckBox.setLayoutY(20);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                m_repository.addTodo(todo);
                todos.getChildren().add(todoCheckBox);

                /////////////// Изменение галочки в чекбоксе для новой тудушки ///////////////
                todoCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        try {
                            Todo[] tempTodosArray = m_repository.getTodosByDay(null);
                            tempTodosArray[tempTodosArray.length - 1].done();
                            m_repository.updateTodo(tempTodosArray[tempTodosArray.length - 1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Todo[] tempTodosArray = m_repository.getTodosByDay(null);
                            tempTodosArray[tempTodosArray.length - 1].undone();
                            m_repository.updateTodo(tempTodosArray[tempTodosArray.length - 1]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        newTodoButton.setLayoutY(290);
        return newTodoButton;
    }

    private Group reportingPage() throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Todo[] allTodos = m_repository.getAllTodos();

        int totalTaskCount = allTodos.length;

        int totalCompletedTaskCount = 0;

        int totalDays = 0;
        List<List<Todo>> dailyTasks = new ArrayList<>();

        for (int i = 0; i < totalTaskCount; i++) {
            if (allTodos[i].isCompleted()) {
                totalCompletedTaskCount++;
            }

            if (i == 0) {
                List<Todo> todos = new ArrayList<>();
                todos.add(allTodos[i]);
                dailyTasks.add(todos);
                continue;
            }

            if (!dateFormat.format(allTodos[i].getCreatedAt())
                    .equals(dateFormat.format(allTodos[i - 1].getCreatedAt()))) {
                totalDays++;
                List<Todo> todos = new ArrayList<>();
                todos.add(allTodos[i]);
                dailyTasks.add(todos);
                continue;
            }

            dailyTasks.get(totalDays).add(allTodos[i]);
        }

        totalDays++;

        int minTaskCount = 100000;
        int maxTaskCount = 0;

        int averageTasksCount = 0;
        int averageCompletedTasksCount = 0;

        for (List<Todo> todosPerDay :
                dailyTasks) {
            if (todosPerDay.size() < minTaskCount) {
                minTaskCount = todosPerDay.size();
            }

            if (todosPerDay.size() > maxTaskCount) {
                maxTaskCount = todosPerDay.size();
            }
        }

        if(minTaskCount == 100000)
        {
            minTaskCount = 0;
        }

        if (totalDays != 0) {
            averageTasksCount = totalTaskCount / totalDays;
            averageCompletedTasksCount = totalCompletedTaskCount / totalDays;
        }

        Group content = new Group();

        VBox container = new VBox(10);
        container.setId("reporting");
        Label totalDaysLabel = new Label("Total days: "
                + totalDays);
        Label totalTasksLabel = new Label("Total tasks: "
                + totalTaskCount);
        Label totalCompletedTasksLabel = new Label("Total completed tasks: "
                + totalCompletedTaskCount);
        Label minTasksInDayLabel = new Label("Min tasks in a day: "
                + minTaskCount);
        Label maxTasksInDayLabel = new Label("Max tasks in a day: "
                + maxTaskCount);
        Label averageTasksLabel = new Label("Average tasks in a day: "
                + averageTasksCount);
        Label averageCompletedTasksLabel = new Label("Average completed tasks in a day: "
                + averageCompletedTasksCount);

        container.getChildren().addAll(totalDaysLabel,
                totalTasksLabel,
                totalCompletedTasksLabel,
                minTasksInDayLabel,
                maxTasksInDayLabel,
                averageTasksLabel,
                averageCompletedTasksLabel);
        content.getChildren().add(container);

        return content;
    }

}
