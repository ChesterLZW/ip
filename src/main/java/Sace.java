import java.util.Scanner;

/**
 * Runs Sace, a personal assistant chatbot.
 */
public class Sace {
    /**
     * Greets the user, manages different task types, and exits when they enter {@code bye}.
     *
     * @param args command-line arguments; not used in Level 4
     */
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        String banner = "  SSS    A    CCCC  EEEEE\n"
                + " S      A A   C     E\n"
                + "  SSS  AAAAA  C     EEE\n"
                + "     S A   A  C     E\n"
                + " SSSS  A   A  CCCC  EEEEE\n";

        System.out.println(horizontalLine);
        System.out.print(banner);
        System.out.println("Hello! I'm Sace.");
        System.out.println("What can I do for you?");
        System.out.println(horizontalLine);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;
        boolean isExit = false;

        while (!isExit && scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(horizontalLine);

            if (command.equals("bye")) {
                isExit = true;
            } else if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
                System.out.println(horizontalLine);
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring("mark ".length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks[taskIndex]);
                System.out.println(horizontalLine);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring("unmark ".length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(tasks[taskIndex]);
                System.out.println(horizontalLine);
            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo ".length());
                tasks[taskCount] = new Todo(description);
                taskCount++;
                showAddedTask(tasks[taskCount - 1], taskCount, horizontalLine);
            } else if (command.startsWith("deadline ")) {
                int byMarkerIndex = command.indexOf(" /by ");
                String description = command.substring("deadline ".length(), byMarkerIndex);
                String by = command.substring(byMarkerIndex + " /by ".length());
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                showAddedTask(tasks[taskCount - 1], taskCount, horizontalLine);
            } else if (command.startsWith("event ")) {
                int fromMarkerIndex = command.indexOf(" /from ");
                int toMarkerIndex = command.indexOf(" /to ");
                String description = command.substring("event ".length(), fromMarkerIndex);
                String from = command.substring(fromMarkerIndex + " /from ".length(), toMarkerIndex);
                String to = command.substring(toMarkerIndex + " /to ".length());
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                showAddedTask(tasks[taskCount - 1], taskCount, horizontalLine);
            } else {
                System.out.println("I don't know what that means.");
                System.out.println(horizontalLine);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(horizontalLine);
        scanner.close();
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
