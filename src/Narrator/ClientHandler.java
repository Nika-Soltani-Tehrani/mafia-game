package Narrator;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClientHandler extends Thread{

    private Socket socket;



    private String role;
    private Server server;
    private String username;
    private DataInputStream reader;
    private DataOutputStream writer;

    public ClientHandler(Socket socket,Server server,String role) throws IOException {
        this.socket = socket;
        this.server = server;
        this.role = role;
        //Read from the client
        this.reader = new DataInputStream(socket.getInputStream());
        //Write to the client
        this.writer = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {


        try
        {

            String clientMessage;
            String serverMessage;

            //make connection
            server.sendToAClient("Enter a username",writer);
            username = reader.readUTF();
            System.out.println(username);
            server.getUsers().add(username);

            serverMessage = "Your role is " + role;
            writer.writeUTF(serverMessage);
            //server.getUsername(this.getReader(),this.getWriter());

            //server.assignNames();
            username = reader.readUTF();

            //server.addUser(this);

            serverMessage = "New user connected: " + username;
            server.sendFromClientToAll(serverMessage, this.writer);

            welcomeNight();

            do {
                clientMessage = reader.readUTF();
                if(server.getCurTime().equals("day"))
                {
                    serverMessage = "[" + username + "]: " + clientMessage;
                    server.sendFromClientToAll(serverMessage, this.writer);
                }
                if(server.getCurTime().equals("night") && (role.equals("mafia") || role.equals("godFather") ||
                        role.equals("drLector")))
                {
                    nightFunction(clientMessage);
                }

            } while (!clientMessage.equals("exit"));

            server.removeUser( this);
            socket.close();

            serverMessage = username + " has quited.";
            server.sendFromClientToAll(serverMessage, this.writer);


        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if(reader != null)
                    reader.close();
                if(writer != null)
                    writer.close();

            }
            catch (IOException e)
            {
                //throw new RuntimeException(e);
                e.printStackTrace();
            }
        }
    }

    public void welcomeNight() throws IOException {
        String clientMessage;
        String serverMessage;

        writer.writeUTF("welcome to the game");
        if ((role.equals("mafia") || role.equals("godFather") || role.equals("drLector")))
        {
            clientMessage = "I am " + role;
            serverMessage = "[" + username + "]: " + clientMessage;
            server.sendFromMafiaToMafia(serverMessage, this.writer);
        }

        if ((role.equals("doctor")))
        {
            clientMessage = "I am " + role;
            serverMessage = "[" + username + "]: " + clientMessage;
            //server.sendFromMafiaToMafia(serverMessage, this.writer);
            for (ClientHandler user: server.getClientHandlers()) {
                if (user.getRole().equals("mayor"))
                {
                    user.getWriter().writeUTF(serverMessage);
                }
            }
        }
    }

    public void nightFunction(String clientMessage) throws IOException {
        String serverMessage;

        serverMessage = "[" + username + "]: " + clientMessage;
        server.sendFromMafiaToMafia(serverMessage, this.writer);
        // set timer

        if (server.getCurTime().equals("votingTime")) {
            if (role.equals("godFather")) {
                writer.writeUTF("Who do you want to kill? ");
                reader.readUTF();
            }
            if (role.equals("drLector")) {
                writer.writeUTF("Which mafia do you want to save? ");
                reader.readUTF();
            }
            if (role.equals("doctor")) {
                writer.writeUTF("Which citizen do you want to save? ");
                reader.readUTF();
            }
            if (role.equals("detective")) {
                writer.writeUTF("Who do you want to know his role ");
                reader.readUTF();
            }
            if (role.equals("sniper")) {
                writer.writeUTF("Do you want to use your role? ");
                clientMessage = reader.readUTF();
                if (clientMessage.equals("yes") || clientMessage.equals("Yes")){
                    writer.writeUTF("Who do you want to kill ");
                    clientMessage = reader.readUTF();
                    for (ClientHandler user : server.getClientHandlers()) {
                        if (user.getUsername().equals(clientMessage) &&
                                (user.getRole().equals("mafia")) || user.getRole().equals("godFather")
                                ||user.getRole().equals("drLector"))
                        {
                            server.removeUser(user);
                        }
                        else
                        {
                            for (ClientHandler player : server.getClientHandlers()) {
                                if (player.getRole().equals("sniper")) {
                                    server.removeUser(user);
                                }
                            }
                        }
                    }
                }

            }
            if (role.equals("psychologist")) {
                writer.writeUTF("Do you want to mute someone ");
                reader.readUTF();
                clientMessage = reader.readUTF();
                if (clientMessage.equals("yes") || clientMessage.equals("Yes")){
                    writer.writeUTF("Who do you want to mute ");
                    clientMessage = reader.readUTF();
                    for (ClientHandler user : server.getClientHandlers()) {
                        if (user.getUsername().equals(clientMessage))
                        {
                            // send its stream  to handle
                        }
                    }
                }
            }
            if (role.equals("dieHard")) {
                writer.writeUTF("Do you want to use your role ");
                reader.readUTF();
                clientMessage = reader.readUTF();
                if (clientMessage.equals("yes") || clientMessage.equals("Yes")){
                    // save it for the day to tell his which roles ended
                }
            }

        }
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public DataInputStream getReader() {
        return reader;
    }


    public DataOutputStream getWriter() {
        return writer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



}
