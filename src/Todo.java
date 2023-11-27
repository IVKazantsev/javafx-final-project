import java.util.Date;

public class Todo {
    private String m_title;

    private boolean m_completed = false;

    private Date m_createdAt;

    public Todo(String title) {
        m_title = title;
        m_createdAt = new Date();
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
}