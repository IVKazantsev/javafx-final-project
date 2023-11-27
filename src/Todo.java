import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

public class Todo {
    private String m_title;

    private boolean m_completed = false;

    private Date m_createdAt;

    public Todo(String title) {
        m_title = title;
        m_createdAt = new Date();
    }

    public Todo(String title, boolean completed) {
        m_title = title;
        m_createdAt = new Date();
        m_completed = completed;
    }

    public void done() {
        m_completed = true;
    }

    public void undone() {
        m_completed = false;
    }

    public boolean isCompleted() {
        return m_completed;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public Date getCreatedAt() {
        return m_createdAt;
    }

    public String saveTodo() {
        return m_title + "::" + m_completed;
    }

    public static Todo loadTodo(String todo) {
        String[] parts = todo.split("::");

        boolean completed = parts[parts.length - 1].equals("true");

        String[] title = Arrays.copyOfRange(parts, 0, (parts.length - 1));
        return new Todo(String.join("", title), completed);
    }
}