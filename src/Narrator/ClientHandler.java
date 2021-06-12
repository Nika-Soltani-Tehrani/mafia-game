package Narrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread{

    private Socket socket;



    private String role;
    private Server server;
    private String username;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket,Server server,String role) throws IOException {
        this.socket = socket;
        this.server = server;
        this.role = role;
        //Read from the client
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //Write to the client
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {


        try
        {

            String clientMessage;
            String serverMessage;

            //make connection

            server.sendToAClient("Enter a username",writer);

            username = reader.readLine();
            System.out.println(username);
            //server.getUsername(this.getReader(),this.getWriter());

            // welcome night
            //serverMessage = "Your role is " + role;
            //writer.println(serverMessage + "\n");
            //writer.flush();



            /*server.sendMessage("Who are mafia");
            ArrayList<String> mafiaMessages = new ArrayList<>();
            for (int i = 0 ; i < server.getMafias(); i++)
            {
                mafiaMessages.add(reader.readLine());
            }

            server.sendMessage("Who is godFather?");

            server.sendMessage("Who is drLector?");*/
            /*server.sendMessage("Who is mayor");
            server.sendMessage("Who is doctor");
            server.sendMessage("Who is sniper");
            server.sendMessage("Who is psychologist");
            server.sendMessage("Who is dieHard");*/

            clientMessage = reader.readLine();

            while (!clientMessage.equals("exit"))
            {

                if (server.getCurTime().equals("day"))
                {

                    server.sendFromClientToAll(clientMessage,writer);

                }

                if (server.getCurTime().equals("night"))
                {
                    server.sendFromMafiaToMafia(clientMessage,writer);

                }

                if (server.getCurTime().equals("votingTime"))
                {

                    //voting function
                }

            }
            //day
            /*do {
                clientMessage = reader.readLine();
                serverMessage = "[" + username + "]: " + clientMessage;
                server.sendFromClientToAll(serverMessage, writer);

            } while (!clientMessage.equals("exit") && curTime.equals("day"));*/
            //night
            /*do {
                clientMessage = reader.readLine();
                serverMessage = "[" + username + "]: " + clientMessage;
                server.sendFromMafiaToMafia(serverMessage, writer);

            } while (!clientMessage.equals("exit"));*/





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
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BufferedReader getReader() {
        return reader;
    }


    public PrintWriter getWriter() {
        return writer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



}
