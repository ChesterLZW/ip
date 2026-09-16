package sace;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates the components of Sace, a personal assistant chatbot.
 */
public class Sace {
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "sace.txt");
    private static final String WELCOME_MESSAGE = "Hello! I'm Sace.\n"
            + "Your moonlit task oracle is ready. What can I do for you?";
    private static final String GOODBYE_MESSAGE = "Until next time. May your path be flawless!";

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private String loadingWarning;
    private boolean isExitRequested;

    /**
     * Creates Sace using its default data file.
     */
    public Sace() {
        this(DEFAULT_DATA_PATH);
    }

    /**
     * Creates Sace using the given file for task persistence.
     *
     * @param filePath path of the task data file.
     */
    public Sace(Path filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = new TaskList();
        loadingWarning = "";
        loadTasks();
    }

    /**
     * Loads saved tasks and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome(getWelcomeMessage());

        while (!isExitRequested && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();
            ui.showResponse(getResponse(command));
        }

        if (!isExitRequested) {
            ui.showResponse(GOODBYE_MESSAGE);
        }
        ui.close();
    }

    /**
     * Returns the welcome text shown when a user opens Sace.
     *
     * @return greeting and any storage warning raised during startup.
     */
    public String getWelcomeMessage() {
        if (loadingWarning.isEmpty()) {
            return WELCOME_MESSAGE;
        }
        return WELCOME_MESSAGE + "\n\n" + loadingWarning;
    }

    /**
     * Processes one command and returns the reply for a console or graphical interface.
     *
     * @param command command entered by the user.
     * @return Sace's reply to the command.
     */
    public String getResponse(String command) {
        isExitRequested = false;
        String normalizedCommand = command == null ? "" : command.trim();
        try {
            return executeCommand(normalizedCommand);
        } catch (SaceException e) {
            return "OOPS!!! " + e.getMessage();
        }
    }

    /**
     * Returns whether the most recently processed command was {@code bye}.
     *
     * @return {@code true} when the current session should end.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Starts Sace with its default data file.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Sace().run();
    }

    /**
     * Loads tasks from storage, falling back to an empty task list if loading fails.
     */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (SaceException e) {
            tasks = new TaskList();
            loadingWarning = "OOPS!!! " + e.getMessage()
                    + "\nI'll start with an empty task list instead.";
        }
    }

    /**
     * Parses and carries out one user command.
     *
     * @param command command entered by the user.
     * @return reply to display to the user.
     * @throws SaceException if the command is invalid or tasks cannot be saved.
     */
    private String executeCommand(String command) throws SaceException {
        Parser.CommandType commandType = Parser.parseCommandType(command);

        switch (commandType) {
            case BYE:
                isExitRequested = true;
                return GOODBYE_MESSAGE;
            case LIST:
                return formatTasks("Here are the tasks in your quest log:", tasks.asList());
            case FIND:
                return findTasks(command);
            case MARK:
                return markTask(command);
            case UNMARK:
                return unmarkTask(command);
            case DELETE:
                return deleteTask(command);
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                return addTask(Parser.parseTask(command, commandType));
            default:
                throw new SaceException("I'm sorry, but I don't know what that means.");
        }
    }

    /**
     * Finds tasks whose descriptions match the requested keyword.
     */
    private String findTasks(String command) throws SaceException {
        String keyword = Parser.parseFindKeyword(command);
        return formatTasks("Here are the matching tasks in your quest log:",
                tasks.find(keyword));
    }

    /**
     * Marks the task selected by a mark command and saves the updated list.
     */
    private String markTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.mark(taskIndex);
        saveTasks();
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Unmarks the task selected by an unmark command and saves the updated list.
     */
    private String unmarkTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "unmark", tasks.size());
        Task task = tasks.unmark(taskIndex);
        saveTasks();
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Deletes the task selected by a delete command and saves the updated list.
     */
    private String deleteTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        saveTasks();
        return "Noted. I've removed this task:\n  " + removedTask + "\n"
                + formatTaskCount(tasks.size());
    }

    /**
     * Adds a parsed task, saves the updated list, and displays confirmation.
     */
    private String addTask(Task task) throws SaceException {
        tasks.add(task);
        saveTasks();
        return "Got it. I've added this task:\n  " + task + "\n"
                + formatTaskCount(tasks.size());
    }

    /**
     * Saves the current task list.
     */
    private void saveTasks() throws SaceException {
        storage.save(tasks.asList());
    }

    /**
     * Formats tasks as a one-based list that is suitable for every user interface.
     */
    private static String formatTasks(String heading, List<Task> taskList) {
        StringBuilder response = new StringBuilder(heading);
        if (taskList.isEmpty()) {
            return response.append("\n  No tasks found.").toString();
        }

        for (int i = 0; i < taskList.size(); i++) {
            response.append('\n')
                    .append(i + 1)
                    .append(". ")
                    .append(taskList.get(i));
        }
        return response.toString();
    }

    /**
     * Formats the number of tasks remaining in the list.
     */
    private static String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }
}
