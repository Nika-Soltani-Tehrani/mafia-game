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
    private PrintWriter out;
    private BufferedReader in;


    public static void main(String[] args)
    {

        Client client = new Client();
        client.connectToServer();
        Scanner sc = new Scanner(System.in);


        // closing the scanner object
        sc.close();
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
            out = new PrintWriter(socket.getOutputStream(), true);

            // reading from server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Enter a user name");
            String name = sc.nextLine();
            out.write(name);
            out.flush();
            String serverCommand = in.readLine();
            while (true) {

                if (serverCommand.equals("True")) {
                    setUsername(name);
                    break;
                }
                else {
                    System.out.println("Enter a new user name");
                    name = sc.nextLine();
                    out.write(name);
                    out.flush();
                }
                serverCommand = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
