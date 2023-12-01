import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.awt.*;

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

        Label label = new Label("Task name:");
        TextField textField = new TextField();
        textField.setPromptText("What to do?");

        System.out.println(textField.promptTextProperty());

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Add a task");
        Label emptyLabel = new Label();
        emptyLabel.setTextFill(Color.rgb(255,0,0));

        cancelButton.setOnAction(e -> {
            isAdded = false;
            window.close();
        });
        saveButton.setOnAction(e -> {
            if(!textField.getText().trim().isEmpty()) {
                isAdded = true;
                todo = new Todo(textField.getText());
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