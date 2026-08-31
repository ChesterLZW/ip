package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list operations that select tasks without changing the stored list.
 */
class TaskListTest {
    @Test
    void find_keywordWithDifferentCase_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new Todo("Read Book");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 9, 30));
        Task nonMatch = new Event("project meeting", "Monday", "Tuesday");
        TaskList tasks = new TaskList(List.of(firstMatch, secondMatch, nonMatch));

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void find_keywordOnlyOutsideDescription_returnsEmptyList() {
        Task event = new Event("project meeting", "bookstore", "library");
        TaskList tasks = new TaskList(List.of(event));

        List<Task> matchingTasks = tasks.find("book");

        assertTrue(matchingTasks.isEmpty());
    }
}
