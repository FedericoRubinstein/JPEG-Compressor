import java.util.Scanner;

public class main {

    public static void welcome_menu () {
        IO.println("Welcome!");
        IO.println("Choose an option:");
        IO.println("\t 1) Compress");
        IO.println("\t 2) Decompress");
        IO.println("\t 0) Exit");
    }

    public static void main(String[] args) {


        boolean exit = false;
        String op;
        Scanner scanner = new Scanner(System.in);
        String path;

        while (!exit) {
            welcome_menu();
            switch (scanner.nextLine()) {
                case "1":
                    IO.println("You selected compression");
                    IO.println("Please, copy the absolute path of the file you want to compress: ");

                    path = scanner.nextLine();
                    if (IO.checkExists(path)) {
                        try {
                            IO.println("Obtaining file...");
                            ppmFile image = new ppmFile(path);
                            IO.println("Compressing...");
                        } catch (Exception e) {
                            IO.println("Error while compressing!");
                        }
                    } else {
                        IO.println("The file doesn't exist!\n");
                    }
                    break;


                case "2":
                    IO.println("You selected decompress");
                    IO.println("Please, copy the absolute path of the file you want to decompress: ");
                    path = scanner.nextLine();
                    if (IO.checkExists(path)) {
                        try {
                            IO.println("Decompressing...");
                        } catch(Exception e) {
                            IO.println("Error while decompressing!");
                        }
                    } else {
                        IO.println("The file doesn't exist!\n");
                    }
                    break;

                case "0":
                    IO.println("Goodbye!");
                    exit = true;
                    break;

                default:
                    IO.println("Wrong option!\n");
                    break;
            }
        }
    }
}