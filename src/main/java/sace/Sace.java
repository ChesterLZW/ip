package sace;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates the components of Sace, a personal assistant chatbot.
 */
public class Sace {
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "sace.txt");
    private static final String WELCOME_MESSAGE = "Welcome, Summoner. I'm Sace, your moonlit strategist.\n"
            + "The quest log awaits your command.";
    private static final String GOODBYE_MESSAGE = "The battle rests for now, Summoner.\n"
            + "May honor and victory follow your path.";
    private static final String ERROR_PREFIX = "The battle plan needs correction:\n";

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
            return ERROR_PREFIX + e.getMessage();
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
            loadingWarning = "The royal archive is in disarray.\n" + e.getMessage()
                    + "\nA fresh quest log has been prepared.";
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
                return formatTasks("Your current battle plan:", tasks.asList());
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
                throw new SaceException("That order is not recorded in the battle manual.");
        }
    }

    /**
     * Finds tasks whose descriptions match the requested keyword.
     */
    private String findTasks(String command) throws SaceException {
        String keyword = Parser.parseFindKeyword(command);
        return formatTasks("The scouts found these matching quests:",
                tasks.find(keyword));
    }

    /**
     * Marks the task selected by a mark command and saves the updated list.
     */
    private String markTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.mark(taskIndex);
        saveTasks();
        return "Victory secured. This quest is complete:\n  " + task;
    }

    /**
     * Unmarks the task selected by an unmark command and saves the updated list.
     */
    private String unmarkTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "unmark", tasks.size());
        Task task = tasks.unmark(taskIndex);
        saveTasks();
        return "This quest returns to the battle plan:\n  " + task;
    }

    /**
     * Deletes the task selected by a delete command and saves the updated list.
     */
    private String deleteTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        saveTasks();
        return "Order withdrawn. This quest has been removed:\n  " + removedTask + "\n"
                + formatTaskCount(tasks.size());
    }

    /**
     * Adds a parsed task, saves the updated list, and displays confirmation.
     */
    private String addTask(Task task) throws SaceException {
        tasks.add(task);
        saveTasks();
        return "Order received. This quest joins your battle plan:\n  " + task + "\n"
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
            return response.append("\n  No quests await you here.").toString();
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
        String questWord = taskCount == 1 ? "quest" : "quests";
        return "Your battle plan now holds " + taskCount + " " + questWord + ".";
    }
}
