import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IO {


    //Check if a file exists
    public static boolean checkExists(String path) {
        return Files.exists(Paths.get(path));
    }

    //Returns the contents of the file in Path
    public static byte[] getFile(String Path){
        byte[] content = new byte[0];
        try {
            File file = new File(Path);
            if(file.isDirectory()) return content;
            content = Files.readAllBytes(file.toPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }


    //Outputs through the console
    public static void println(String text){
        System.out.println(text);
    }
}
