import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddTodoBox {
    static Todo todo;

    static boolean isAdded = false;

    public static boolean display(String title) {
        isAdded = false;
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(100);
        Image icon = new Image("file:img/icon.png", 100, 100, true, true);
        window.getIcons().add(icon);
        Label label = new Label("Task name:");
        label.setId("taskName");
        label.getStylesheets().add("style.css");
        TextField textField = new TextField();
        textField.setPromptText("What to do?");
        textField.getStylesheets().add("style.css");

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Add a task");
        cancelButton.getStylesheets().add("style.css");
        saveButton.getStylesheets().add("style.css");
        Label emptyLabel = new Label();
        emptyLabel.setTextFill(Color.rgb(255, 0, 0));

        cancelButton.setOnAction(e -> {
            isAdded = false;
            window.close();
        });
        saveButton.setOnAction(e -> {
            if (!textField.getText().trim().isEmpty()) {
                isAdded = true;
                try {
                    todo = new Todo(textField.getText(), false);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                window.close();
            } else {
                emptyLabel.setText("Empty task");
            }
        });

        HBox input = new HBox(10);
        input.getChildren().addAll(label, textField);
        input.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(cancelButton, saveButton);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(input, buttons, emptyLabel);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return isAdded;
    }
}
