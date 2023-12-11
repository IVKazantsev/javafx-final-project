import java.util.Date;

public class Todo {
    private int m_id;
    private String m_title;

    private boolean m_completed;

    private final Date m_createdAt;

    private Date m_updatedAt;

    private Date m_completedAt;

    public Todo(String title, boolean completed) throws Exception {
        this.setTitle(title);

        Date now = new Date();

        m_createdAt = now;
        m_updatedAt = now;

        m_completed = completed;

        m_completedAt = (completed) ? now : null;
    }

    public Todo(String title, boolean completed, int id) throws Exception {
        this.setTitle(title);

        m_id = id;

        Date now = new Date();

        m_createdAt = now;
        m_updatedAt = now;

        m_completed = completed;

        m_completedAt = (completed) ? now : null;
    }

    public void done() {
        Date now = new Date();

        m_completed = true;
        m_completedAt = now;
        m_updatedAt = now;
    }

    public void undone() {
        m_completed = false;

        m_completedAt = null;
        m_updatedAt = new Date();
    }

    public boolean isCompleted() {
        return m_completed;
    }

    public int getId() {
        return m_id;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) throws Exception {
        title = title.trim();
        if (title.isEmpty()) {
            throw new Exception("Title cannot be empty");
        }

        m_title = title;
    }

    public Date getCreatedAt() {
        return m_createdAt;
    }

    public Date getUpdatedAt() {
        return m_updatedAt;
    }

    public Date getCompletedAt() {
        return m_completedAt;
    }
}