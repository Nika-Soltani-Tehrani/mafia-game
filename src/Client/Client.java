package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    protected static String username;
    protected static String role = "client";
    private static String curTime = "night";


    public static void main(String[] args)
    {
        // object of scanner class
        Scanner sc = new Scanner(System.in);

        // establish a connection by providing host and port number
        System.out.println("Enter port number of the server of the game you wanna play");
        int port = sc.nextInt();
        sc.nextLine();

        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Client connected to the server");
            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // reading from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            String serverCommand;

            out.write(role + "\n");
            out.flush();
            username = in.readLine();
            System.out.println(username);

            sc = new Scanner(System.in);
            String line = null;


            //curTime = in.readLine();
            System.out.println("Now it's " + curTime);


            while(socket.isConnected())
            {
                curTime = in.readLine();
                System.out.println("Now it's " + curTime);

                if (curTime.equals("night"))
                {

                }

                if (curTime.equals("day"))
                {

                }

                if (curTime.equals("votingTime"))
                {

                }




            }



            while (!"exit".equalsIgnoreCase(line)) {

                // reading from user
                line = sc.nextLine();

                // sending the user input to server
                out.println(line);
                out.flush();

                // displaying server reply
                System.out.println("Server replied " + in.readLine());
            }

            // closing the scanner object
            sc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}
