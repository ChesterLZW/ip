/**
 * Runs Sace, a personal assistant chatbot.
 */
public class Sace {
    /**
     * Displays the Level 0 greeting and then exits.
     *
     * @param args command-line arguments; not used in Level 0
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
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(horizontalLine);
    }
}
