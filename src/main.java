import java.util.Scanner;

public class main {

    private static void welcome_menu () {
        IO.println("Welcome!");
        IO.println("Choose an option:");
        IO.println("\t 1) Compress");
        IO.println("\t 2) Decompress");
        IO.println("\t 0) Exit");
    }
    private static void subsampling_menu () {
        IO.println("Please, select a subsampling type:");
        IO.println("\t 0) 4:4:4");
        IO.println("\t 1) 4:2:2");
        IO.println("\t 2) 4:2:0 (Recommended)");
    }

    public static void main(String[] args) {


        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        String path;
        JPEG jpeg = new JPEG();

        while (!exit) {
            welcome_menu();
            switch (scanner.nextLine()) {
                case "1":
                    IO.println("You selected compression");
                    IO.println("Please, paste the absolute path of the file you want to compress: ");

                    path = scanner.nextLine();
                    if (IO.checkExists(path)) {

                        try {
                            //Get the file:
                            IO.println("Obtaining file...");
                            File image;
                            image = new File(path);
                            IO.println("Obtained the file");

                            //Get the subsampling:
                            int subsampling = 2;
                            boolean selectedSubsampling = false;
                            while (!selectedSubsampling) {
                                subsampling_menu();
                                String subStr = scanner.nextLine();
                                try{
                                    subsampling = Integer.parseInt(subStr);
                                    if (subsampling >= 0 && subsampling <= 2){
                                        selectedSubsampling = true;
                                    } else {
                                        IO.println("Please, select a number from 0 to 2");
                                    }
                                } catch (Exception e) {
                                    IO.println("Please, select a number from 0 to 2");
                                }
                            }

                            //Try compressing:
                            IO.println("Compressing...");
                            byte[] compressed = jpeg.compress(image, subsampling);

                            //Save file:
                            IO.println("Compression successful! ");
                            IO.println("Please, insert the absolute path where you want to save the compression (without extension): ");
                            String saveToPath = scanner.nextLine();
                            IO.saveFile(saveToPath + ".comp", compressed);

                            IO.println("\nCompression saved as: " + saveToPath + ".comp\n");

                            jpeg.statistic.print();

                        } catch (Exception e) {
                            IO.println("Error while compressing!");
                        }
                    } else {
                        IO.println("The file doesn't exist!\n");
                    }
                    break;


                case "2":
                    IO.println("You selected decompression");
                    IO.println("Please, paste the absolute path of the file you want to decompress: ");

                    path = scanner.nextLine();
                    if (IO.checkExists(path)) {

                        try {
                            //Get the file:
                            IO.println("Obtaining file...");
                            File compressedFile;
                            compressedFile = new File(path);
                            IO.println("Obtained the file");


                            //Try compressing:
                            IO.println("Decompressing...");
                            byte[] compressed = jpeg.decompress(compressedFile);

                            //Save file:
                            IO.println("Decompression successful! ");
                            IO.println("Please, insert the absolute path where you want to save the image (without extension): ");
                            String saveToPath = scanner.nextLine();
                            IO.saveFile(saveToPath + ".ppm", compressed);

                            IO.println("\nImage saved as: " + saveToPath + ".ppm\n");

                            jpeg.statistic.print();

                        } catch (Exception e) {
                            IO.println("Error while compressing!");
                        }
                    } else {
                        IO.println("The file doesn't exist!\n");
                    }
                    break;
            }
        }
    }
}