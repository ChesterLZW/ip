/**
 * Represents a task that needs to be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline with the given description and due information.
     *
     * @param description description of the deadline
     * @param by date or time by which the deadline should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due information of this deadline.
     *
     * @return due date or time
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns this deadline with its type, completion status, and due information.
     *
     * @return displayable representation of this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
