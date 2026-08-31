package sace;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets user commands and converts their arguments into task data.
 */
public class Parser {
    /**
     * Identifies the supported kinds of user commands.
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        DEADLINE,
        EVENT
    }

    /**
     * Identifies which supported command the user entered.
     *
     * @param command full command entered by the user.
     * @return type of the command.
     * @throws SaceException if the command is blank or unknown.
     */
    public static CommandType parseCommandType(String command) throws SaceException {
        if (command.isEmpty()) {
            throw new SaceException("Please enter a command.");
        } else if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        }
        throw new SaceException("I'm sorry, but I don't know what that means.");
    }

    /**
     * Converts the number in a task operation command to a valid list index.
     *
     * @param command full command entered by the user.
     * @param action command word, such as {@code mark}, {@code unmark}, or {@code delete}.
     * @param taskCount current number of stored tasks.
     * @return zero-based index of the selected task.
     * @throws SaceException if the number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, String action, int taskCount)
            throws SaceException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new SaceException("Please provide a task number after " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new SaceException("The task number for " + action + " must be a whole number.");
        }

        if (taskCount == 0) {
            throw new SaceException("There are no tasks to " + action + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new SaceException("Choose a task number between 1 and " + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Creates a task from an add command.
     *
     * @param command full command entered by the user.
     * @param commandType type of task creation command.
     * @return task described by the command.
     * @throws SaceException if required task information is missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType)
            throws SaceException {
        switch (commandType) {
            case TODO:
                return parseTodo(command);
            case DEADLINE:
                return parseDeadline(command);
            case EVENT:
                return parseEvent(command);
            default:
                throw new SaceException("This command does not create a task.");
        }
    }

    /**
     * Creates a todo from a validated command.
     */
    private static Todo parseTodo(String command) throws SaceException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new SaceException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a validated command.
     */
    private static Deadline parseDeadline(String command) throws SaceException {
        int byMarkerIndex = command.indexOf(" /by ");
        if (byMarkerIndex < 0) {
            throw new SaceException(
                    "Use this format: deadline DESCRIPTION /by DATE_OR_TIME.");
        }

        String description = command.substring("deadline".length(), byMarkerIndex).trim();
        String by = command.substring(byMarkerIndex + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new SaceException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new SaceException("The due date of a deadline cannot be empty.");
        }

        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException e) {
            throw new SaceException(
                    "Use a real date in yyyy-MM-dd format, for example 2026-08-31.");
        }
    }

    /**
     * Creates an event from a validated command.
     */
    private static Event parseEvent(String command) throws SaceException {
        int fromMarkerIndex = command.indexOf(" /from ");
        int toMarkerIndex = command.indexOf(" /to ");
        if (fromMarkerIndex < 0 || toMarkerIndex < 0 || toMarkerIndex <= fromMarkerIndex) {
            throw new SaceException(
                    "Use this format: event DESCRIPTION /from START /to END.");
        }

        String description = command.substring("event".length(), fromMarkerIndex).trim();
        String from = command.substring(
                fromMarkerIndex + " /from ".length(), toMarkerIndex).trim();
        String to = command.substring(toMarkerIndex + " /to ".length()).trim();
        if (description.isEmpty()) {
            throw new SaceException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new SaceException("The start of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new SaceException("The end of an event cannot be empty.");
        }
        return new Event(description, from, to);
    }
}
