package sace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the interface-independent command responses used by the GUI.
 */
class SaceTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_addThenList_returnsSavedTask() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String addResponse = sace.getResponse("todo prepare slides");
        String listResponse = sace.getResponse("list");

        assertTrue(addResponse.contains("prepare slides"));
        assertTrue(listResponse.contains("1. [T][ ] prepare slides"));
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("dance");

        assertTrue(response.startsWith("The battle plan needs correction:"));
        assertFalse(sace.isExitRequested());
    }

    @Test
    void getResponse_bye_marksSessionForExit() {
        Sace sace = new Sace(tempDir.resolve("sace.txt"));

        String response = sace.getResponse("bye");

        assertTrue(response.contains("The battle rests for now"));
        assertTrue(sace.isExitRequested());
    }
}
