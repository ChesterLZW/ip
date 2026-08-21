/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns {@code X} for a completed task and a blank for an incomplete task.
     *
     * @return status icon for this task
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task in the format {@code [status] description}.
     *
     * @return displayable representation of this task
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
