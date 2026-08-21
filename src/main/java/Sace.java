import java.util.Scanner;

/**
 * Runs Sace, a personal assistant chatbot.
 */
public class Sace {
    /**
     * Greets the user, stores tasks, lists tasks, and exits when they enter {@code bye}.
     *
     * @param args command-line arguments; not used in Level 2
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
        String[] tasks = new String[100];
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
            } else {
                tasks[taskCount] = command;
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
