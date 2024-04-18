package CueZone;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileLogger {
    public final String path;
    public FileLogger(String path) {
        this.path = path;
    }
    public void log(String fileName, String str) {
        try{
            fileName = "/" + fileName + ".log";
            FileOutputStream writeData = new FileOutputStream(this.path + fileName);
            System.out.println(this.path + fileName);
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(str);
            writeStream.flush();
            writeStream.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}