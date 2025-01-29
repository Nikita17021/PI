package Client;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// Client class
class Client {

    // Helper method to display available commands
    // Обновленный метод pomoc
    static void pomoc(boolean isLoggedIn) {
        if (isLoggedIn) {
            // Меню для залогиненных пользователей
            String[] pomoct = {
                    "╔══════════════════════════════════════════════════════════════════╗",
                    "║                          ⚡ COMMANDS ⚡                            ║",
                    "╟──────────────────────────────────────────────────────────────────╢",
                    "║  [3] ⚡ Display the 10 latest posts                               ║",
                    "║  [4] ⚡ Add new posts                                             ║",
                    "║  [5] ⚡ Upload files to the cloud                                 ║",
                    "║  [6] ⚡ Download files from the cloud                             ║",
                    "║  [7] ⚡ Logout                                                    ║",
                    "║  [8] ⚡ Exit                                                      ║",
                    "║  [9] ⚡ Help                                                      ║",
                    "╚══════════════════════════════════════════════════════════════════╝"
            };

            for (String s : pomoct) {
                System.out.println(s);
            }
        } else {
            // Меню для незалогиненных пользователей
            String[] pomoct = {
                    "╔══════════════════════════════════════════════════════════════════╗",
                    "║                        ⚡ LOGIN REQUIRED ⚡                        ║",
                    "╟──────────────────────────────────────────────────────────────────╢",
                    "║  [1] ⚡ Register a new user                                       ║",
                    "║  [2] ⚡ Login                                                     ║",
                    "║  [8] ⚡ Exit                                                      ║",
                    "║  [9] ⚡ Help                                                      ║",
                    "╚══════════════════════════════════════════════════════════════════╝"
            };

            for (String s : pomoct) {
                System.out.println(s);
            }
        }
    }


