package Narrator;

import Client.Citizen.*;
import Client.Mafia.DrLector;
import Client.Mafia.GodFather;
import Client.Mafia.Mafia;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server class
class Server {

    final int port = 1234;
    int numberOfThreads;
    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<String> roles = new ArrayList<>();
    private static ArrayList<PrintWriter> oStreams = new ArrayList<>();
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static String curTime = "welcome night";
    private static LinkedHashMap<String,PrintWriter> mafiaGroup = new LinkedHashMap<>();


    private static LinkedHashMap<PrintWriter,String[]> currentUsers  = new LinkedHashMap<>();
    private static LinkedHashMap<String,PrintWriter> usersAndStreams  = new LinkedHashMap<>();

    private ServerSocket serverSocket;
    //private static ArrayList<Record> records;
    private static String filePath = "server-files/records.txt";

    public Server() throws IOException
    {

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        //records = new ArrayList();
        File file = new File(filePath);
        if (!file.exists()) {
            //this.writeFile(records, filePath);
        }

    }

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players");
        server.numberOfThreads = scanner.nextInt();
        scanner.nextLine();

        server.makeConnection(server.numberOfThreads,server);
        server.assignRoles(server.numberOfThreads);

    }

    /*private static class ConnectionHandler implements Runnable {

        private Socket socket;
        private int numOfUser;
        private Server server;
        private String username;

        public ConnectionHandler(Socket socket,int numOfCurUser,Server server) {
            this.socket = socket;
            this.server = server;
            this.numOfUser = numOfCurUser;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            PrintWriter writer = null;
            String role;
            try
            {
                //Read from the client
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //Write to the client
                writer = new PrintWriter(socket.getOutputStream(), true);

                username = getUsername(reader,writer);
                usersAndStreams.put(username,writer);


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
    }*/

    public String getUsername(BufferedReader reader,PrintWriter writer) throws IOException
    {
        String username = reader.readLine();
        while (true){
            if (uniqueUsername(username)) {
                users.add(username);
                writer.println("True");
                writer.flush();
                break;
            } else {
                writer.println("False");
                writer.flush();
                username = reader.readLine();
            }
        }
        return username;

    }

    private static boolean uniqueUsername(String username)
    {

        //checking the uniqueness of the username
        if (users.size() > 0) {
            for (String user : users) {
                if (username.equals(user)) {
                    System.out.println("No unique username");
                    return false;
                }
            }
        }
       return true;
    }


    private synchronized static void addNewUser(PrintWriter writer, int numOfUser,String role)
    {
        String[] info = {users.get(numOfUser),role};
        currentUsers.put(writer,info);
        //usersAndRoles.put(users.get(numOfUser),role);

        roles.add(role);
        oStreams.add(writer);

        if (role.equals("mafia") || role.equals("god-father") || role.equals("dr.Lector"))
        {
         //   mafiaGroup.put(writer,info);
        }
    }


    // Remove user quit
    private synchronized void removeOldUser(PrintWriter oldUser){
        currentUsers.remove(oldUser);
    }


    // Send message to every user in current users list
    private synchronized void sendMessage(String message){
        for(PrintWriter user:currentUsers.keySet()){
            user.write(message);
            user.flush();

        }
    }
    /**
     * Delivers a message to all (broadcasting)
     */
    void SendToAll(String message) {
        for (PrintWriter writer : oStreams) {
            //aUser.sendMessage(message);
            writer.println(message);
            writer.flush();
        }
    }
    /**
     * Delivers a message from one client to others
     */
    void sendFromClientToAll(String message, PrintWriter excludeUser) {
        for (PrintWriter writer : oStreams) {
            if (writer != excludeUser) {
                writer.println(message);
                writer.flush();
            }
        }
    }

    /**
     * Delivers a message between mafias
     */
    void sendFromMafiaToMafia(String message, PrintWriter excludeUser) {
        for (PrintWriter writer : mafiaGroup.values()) {
            if (writer != excludeUser) {
                writer.println(message);
                writer.flush();
            }
        }
    }

    private String setTime(String curTime)
    {

        ArrayList<String> times = new ArrayList<>();
        times.add("night");
        times.add("day");
        times.add("votingTime");

        int index = times.indexOf(curTime);
        if( index < times.size() - 1)
        {
            return times.get(index + 1);
        }
        else
            return times.get(0);

    }

    private int determinePlayers(int numberOfPlayers)
    {
        int ordinaries = numberOfPlayers - 8;//8 main roles
        int mafias = (int)Math.floor(ordinaries/3);
        int citizens = ordinaries - mafias;
        System.out.println("In this game we have " + mafias + " ordinary mafia roles and " +
                            citizens + " ordinary citizen roles. ");

        return citizens;
    }
    public void makeConnection(int numberOfThreads,Server server) throws IOException
    {
        int numOfCurUser = 0;
        determinePlayers(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("The narrator is waiting on port " + port + " for players to join...");
        //serverSocket.setReuseAddress(true);
        int count = numberOfThreads;
        try {
            while (count > 0) //loop for starting the connections with clients
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected with port address" + socket.getInetAddress().getHostAddress());


                //socket.setSoTimeout(30000); // inputStream's read times out if no data came after 3 seconds
                ClientHandler newUser = new ClientHandler(socket, numOfCurUser++,this);
                executorService.execute(newUser);
                clientHandlers.add(newUser);
                // go back to start of infinite loop and listen for next incoming connection
                count--;
            }

        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    private void assignRoles(int players)
    {
        int citizens = determinePlayers(players);
        int mafias = players - citizens - 8;
        Collections.shuffle(users);
        int index = 0;
        for (int i = 0; i < citizens; i++)
        {
            Citizen citizen = new Citizen(users.get(index++));
        }
        for (int i = 0; i < mafias; i++)
        {
            Mafia mafia = new Mafia(users.get(index++));
        }
        Psychologist psychologist = new Psychologist(users.get(index++));
        Detective detective = new Detective(users.get(index++));
        GodFather godFather = new GodFather(users.get(index++));
        DrLector drLector = new DrLector(users.get(index++));
        DieHard dieHard = new DieHard(users.get(index++));
        Sniper sniper = new Sniper(users.get(index++),mafias);
        Doctor doctor = new Doctor(users.get(index++));
        Mayor mayor = new Mayor(users.get(index));

    }

    private void startGame()
    {
        int mafias = numberOfThreads - 8 - determinePlayers(numberOfThreads);
        String curTime;//badan claso stic kon va ino hamoon fildesh kon
        String username;
        sendMessage("All mafias wake up!");
        for (int i = 0 ; i < mafias; i++)
        {
            //receive names and streams

        }

        sendMessage("Who is God Father?");
        sendMessage("Who is Dr Lector?");
        sendMessage("Who is the mayor");
        sendMessage("Who is the doctor");
        sendMessage("Who is the sniper");
        sendMessage("Who is the psychologist");
        sendMessage("Who is the die hard");

        while(true){
            curTime = "night";
            if (curTime.equals("night")) {

            }
        }

    }

}
/*
public void writeFile(ArrayList<Record> array, String filePath) {
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(filePath, false);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(array);
            objectOut.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static ArrayList<Record> readFile(String filePath) {
        FileInputStream fileIn = null;

        try {
            fileIn = new FileInputStream(filePath);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        try {
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            ArrayList<Record> obj = (ArrayList)objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }
 */





