import java.io.*;
import java.net.*;
import java.util.*;

class ToDoListServer {
    // The data structure that will store the user's to-do list
    private List<ToDoItem> toDoList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Create a new ToDoListServer object
        ToDoListServer server = new ToDoListServer();

        // Start the server and listen for incoming connections
        server.startServer();
    }

    public void startServer() throws IOException {
        // Create a ServerSocket to listen for incoming connections on port 8080
        ServerSocket serverSocket = new ServerSocket(8080);

        // Print a message to the console to let us know the server is running
        System.out.println("To-do list server is running on port 8080...");

        while (true) {
            // Accept an incoming client connection
            Socket clientSocket = serverSocket.accept();

            // Create a new thread to handle the client's requests
            Thread clientHandler = new Thread(new ClientHandler(clientSocket));

            // Start the client handler thread
            clientHandler.start();
        }
    }

    // implement the ToDoItem class
    private class ToDoItem {
        private String description;
        private boolean isComplete;

        public ToDoItem(String description, boolean isComplete) {
            this.description = description;
            this.isComplete = isComplete;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }

    // This class will handle requests from a single client
    private class ClientHandler implements Runnable {
        // The socket that the client is connected to
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // Create input and output streams for the client socket
                DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    // Read the request type from the client
                    int requestType = input.readInt();

                    // Handle the request based on the request type
                    switch (requestType) {
                        case 1:
                            // Get the to-do list
                            output.writeInt(toDoList.size());
                            for (ToDoItem i : toDoList) {
                                output.writeUTF(i.getDescription());
                                output.writeBoolean(i.isComplete());
                            }
                            break;

                        case 2:
                            // Add a new item to the to-do list
                            String description = input.readUTF();
                            boolean isComplete = input.readBoolean();
                            ToDoItem item = new ToDoItem(description, isComplete);
                            toDoList.add(item);
                            break;

                        case 3:
                            // Mark an item as complete
                            int index = input.readInt();
                            toDoList.get(index).setComplete(true);
                            break;
                        case 4:
                            // Remove an item from the to-do list
                            index = input.readInt();
                            toDoList.remove(index);
                            break;
                        case 5:
                            // Exit the program
                            System.exit(0);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