    public static void main(String[] args) {
        boolean cz = false; // Flag indicating whether the user is logged in
        String login = null; // Stores the username
        pomoc(cz); // Display available commands

        try (Socket socket = new Socket("localhost", 3001)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner sc = new Scanner(System.in);
            String line = null;

            while (!"8".equals(line)) { // Main loop, exits when the user selects "8" (Exit)
                System.out.print("Select an option : ");
                line = sc.nextLine();

                // Switch statement to handle different user inputs
                switch (line) {
                    case "1":
                        // New user registration
                        System.out.print("Enter your username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter the password: ");
                        String password = sc.nextLine();
                        out.println("type:register~user_name:" + username + "~password:" + password);
                        out.flush();
                        String odp = in.readLine();
                        if ("type:register~status:OK".equals(odp)) {
                            System.out.println("User registered successfully " + username);
                        } else {
                            System.out.println("This user already exists");
                        }
                        break;

                    case "2":
                        // Login
                        System.out.print("Enter your username: ");
                        username = sc.nextLine();
                        System.out.print("Enter the password: ");
                        password = sc.nextLine();
                        out.println("type:login~user_name:" + username + "~password:" + password);
                        out.flush();
                        odp = in.readLine();
                        if ("type:login~status:OK".equals(odp)) {
                            login = username;
                            cz = true;
                            System.out.println("Logged in as " + login);
                            pomoc(cz);
                        } else {
                            System.out.println("Incorrect login details");
                        }
                        break;

                    case "3":
                        // Showing the last 10 posts
                        out.println("type:table");
                        out.flush();
                        odp = in.readLine();
                        String[] result2 = odp.split("contents:");
                        String[] zaw = result2[1].split(";");

                        // Print table header
                        System.out.printf("%-24s | %-20s | %-20s%n", "Date", "Author", "Title");
                        System.out.println("------------------------------------------------------------");

                        // Print each row
                        for (int i = 0; i < zaw.length; i += 3) {
                            // Remove prefixes from data
                            String date = zaw[i].replace("data-", "");
                            String author = zaw[i + 1].replace("nick-", "");
                            String title = zaw[i + 2].replace("tresc-", "");

                            // Print formatted row
                            System.out.printf("%-24s | %-20s | %-20s%n", date, author, title);
                        }
                        break;



                    case "4":
                        // Adding posts
                        if (!cz) {
                            System.out.println("Log in first");
                        } else {
                            System.out.print("Enter post: ");
                            String postContents = sc.nextLine();
                            out.println("type:czat~login:" + login + "~contents:" + postContents);
                            out.flush();
                            odp = in.readLine();
                            if ("type:czat~status:OK".equals(odp)) {
                                System.out.println("The post has been successfully added to the board.");
                            }
                        }
                        break;

                    case "5":
                        System.out.println("The option selected is: File transfer - File upload");
                        if (!cz) {
                            return;
                        }

                        System.out.println("Enter the file path:");
                        String filePath = sc.nextLine();

                        File plik = new File(filePath);

                        if (plik.exists()) {
                            String fileName = Paths.get(filePath).getFileName().toString();

                            // We read and encode file data to Base64
                            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                            String encodedData = Base64.getEncoder().encodeToString(fileBytes);

                            // We send Base64 encoded data to the server
                            out.println(formatSentFileRequest(login, fileName, encodedData));

                            // We are waiting for a response from the server
                            out.flush();

                            String respond = in.readLine();
                            if ("type:transfer_out~status:OK".equals(respond)) {
                                System.out.println("The file was successfully sent to the cloud.");
                            }
                        } else {
                            System.out.println("The file does not exist in the specified path.");
                        }
                        break;

                    case "6":
                        // Downloading files from the cloud
                        if (!cz) {
                            System.out.println("Log in first");
                        } else {
                            System.out.print("Enter the name of the file to download: ");
                            String fileNameIn = sc.nextLine();
                            System.out.println("Enter the name you want to save the file under:");
                            String fileNameSave = sc.nextLine();

                            out.println(formatDownloadFileRequest(login, fileNameIn));
                            out.flush();
                            String respond = in.readLine();
                            String[] strTemp = splitW(respond);
                            String stat = strTemp[2];
                            if (stat.equals("200")) {
                                String daneRes = strTemp[4];

                                // We decode the Base64 data into bytes
                                byte[] fileBytes = Base64.getDecoder().decode(daneRes);

                                // We write the bytes to the file
                                try (FileOutputStream fos = new FileOutputStream(fileNameSave)) {
                                    fos.write(fileBytes);
                                    System.out.println("The file was successfully downloaded from the cloud.");
                                } catch (IOException e) {
                                    System.out.println("Error reading data");
                                }
                            } else {
                                System.out.println("There is no such file on your cloud drive.");
                            }
                        }
                        break;

                    case "7":
                        // Logging out
                        if (!cz) {
                            System.out.println("Log in first");
                        } else {
                            login = null;
                            cz = false;
                            System.out.println("You have logged out successfully");
                        }
                        break;

                    case "8":
                        // Exit
                        login = null;
                        cz = false;
                        socket.close();
                        System.out.println("Thanks for using our application, goodbye!");
                        System.exit(0);
                        break;

                    case "9":
                        // View help
                        pomoc(cz);
                        break;

                    default:
                        System.out.println("Invalid option. Select a number from 1 to 8 or enter 9 to view help.");
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to format file upload request
    private static String formatSentFileRequest(String login, String fileNameOut, String dane) {
        return ("type:transfer_out~user_name:" + login + "~kind:out~file_name:" + fileNameOut + "~file_length:" + 540 + "~is_empty:" + true + "~Dane~" + dane);
    }

    // Method to format file download request
    private static String formatDownloadFileRequest(String login, String fileNameIn) {
        return "type:transfer_in~user_name:" + login + "~kind:in~file_name:" + fileNameIn;
    }

    // Method to split response string
    private static String[] splitW(String respond) {
        return respond.split("~");
    }
}
