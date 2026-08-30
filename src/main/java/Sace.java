import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs Sace, a personal assistant chatbot.
 */
public class Sace {
    /**
     * Greets the user, manages and deletes tasks, and exits on {@code bye}.
     *
     * @param args command-line arguments; not used in Level 6
     */
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        String banner = "  ____      _      ____   _____\n"
                + " / ___|    / \\    / ___| | ____|\n"
                + " \\___ \\   / _ \\  | |     |  _|\n"
                + "  ___) | / ___ \\ | |___  | |___\n"
                + " |____/ /_/   \\_\\ \\____| |_____|\n";

        System.out.println(horizontalLine);
        System.out.print(banner);
        System.out.println("Hello! I'm Sace.");
        System.out.println("What can I do for you?");
        System.out.println(horizontalLine);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
        boolean isExit = false;

        while (!isExit && scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(horizontalLine);

            try {
                if (command.isEmpty()) {
                    throw new SaceException("Please enter a command.");
                } else if (command.equals("bye")) {
                    isExit = true;
                } else if (command.equals("list")) {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + ". " + tasks.get(i));
                    }
                    System.out.println(horizontalLine);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(tasks.get(taskIndex));
                    System.out.println(horizontalLine);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println(tasks.get(taskIndex));
                    System.out.println(horizontalLine);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    String taskWord = tasks.size() == 1 ? "task" : "tasks";
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println(
                            "Now you have " + tasks.size() + " " + taskWord + " in the list.");
                    System.out.println(horizontalLine);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    addTask(tasks, parseTodo(command), horizontalLine);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    addTask(tasks, parseDeadline(command), horizontalLine);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    addTask(tasks, parseEvent(command), horizontalLine);
                } else {
                    throw new SaceException("I'm sorry, but I don't know what that means.");
                }
            } catch (SaceException e) {
                System.out.println("OOPS!!! " + e.getMessage());
                System.out.println(horizontalLine);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(horizontalLine);
        scanner.close();
    }

    /**
     * Converts the number in a task operation command to a valid list index.
     *
     * @param command full command entered by the user
     * @param action command word, such as {@code mark}, {@code unmark}, or {@code delete}
     * @param taskCount current number of stored tasks
     * @return zero-based index of the selected task
     * @throws SaceException if the number is missing, invalid, or out of range
     */
    private static int parseTaskIndex(String command, String action, int taskCount)
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
     * Creates a todo from a validated command.
     *
     * @param command full todo command
     * @return parsed todo
     * @throws SaceException if the description is empty
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
     *
     * @param command full deadline command
     * @return parsed deadline
     * @throws SaceException if required information is missing
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
            throw new SaceException("The due date or time of a deadline cannot be empty.");
        }
        return new Deadline(description, by);
    }

    /**
     * Creates an event from a validated command.
     *
     * @param command full event command
     * @return parsed event
     * @throws SaceException if required information is missing or out of order
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

    /**
     * Stores a task and displays confirmation.
     *
     * @param tasks list used to store tasks
     * @param task task to add
     * @param horizontalLine line used to separate chatbot responses
     */
    private static void addTask(
            ArrayList<Task> tasks, Task task, String horizontalLine) {
        tasks.add(task);
        showAddedTask(task, tasks.size(), horizontalLine);
    }

    /**
     * Displays confirmation that a task was added and reports the current task count.
     *
     * @param task task that was added
     * @param taskCount current number of stored tasks
     * @param horizontalLine line used to separate chatbot responses
     */
    private static void showAddedTask(Task task, int taskCount, String horizontalLine) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
        System.out.println(horizontalLine);
    }
}
