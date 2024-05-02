package CueZone;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLogger {
    public final String path;
    public FileLogger(String path) {
        this.path = path;
    }
    public void log(String fileName, String str) {
        try{
            Files.createDirectories(Paths.get(path));

            fileName = "/" + fileName + ".log";
            FileOutputStream writeData = new FileOutputStream(this.path + fileName);
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(str);
            writeStream.flush();
            writeStream.close();

            System.out.println("Logs outputted to: " + this.path + fileName);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
