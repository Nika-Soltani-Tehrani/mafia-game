package Narrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket socket;
    private int numOfUser;
    private Server server;
    private String username;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public ClientHandler(Socket socket,int numOfCurUser,Server server) {
        this.socket = socket;
        this.server = server;
        this.numOfUser = numOfCurUser;
    }

    @Override
    public void run() {

        String role;
        try
        {
            //Read from the client
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Write to the client
            writer = new PrintWriter(socket.getOutputStream(), true);

            username = server.getUsername(reader,writer);
            //usersAndStreams.put(username,writer);


            String clientMessage;
            String serverMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + username + "]: " + clientMessage;
                server.sendFromClientToAll(serverMessage, writer);

            } while (!clientMessage.equals("bye"));


            role = reader.readLine();
            System.out.println(role);


        } catch (IOException e) {
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

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

}
