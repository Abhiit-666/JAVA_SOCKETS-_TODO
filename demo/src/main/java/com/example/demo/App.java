package demo.src.main.java.com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class App {
	// The data structure that will store the user's to-do list
	private List<ToDoItem> toDoList = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		// Create a new ToDoListServer object
		App server = new App();

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

				// connect to a mysql database on localhost:3306 to table TODOlist
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TODO", "root", "abhijit1");
				System.out.println("Connected to the database!" + conn.toString());

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
						// need to not limit the arrays to a size of 100
						case 1:
							// Get the to-do list
							String sql1 = "SELECT * FROM tasks";
							int count = 0;
							String desc[] = new String[100];
							Boolean status[] = new Boolean[100];
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql1);
								ResultSet i = pstmt.executeQuery();

								while (i.next()) {
									desc[count] = i.getString("description");
									status[count] = i.getBoolean("isComplete");
									count++;

								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
							output.writeInt(count);
							for (int i = 0; i < count; i++) {
								output.writeUTF(desc[i]);
								output.writeBoolean(status[i]);
							}

							break;

						case 2:

							String description = input.readUTF();
							boolean isComplete = input.readBoolean();
							String sql = "INSERT INTO tasks (description, isComplete) VALUES (?, ?)";
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql);
								pstmt.setString(1, description);
								pstmt.setBoolean(2, isComplete);
								pstmt.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							}

							/*
							 * // Add a new item to the to-do list
							 * String description = input.readUTF();
							 * boolean isComplete = input.readBoolean();
							 * ToDoItem item = new ToDoItem(description, isComplete);
							 * toDoList.add(item);
							 */

							break;

						case 3:
							// Mark an item as complete
							int index = input.readInt();
							String sql2 = "UPDATE tasks SET isComplete = ? WHERE id = ?";
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql2);
								pstmt.setInt(1, 1);
								pstmt.setInt(2, index);
								pstmt.executeUpdate();
								System.out.println("Task marked as complete");
								/*
								 * if (i == 1) {
								 * output.writeUTF("Task marked as complete");
								 * } else {
								 * output.writeUTF("Task not present");
								 * }
								 */
							} catch (SQLException e) {
								e.printStackTrace();

							}

							break;
						case 4:
							// Remove an item from the to-do list
							index = input.readInt();
							String sql3 = "DELETE FROM tasks WHERE id = ?";
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql3);
								pstmt.setInt(1, index);
								pstmt.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();

							}
							String sql5 = "UPDATE tasks SET id = id - 1 WHERE id > ?";
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql5);
								pstmt.setInt(1, index);
								pstmt.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							String sql4 = "ALTER TABLE tasks AUTO_INCREMENT = ?";
							try {
								PreparedStatement pstmt = conn.prepareStatement(sql4);
								pstmt.setInt(1, index);
								pstmt.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							}
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