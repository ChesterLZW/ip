import java.util.Scanner;

/**
 * Runs Sace, a personal assistant chatbot.
 */
public class Sace {
    /**
     * Greets the user, manages task completion, and exits when they enter {@code bye}.
     *
     * @param args command-line arguments; not used in Level 3
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
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
                System.out.println(horizontalLine);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(horizontalLine);
        scanner.close();
    }
}
