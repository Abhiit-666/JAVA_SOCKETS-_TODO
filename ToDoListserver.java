import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        try (ServerSocket serverSocket = new ServerSocket(8080)) {

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

    // class for jdbc connection
    public class JDBCConnection {
        public Connection conn;

        public JDBCConnection() {
            try {

                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TODO", "root", "abhijit1");

            } catch (Exception e) {
                System.out.println(e);

            }

        }
    }

    // This class will handle requests from a single client

    private class ClientHandler extends JDBCConnection implements Runnable {
        private Socket clientSocket;

        // The socket that the client is connected to
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
                            /*
                             * String description = input.readUTF();
                             * boolean isComplete = input.readBoolean();
                             * String sql = "INSERT INTO tasks (description, isComplete) VALUES (?, ?)";
                             * try {
                             * PreparedStatement pstmt = conn.prepareStatement(sql);
                             * pstmt.setString(1, description);
                             * pstmt.setBoolean(2, isComplete);
                             * pstmt.executeUpdate();
                             * } catch (SQLException e) {
                             * e.printStackTrace();
                             * }
                             */

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
    // code to persist the TODOlist in a mysql database
    // connect to a mysql database on localhost:3306 to table TODOlist
    //

}
