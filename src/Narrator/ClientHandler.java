package Narrator;

import java.io.*;
import java.net.Socket;



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
            server.getUsername(this.getReader(),this.getWriter(),username);


            serverMessage = "Your role is " + role;
            writer.writeUTF(serverMessage);


            //server.assignNames();
            username = reader.readUTF();



            serverMessage = "New user connected: " + username;
            server.sendFromClientToAll(serverMessage, this.writer);

            welcomeNight();

            while (server.endOfGame()){
                do {
                    clientMessage = reader.readUTF();
                    if (server.getCurTime().equals("day")) {
                        for (String name : server.getQuitedUsernames()) {
                            writer.writeUTF(name + " quited the game last night ");
                        }
                        if (server.getDieHard().getAsked()) {
                            for (String role : server.getQuitedRoles()) {
                                writer.writeUTF(role + " quited the game last night ");
                            }
                        }
                        //do
                        serverMessage = "[" + username + "]: " + clientMessage;
                        server.sendFromClientToAll(serverMessage, this.writer);
                        clientMessage = reader.readUTF();
                        //while(times up);
                        votingCollection();
                    }

                    if (server.getCurTime().equals("night")) {
                        nightFunction(clientMessage);
                    }

                } while (!clientMessage.equals("exit"));

                server.removeUser(this);
                socket.close();

                serverMessage = username + " has quited.";
                server.sendFromClientToAll(serverMessage, this.writer);
            }


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
                System.err.println(e.getMessage());
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

        if ((role.equals("mafia") || role.equals("godFather") || role.equals("drLector")))
        {
            do {
                serverMessage = "[" + username + "]: " + clientMessage;
                server.sendFromMafiaToMafia(serverMessage, this.writer);
                clientMessage = reader.readUTF();
            } while(!clientMessage.equals("exit"));// or times up
            // set timer

            if (role.equals("godFather")) {
                writer.writeUTF("Who do you want to kill? ");
                clientMessage = reader.readUTF();
                for (ClientHandler user : server.getClientHandlers()) {
                    if (user.getUsername().equals(clientMessage))
                    {
                        if (user.getRole().equals("dieHard"))
                        {
                            if (!server.getDieHard().haveNightHeart())
                            {
                                server.removeUser(user);
                            }
                        }
                        if (user.getRole().equals("doctor"))
                        {
                            if (!server.getDoctor().haveHeart())
                            {
                                server.removeUser(user);
                            }
                        }
                    }
                }
            }
            if (role.equals("drLector")) {
                writer.writeUTF("Which mafia do you want to save? (say [his name] or say [myself] to save yourself) ");
                clientMessage = reader.readUTF();
                if (clientMessage.equals("myself"))
                {
                    if (server.getDrLector().haveHeart()){
                        server.getDrLector().setSave("drLector");
                    }
                    else
                        writer.writeUTF("You don't have extra heart ");
                }
                else
                {
                    server.getDrLector().setSave(clientMessage);
                    writer.writeUTF("You used your this night's potion for saving " + clientMessage);
                }
            }
        }

        if (role.equals("doctor")) {
            writer.writeUTF("Which citizen do you want to save?(say [his name] or say [myself] to save yourself) ");
            clientMessage = reader.readUTF();
            if (clientMessage.equals("myself"))
            {
                if (server.getDoctor().haveHeart()){
                    server.getDoctor().setSave("doctor");
                }
                else
                    writer.writeUTF("You don't have extra heart ");
            }
            else
            {
                server.getDrLector().setSave(clientMessage);
                writer.writeUTF("You used your this night's potion for saving " + clientMessage);
            }
        }
        if (role.equals("detective")) {
            writer.writeUTF("Who do you want to know his role ");
            clientMessage = reader.readUTF();
            for (ClientHandler user : server.getClientHandlers()) {
                if (user.getUsername().equals(clientMessage))
                {
                    if (user.getRole().equals("mafia") || user.getRole().equals("drLector"))
                    {
                        if (server.getQuitedRoles().contains("mafia") || server.getQuitedRoles().contains("drLector"))
                        {
                            writer.writeUTF("This role has quited ");
                        }
                        else
                            writer.writeUTF("This role is in the game now");
                    }
                    else
                        writer.writeUTF("You are not allowed to know this information");
                }
            }
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
                server.getPsychologist().setToMute(true);
                writer.writeUTF("Who do you want to mute ");
                clientMessage = reader.readUTF();
                for (ClientHandler user : server.getClientHandlers()) {
                    if (user.getUsername().equals(clientMessage))
                    {
                        server.getPsychologist().setMuted(user);
                    }
                }
            }
            else
                server.getPsychologist().setToMute(false);
        }
        if (role.equals("dieHard")) {
            writer.writeUTF("Do you want to use your role ");
            reader.readUTF();
            clientMessage = reader.readUTF();
            if (clientMessage.equals("yes") || clientMessage.equals("Yes")){
                if (server.getDieHard().canAsk()){
                    server.getDieHard().setWantToAsk(true);
                }
                else
                    writer.writeUTF("You can not ask anymore ");
            }
            else
                server.getDieHard().setWantToAsk(false);
        }

    }

    public void votingCollection() throws IOException {
        String clientMessage;
        writer.writeUTF("Who do you vote to kill? ");
        clientMessage = reader.readUTF();
        server.getVotes().add(clientMessage);
        server.sendToAll("[" + username + "] voted for killing " + clientMessage);
        if (server.getVotes().size() == server.numberOfThreads)
        {
            System.out.println("voting handling" + server.getVotes().size());
            server.handleVotes();
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
