package CueZone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLogger {
    public final String path;
    public FileLogger(String path) {
        this.path = path;
    }
    public void log(String fileName, String str, String extension) {
        try {
            Files.createDirectories(Paths.get(path));

            fileName = "/" + fileName + extension;
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.path + fileName));
            writer.write(str);

            writer.close();

            System.out.println("Logs outputted to: " + this.path + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
