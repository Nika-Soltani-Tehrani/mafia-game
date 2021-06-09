package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username;
    private String role = "client";
    private static String curTime = "night";
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;



    public static void main(String[] args)
    {

        Client client = new Client();
        client.connectToServer();

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    private void connectToServer() {
        // object of scanner class
        Scanner sc = new Scanner(System.in);

        // establish a connection by providing host and port number
        System.out.println("Enter port number of the server of the game you wanna play");
        int port = sc.nextInt();
        sc.nextLine();

        try {
            this.socket = new Socket("localhost", port);
            System.out.println("Client connected to the server");
            // writing to server
            writer = new PrintWriter(socket.getOutputStream(), true);

            // reading from server
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Enter a user name");
            String name = sc.nextLine();
            writer.write(name);
            writer.flush();
            String serverCommand = reader.readLine();
            while (true) {

                if (serverCommand.equals("True")) {
                    break;
                }
                else {
                    System.out.println("Enter a new user name");
                    name = sc.nextLine();
                    writer.write(name);
                    writer.flush();
                }
                serverCommand = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server.
     */
    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    /**
     * Sends a message to the client.
     */
    public void receiveMessage(String message) throws IOException {
        System.out.println(reader.readLine());
    }
}
