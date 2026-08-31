package sace;

import java.util.Scanner;

/**
 * Handles console input and displays Sace's responses to the user.
 */
public class Ui {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";
    private static final String BANNER = "  ____      _      ____   _____\n"
            + " / ___|    / \\    / ___| | ____|\n"
            + " \\___ \\   / _ \\  | |     |  _|\n"
            + "  ___) | / ___ \\ | |___  | |___\n"
            + " |____/ /_/   \\_\\ \\____| |_____|\n";

    private final Scanner scanner;

    /**
     * Creates a console user interface reading from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Sace's banner and greeting.
     */
    public void showWelcome() {
        System.out.println(HORIZONTAL_LINE);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Sace.");
        System.out.println("What can I do for you?");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a line separating the user's command from Sace's response.
     */
    public void showDivider() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays a storage error and explains that Sace will use an empty list.
     *
     * @param message explanation of the loading error
     */
    public void showLoadingError(String message) {
        System.out.println("OOPS!!! " + message);
        System.out.println("I'll start with an empty task list instead.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays every task with a one-based task number.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was marked.
     *
     * @param task marked task
     */
    public void showMarkedTask(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays confirmation that a task was unmarked.
     *
     * @param task unmarked task
     */
    public void showUnmarkedTask(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays the removed task and the number of remaining tasks.
     *
     * @param task removed task
     * @param taskCount number of remaining tasks
     */
    public void showDeletedTask(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays the added task and the number of stored tasks.
     *
     * @param task added task
     * @param taskCount number of stored tasks
     */
    public void showAddedTask(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays a user-friendly error response.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays Sace's goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Releases the console input scanner.
     */
    public void close() {
        scanner.close();
    }
}
