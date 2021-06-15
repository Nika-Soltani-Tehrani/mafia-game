package Client;



import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class WriteThread extends Thread {
    private DataOutputStream writer;
    private Socket socket;
    private Client client;
    private File file ;


    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {

            this.writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Scanner sc = new Scanner(System.in);

        System.out.println("\nEnter your name: ");
        String userName = sc.nextLine();
        client.setUsername(userName);
        file = new File("./" + userName + "File.txt");
        client.fileWriter(userName + "'s messages\n",file);

        try {
            writer.writeUTF(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String defaultText;
        String text;

        do {
            //System.out.println("[" + userName + "]: ");
            defaultText = "[" + userName + "]: ";
            text = sc.nextLine();

            if (text.equals("HISTORY"))
            {
                client.fileReader(file);
            }
            else {
                client.editFile(file, text + "\n");
                try {
                    text = defaultText + " " + text;
                    writer.writeUTF(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } while (!text.equals("exit"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}