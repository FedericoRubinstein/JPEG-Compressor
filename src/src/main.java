import java.util.Scanner;

public class main {
    public static void println(String text){
        System.out.println(text);
    }

    public static void welcome_menu () {
        println("Welcome!");
        println("Choose an option:");
        println("\t 1) Compress");
        println("\t 2) Decompress");
        println("\t 0) Exit");
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
                    println("You selected compression");
                    println("Please, copy the absolute path of the file you want to compress: ");
                    path = scanner.nextLine();
                    try {
                        println("Compressing...");
                    } catch(Exception e) {
                        println("Error while compressing!");
                    } break;

                case "2":
                    println("You selected decompress");
                    println("Please, copy the absolute path of the file you want to decompress: ");
                    path = scanner.nextLine();
                    try {
                        println("Decompressing...");
                    } catch(Exception e) {
                        println("Error while decompressing!");
                    } break;

                case "0":
                    println("Goodbye!");
                    exit = true;
                    break;

                default:
                    println("Wrong option!");
                    println("");
                    break;
            }
        }
    }
}