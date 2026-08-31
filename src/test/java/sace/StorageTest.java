package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests saving tasks to disk and loading them back into the application.
 */
class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskData() throws SaceException {
        Path dataFile = tempDir.resolve("data").resolve("sace.txt");
        Storage storage = new Storage(dataFile);

        Todo todo = new Todo("read | chapter \\ notes\nreview");
        todo.markAsDone();
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2026, 9, 15));
        Event event = new Event(
                "project meeting", "Aug 31 at 2pm", "Aug 31 at 4pm");

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read | chapter \\ notes\nreview", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("Aug 31 at 2pm", loadedEvent.getFrom());
        assertEquals("Aug 31 at 4pm", loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void load_missingDataFile_returnsEmptyTaskList() throws SaceException {
        Storage storage = new Storage(tempDir.resolve("missing").resolve("sace.txt"));

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_corruptedTaskData_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Files.writeString(
                dataFile, "T | invalid-status | read book", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        SaceException exception = assertThrows(SaceException.class, storage::load);

        assertEquals("The data file is corrupted at line 1.", exception.getMessage());
    }
}
