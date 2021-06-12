package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username;
    private String role ;
    private static String curTime = "night";
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    Scanner sc = new Scanner(System.in);



    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.connectToServer();

        /*client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }
        client.receiveMessage();
        if (client.getUsername().equals("mafia"))
        {
            client.sendMessage();
        }*/


    }

    private void connectToServer() {
        String serverCommand;
        String clientMessage;

        // establish a connection by providing host and port number
        System.out.println("Enter port number of the server of the game you wanna play");
        int port = sc.nextInt();
        sc.nextLine();

        try {
            this.socket = new Socket("localhost", port);
            System.out.println("READY");
            // writing to server
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            // reading from server
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            receiveMessage();//enter username
            sendMessage();//send a username

            //checkUniqueName();
            //assignRole();

            // welcome night
            //if (receiveMessage().split(" ")[2].equals(role))
            //{
              //  sendMessage(username + " is " + role);
            //}

            receiveAndSendData();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    public String receiveMessage() throws IOException {
        String serverCommand = reader.readLine();
        System.out.println(serverCommand);
        return serverCommand;
    }

    /**
     * Sends a message to the server.
     */
    public void sendMessage()
    {
        String clientMessage = sc.nextLine();
        writer.write(clientMessage + "\n");
        writer.flush();
        System.out.println(clientMessage);
    }
    /**
     * Sends a message to the server.
     */
    /*public void sendMessage(String message)
    {
        writer.write(message + "\n");
        writer.flush();
        System.out.println(message);
    }*/
    /*public void checkUniqueName() throws IOException {
        String serverCommand = reader.readLine();
        while (true) {
            System.out.println(serverCommand + "s,dorkkoevroko");
            if (serverCommand.equals("True")) {
                setUsername(serverCommand);
                System.out.println("username is valid");
                break;
            }
            else {
                System.out.println("username was taken before. Enter a new user name");
                sendMessage();

            }
            serverCommand = reader.readLine();
        }
    }*/
    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void assignRole() throws IOException {
        String serverCommand = reader.readLine();
        System.out.println(serverCommand);
        role =  serverCommand.split(" ")[3];
        System.out.println(role);
    }

    /**
     * sending username and score data to server
     */
    public void receiveAndSendData() throws IOException {

        String input = sc.nextLine();

        while(!input.equals("Exit")){
            receiveMessage();
            try{
                //dataOutputStream.writeUTF(input);
                writer.println(input);
                writer.flush();

            }catch (Exception ex){
                ex.printStackTrace();
            }

            input = sc.nextLine();
        }


    }

    /**
     * receiving username and score data to server
     */
    public void receiveData(){

        String input = sc.nextLine();

        while(!input.equals("Exit")){

            try{
                //dataOutputStream.writeUTF(input);
                writer.println(input);
                writer.flush();

            }catch (Exception ex){
                ex.printStackTrace();
            }

            input = sc.nextLine();
        }


    }

}
