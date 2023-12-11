import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Repository {
    public Connection dbConnection;

    public Connection getConnection() {
        File configFile = new File("./config/config.properties");

        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            String databaseName = props.getProperty("db_name");
            String databaseUser = props.getProperty("db_user");
            String databasePassword = props.getProperty("db_password");

            reader.close();

            String url = "jdbc:mysql://localhost/" + databaseName;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                dbConnection = DriverManager.getConnection(url, databaseUser, databasePassword);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return dbConnection;
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

        return null;
    }

    public Todo[] getTodos(Date time) throws IOException {
        Connection connection = getConnection();

        if (time == null) {
            time = new Date();
        }

        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        String from = fromFormat.format(time);
        String to = toFormat.format(time);

        String query = "SELECT ID, TITLE, COMPLETED FROM todos WHERE CREATED_AT BETWEEN ? AND ?";


        List<Todo> todos = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, from);
            statement.setString(2, to);

            ResultSet queryOutput = statement.executeQuery();

            while (queryOutput.next()) {
                todos.add(new Todo(queryOutput.getString("TITLE"), queryOutput.getBoolean("COMPLETED"), queryOutput.getInt("ID")));
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return todos.toArray(new Todo[0]);
    }

    public void addTodo(Todo todo) {
        Connection connection = getConnection();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String title = todo.getTitle();
        String completed = (todo.isCompleted()) ? "Y" : "N";
        String createdAt = dateFormat.format(todo.getCreatedAt());
        String updatedAt = dateFormat.format(todo.getUpdatedAt());
        String completedAt = (Objects.isNull(todo.getCompletedAt())) ? null : dateFormat.format(todo.getCompletedAt());

        String query = "INSERT INTO todos (TITLE, COMPLETED, CREATED_AT, UPDATED_AT, COMPLETED_AT)" +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, completed);
            statement.setString(3, createdAt);
            statement.setString(4, updatedAt);
            if ((Objects.isNull(completedAt))) {
                statement.setNull(5, Types.DATE);
            } else {
                statement.setString(5, completedAt);
            }

            statement.execute();

            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateTodo(Todo todo) {
        Connection connection = getConnection();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String completed = (todo.isCompleted()) ? "Y" : "N";
        String updatedAt = dateFormat.format(todo.getUpdatedAt());
        String completedAt = (Objects.isNull(todo.getCompletedAt())) ? null : dateFormat.format(todo.getCompletedAt());

        String query = "UPDATE todos SET COMPLETED = ?, UPDATED_AT = ?, COMPLETED_AT = ?" +
                "WHERE ID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, completed);
            statement.setString(2, updatedAt);
            if ((Objects.isNull(completedAt))) {
                statement.setNull(3, Types.DATE);
            } else {
                statement.setString(3, completedAt);
            }
            statement.setInt(4, todo.getId());
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
