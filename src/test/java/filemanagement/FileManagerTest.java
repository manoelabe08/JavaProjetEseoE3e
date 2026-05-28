package file_management;

import exceptions.FilePersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateFileIncludingParentDirectories() throws Exception {
        FileManager fileManager = new FileManager();
        String filePath = tempDir.resolve("nested/test-file.txt").toString();

        File file = fileManager.createFile(filePath);

        assertTrue(file.exists());
        assertTrue(file.isFile());
    }

    @Test
    void shouldWriteAndReadFile() throws Exception {
        FileManager fileManager = new FileManager();
        String filePath = tempDir.resolve("data.txt").toString();
        String content = "Hello STRMS";

        fileManager.writeFile(filePath, content);
        String read = fileManager.readFile(filePath);

        assertTrue(read.contains("Hello STRMS"));
    }

    @Test
    void shouldThrowWhenReadingMissingFile() {
        FileManager fileManager = new FileManager();
        String filePath = tempDir.resolve("absent.txt").toString();

        assertThrows(FilePersistenceException.class,
                () -> fileManager.readFile(filePath));
    }
}