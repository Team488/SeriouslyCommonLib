package xbot.common.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link BaseRobot} NetworkTables persistent-file recovery logic.
 *
 * <p>These tests exercise {@link BaseRobot#isNtFileUsable(Path)} and
 * {@link BaseRobot#recoverNetworkTablesFileIfNeeded(Path, Path)} with temporary files so
 * that no real roboRIO paths are required.
 */
public class BaseRobotNTRecoveryTest {

    private Path tempDir;
    private Path mainFile;
    private Path backupFile;

    /** Minimal NT4 JSON content that represents a single persisted preference. */
    private static final String VALID_NT_JSON =
            "[{\"name\":\"/Preferences/myKey\",\"type\":\"double\","
            + "\"value\":1.0,\"properties\":{\"persistent\":true}}]\n";
    private static final byte[] VALID_NT_CONTENT = VALID_NT_JSON.getBytes(StandardCharsets.UTF_8);

    /** A second distinct valid NT4 JSON entry used in tests that need two different valid files. */
    private static final String OTHER_VALID_NT_JSON =
            "[{\"name\":\"/Preferences/other\",\"type\":\"double\","
            + "\"value\":2.0,\"properties\":{\"persistent\":true}}]\n";

    /** The empty-array value written by the NT server when it cannot load the primary file. */
    private static final byte[] EMPTY_NT_CONTENT = "[]\n".getBytes(StandardCharsets.UTF_8);

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("nt_recovery_test_");
        mainFile = tempDir.resolve("networktables.json");
        backupFile = tempDir.resolve("networktables.json.bck");
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(mainFile);
        Files.deleteIfExists(backupFile);
        Files.deleteIfExists(tempDir);
    }

    // -------------------------------------------------------------------------
    // isNtFileUsable tests
    // -------------------------------------------------------------------------

    @Test
    public void testIsNtFileUsable_missingFile_returnsFalse() {
        assertFalse(BaseRobot.isNtFileUsable(mainFile));
    }

    @Test
    public void testIsNtFileUsable_emptyFile_returnsFalse() throws IOException {
        Files.write(mainFile, new byte[0]);
        assertFalse(BaseRobot.isNtFileUsable(mainFile));
    }

    @Test
    public void testIsNtFileUsable_emptyArrayContent_returnsFalse() throws IOException {
        Files.write(mainFile, EMPTY_NT_CONTENT);
        assertFalse(BaseRobot.isNtFileUsable(mainFile));
    }

    @Test
    public void testIsNtFileUsable_validContent_returnsTrue() throws IOException {
        Files.write(mainFile, VALID_NT_CONTENT);
        assertTrue(BaseRobot.isNtFileUsable(mainFile));
    }

    @Test
    public void testIsNtFileUsable_contentNotStartingWithBracket_returnsFalse() throws IOException {
        // Simulate a garbled / non-JSON file
        Files.write(mainFile, "garbage data that is longer than four bytes".getBytes(StandardCharsets.UTF_8));
        assertFalse(BaseRobot.isNtFileUsable(mainFile));
    }

    @Test
    public void testIsNtFileUsable_leadingWhitespace_returnsTrue() throws IOException {
        // NT4 format allows leading whitespace before '['
        byte[] content = ("  \n" + new String(VALID_NT_CONTENT, StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        Files.write(mainFile, content);
        assertTrue(BaseRobot.isNtFileUsable(mainFile));
    }

    // -------------------------------------------------------------------------
    // recoverNetworkTablesFileIfNeeded tests
    // -------------------------------------------------------------------------

    @Test
    public void testRecovery_mainMissing_backupValid_restoresMain() throws IOException {
        Files.write(backupFile, VALID_NT_CONTENT);
        // mainFile does not exist — simulates the interrupted-save scenario

        BaseRobot.recoverNetworkTablesFileIfNeeded(mainFile, backupFile);

        assertTrue("main file should have been created", Files.exists(mainFile));
        assertArrayEquals("main file content should match backup", VALID_NT_CONTENT, Files.readAllBytes(mainFile));
    }

    @Test
    public void testRecovery_mainEmpty_backupValid_restoresMain() throws IOException {
        Files.write(mainFile, EMPTY_NT_CONTENT);
        Files.write(backupFile, VALID_NT_CONTENT);

        BaseRobot.recoverNetworkTablesFileIfNeeded(mainFile, backupFile);

        assertArrayEquals("main file should have been replaced with backup content",
                VALID_NT_CONTENT, Files.readAllBytes(mainFile));
    }

    @Test
    public void testRecovery_mainValid_backupValid_mainUntouched() throws IOException {
        byte[] originalMain = OTHER_VALID_NT_JSON.getBytes(StandardCharsets.UTF_8);
        Files.write(mainFile, originalMain);
        Files.write(backupFile, VALID_NT_CONTENT);

        BaseRobot.recoverNetworkTablesFileIfNeeded(mainFile, backupFile);

        assertArrayEquals("main file should be unchanged when it is already valid",
                originalMain, Files.readAllBytes(mainFile));
    }

    @Test
    public void testRecovery_mainMissing_backupMissing_mainStillMissing() {
        // Neither file exists — nothing should be created
        BaseRobot.recoverNetworkTablesFileIfNeeded(mainFile, backupFile);

        assertFalse("main file should not be created when backup is also missing",
                Files.exists(mainFile));
    }

    @Test
    public void testRecovery_mainMissing_backupEmpty_mainStillMissing() throws IOException {
        Files.write(backupFile, EMPTY_NT_CONTENT);

        BaseRobot.recoverNetworkTablesFileIfNeeded(mainFile, backupFile);

        assertFalse("main file should not be created when backup contains no real preferences",
                Files.exists(mainFile));
    }
}
