// client class to connect to the server and send requests to the server. To get, add, remove and mark items as complete in a TODO  list. 

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8080);

            // Create an input stream to receive data from the server
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Create a Scanner
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Prompt the user to enter a request
                System.out.println("Enter a request: ");
                System.out.println("1: List all items");
                System.out.println("2: Add an item");
                System.out.println("3: Mark an item as complete");
                System.out.println("4: Remove an item");
                System.out.println("5: Exit");

                int request = scanner.nextInt();

                switch (request) {
                    case 1:
                        // Send the request to the server
                        output.writeInt(request);

                        // Get the number of items from the server
                        int numberOfItems = input.readInt();

                        // Display the items
                        for (int i = 0; i < numberOfItems; i++) {
                            System.out.println(input.readUTF() + " " + input.readBoolean());
                        }
                        break;
                    case 2:
                        // Send the request to the server
                        output.writeInt(request);

                        // Prompt the user to enter a description
                        System.out.print("Enter a description: ");
                        String description = scanner.next();

                        // Send the description to the server
                        output.writeUTF(description);

                        // Prompt the user to enter a status
                        System.out.print("Enter a status: ");
                        boolean isComplete = scanner.nextBoolean();

                        // Send the status to the server
                        output.writeBoolean(isComplete);
                        break;
                    case 3:
                        // Send the request to the server
                        output.writeInt(request);

                        // Prompt the user to enter an index
                        System.out.print("Enter an index: ");
                        int index = scanner.nextInt();

                        // Send the index to the server
                        output.writeInt(index);
                        break;
                    case 4:
                        // Send the request to the server
                        output.writeInt(request);
                        // Prompt the user to enter an index
                        System.out.print("Enter an index: ");
                        index = scanner.nextInt();

                        // Send the index to the server
                        output.writeInt(index);
                        break;
                    case 5:
                        // Send the request to the server
                        output.writeInt(request);
                        System.exit(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
