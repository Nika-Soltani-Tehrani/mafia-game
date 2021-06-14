package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username;
    private String role ;
    private static String curTime = "night";
    private DataOutputStream writer;
    private DataInputStream reader;
    Scanner sc = new Scanner(System.in);



    public static void main(String[] args) {

        Client client = new Client();
        client.connectToServer();



    }

    private void connectToServer() {
        String serverCommand;
        String clientMessage;

        // establish a connection by providing host and port number
        System.out.println("Enter port number of the server of the game you wanna play");
        int port = sc.nextInt();
        sc.nextLine();

        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("READY");
            this.reader = new DataInputStream(socket.getInputStream());
            //Write to the client
            this.writer = new DataOutputStream(socket.getOutputStream());

            receiveMessage();//enter username
            sendMessage();//send a username
            assignRole();
            //checkUniqueName();
            username = reader.readUTF();//waiting for assigning username


            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    public String receiveMessage() throws IOException {
        String serverCommand = reader.readUTF();
        System.out.println(serverCommand);
        return serverCommand;
    }

    /**
     * Sends a message to the server.
     */
    public void sendMessage() throws IOException {
        String clientMessage = sc.nextLine();
        writer.writeUTF(clientMessage);
        System.out.println(clientMessage);
    }
    /**
     * Sends a message to the server.
     */

    /*public void checkUniqueName() throws IOException {
        String serverCommand = reader.readLine();
        while (true) {

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
        String serverCommand = reader.readUTF();
        System.out.println(serverCommand);
        role =  serverCommand.split(" ")[3];
        //System.out.println(role);
    }

    /**
     * writing content on file using bufferedWriter
     * @param content is what we want to write to the file
     */
    public void fileWriter(String content,File file) {

        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.newLine();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally{
            try{
                writer.flush();
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * This method is used to edit a file
     * @param file is the wanted file
     * @param content is what we wanna add to our file.
     */
    public void editFile(File file,String content)
    {
        String result = "";

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String newLine;
            while ((newLine = reader.readLine()) != null) {
                //result = result.concat(newLine);
                result = result + "\n" + newLine;
            }
            //System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        result = result.concat(content);

        BufferedWriter writer = null;
        try
        {
            //File file = new File(fileName);
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(result);
            writer.newLine();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally{
            try{
                assert writer != null;
                writer.flush();
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * reading content from file using bufferedReader
     * @param file file object
     * @return content of the file
     */
    public String fileReader(File file) {

        String result = "";

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String newLine;
            while ((newLine = reader.readLine()) != null) {
                result = result.concat(newLine);
            }
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


}
