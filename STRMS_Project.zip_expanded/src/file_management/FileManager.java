package file_management;

import exceptions.FilePersistenceException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public File createFile(String filename) throws FilePersistenceException {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            return file;
        } catch (IOException e) {
            throw new FilePersistenceException("Unable to create file: " + filename, e);
        }
    }

    public void writeFile(String filename, String content) throws FilePersistenceException {
        File file = createFile(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            throw new FilePersistenceException("Unable to write file: " + filename, e);
        }
    }

    public void appendToFile(String filename, String content) throws FilePersistenceException {
        File file = createFile(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            throw new FilePersistenceException("Unable to append to file: " + filename, e);
        }
    }

    public String readFile(String filename) throws FilePersistenceException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new FilePersistenceException("File not found: " + filename);
        }

        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new FilePersistenceException("Unable to read file: " + filename, e);
        }

        return builder.toString();
    }
}
