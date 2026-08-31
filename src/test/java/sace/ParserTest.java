package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition and extraction of command arguments.
 */
class ParserTest {
    @Test
    void parseCommandType_findCommand_returnsFindCommandType() throws SaceException {
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    void parseFindKeyword_validCommand_returnsTrimmedKeyword() throws SaceException {
        assertEquals("project meeting", Parser.parseFindKeyword("find   project meeting  "));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsException() {
        SaceException exception = assertThrows(
                SaceException.class,
                () -> Parser.parseFindKeyword("find"));

        assertEquals("The search keyword cannot be empty.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndexes() throws SaceException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(1, Parser.parseTaskIndex("unmark   2", "unmark", 3));
        assertEquals(2, Parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_throwsException() {
        SaceException exception = assertThrows(
                SaceException.class,
                () -> Parser.parseTaskIndex("mark", "mark", 3));

        assertEquals("Please provide a task number after mark.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericTaskNumber_throwsException() {
        SaceException exception = assertThrows(
                SaceException.class,
                () -> Parser.parseTaskIndex("delete two", "delete", 3));

        assertEquals(
                "The task number for delete must be a whole number.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_emptyTaskList_throwsException() {
        SaceException exception = assertThrows(
                SaceException.class,
                () -> Parser.parseTaskIndex("unmark 1", "unmark", 0));

        assertEquals("There are no tasks to unmark.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_outOfRangeTaskNumbers_throwsException() {
        SaceException belowRangeException = assertThrows(
                SaceException.class,
                () -> Parser.parseTaskIndex("mark 0", "mark", 3));
        SaceException aboveRangeException = assertThrows(
                SaceException.class,
                () -> Parser.parseTaskIndex("mark 4", "mark", 3));

        assertEquals(
                "Choose a task number between 1 and 3.",
                belowRangeException.getMessage());
        assertEquals(
                "Choose a task number between 1 and 3.",
                aboveRangeException.getMessage());
    }
}
