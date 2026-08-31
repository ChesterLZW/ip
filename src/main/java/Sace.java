import java.nio.file.Path;

/**
 * Coordinates the components of Sace, a personal assistant chatbot.
 */
public class Sace {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates Sace using the given file for task persistence.
     *
     * @param filePath path of the task data file
     */
    public Sace(Path filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = new TaskList();
    }

    /**
     * Loads saved tasks and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();
        loadTasks();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            try {
                isExit = executeCommand(command);
            } catch (SaceException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    /**
     * Starts Sace with its default data file.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        new Sace(Path.of("data", "sace.txt")).run();
    }

    /**
     * Loads tasks from storage, falling back to an empty task list if loading fails.
     */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (SaceException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Parses and carries out one user command.
     *
     * @param command command entered by the user
     * @return {@code true} if the command asks Sace to exit
     * @throws SaceException if the command is invalid or tasks cannot be saved
     */
    private boolean executeCommand(String command) throws SaceException {
        Parser.CommandType commandType = Parser.parseCommandType(command);

        switch (commandType) {
        case BYE:
            return true;
        case LIST:
            ui.showTaskList(tasks);
            break;
        case MARK:
            markTask(command);
            break;
        case UNMARK:
            unmarkTask(command);
            break;
        case DELETE:
            deleteTask(command);
            break;
        case TODO:
        case DEADLINE:
        case EVENT:
            addTask(Parser.parseTask(command, commandType));
            break;
        default:
            throw new SaceException("I'm sorry, but I don't know what that means.");
        }
        return false;
    }

    /**
     * Marks the task selected by a mark command and saves the updated list.
     */
    private void markTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "mark", tasks.size());
        Task task = tasks.mark(taskIndex);
        saveTasks();
        ui.showMarkedTask(task);
    }

    /**
     * Unmarks the task selected by an unmark command and saves the updated list.
     */
    private void unmarkTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "unmark", tasks.size());
        Task task = tasks.unmark(taskIndex);
        saveTasks();
        ui.showUnmarkedTask(task);
    }

    /**
     * Deletes the task selected by a delete command and saves the updated list.
     */
    private void deleteTask(String command) throws SaceException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        saveTasks();
        ui.showDeletedTask(removedTask, tasks.size());
    }

    /**
     * Adds a parsed task, saves the updated list, and displays confirmation.
     */
    private void addTask(Task task) throws SaceException {
        tasks.add(task);
        saveTasks();
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Saves the current task list.
     */
    private void saveTasks() throws SaceException {
        storage.save(tasks.asList());
    }
}
