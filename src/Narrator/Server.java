package Narrator;

import Client.Citizen.*;
import Client.Mafia.DrLector;
import Client.Mafia.GodFather;
import Client.Mafia.Mafia;

import java.io.*;
import java.net.*;
import java.util.*;


// Server class
class Server {

    final int port = 1234;
    int numberOfThreads;
    int mafias;
    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<PrintWriter> oStreams = new ArrayList<>();

    public String getCurTime() {
        return curTime;
    }

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static String curTime = "day";
    private static LinkedHashMap<String,PrintWriter> mafiaGroup = new LinkedHashMap<>();

    private static LinkedHashMap<PrintWriter,String[]> currentUsers  = new LinkedHashMap<>();
    private static LinkedHashMap<String,PrintWriter> usersAndStreams  = new LinkedHashMap<>();

    private static String filePath = "server-files/records.txt";

    public Server()
    {

        //records = new ArrayList();
        /*File file = new File(filePath);
        if (!file.exists()) {
            //this.writeFile(records, filePath);
        }*/

    }

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players");
        server.numberOfThreads = scanner.nextInt();
        scanner.nextLine();
        server.makeConnection();
    }

    /*private static class ConnectionHandler implements Runnable {

        private Socket socket;
        private int numOfUser;
        //private Server server;
        private String username;

        public ConnectionHandler(Socket socket,int numOfCurUser) {
            this.socket = socket;
            //this.server = server;
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

                username = reader.readLine();
                System.out.println(username);
                username = getUsername(reader,writer);
                usersAndStreams.put(username,writer);


                String clientMessage;
                String serverMessage;

                do {
                    clientMessage = reader.readLine();
                    serverMessage = "[" + username + "]: " + clientMessage;
                    sendFromClientToAll(serverMessage, writer);

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
    private int determinePlayers(int numberOfPlayers)
    {
        int ordinaries = numberOfPlayers - 8;//8 main roles
        if ((ordinaries % 2) == 0)
            mafias = (int)Math.floor((float)ordinaries/3);
        else
            mafias = (int)Math.floor((float)ordinaries/3) + 1;

        //citizens number
        return ordinaries - mafias;
    }

    public ArrayList<String> roles()
    {
        ArrayList<String> roles = new ArrayList<>();
        for (int i = 0; i < mafias ; i++)
        {
            roles.add("mafia");
        }
        for (int i = 0; i < determinePlayers(numberOfThreads) ; i++)
        {
            roles.add("citizen");
        }
        roles.add("godFather");
        roles.add("psychologist");
        roles.add("drLector");
        roles.add("detective");
        roles.add("doctor");
        roles.add("dieHard");
        roles.add("sniper");
        roles.add("mayor");
        Collections.shuffle(roles);
        return roles;
    }

    public void makeConnection() throws IOException
    {
        int numOfCurUser = 0;
        ArrayList<String> roles = roles();
        determinePlayers(numberOfThreads);
        //ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("The narrator is waiting on port " + port + " for players to join...");
        int count = numberOfThreads;
        try {
            while (count > 0) //loop for starting the connections with clients
            {
                System.out.println(count);
                Socket socket = serverSocket.accept();
                System.out.println("New client connected with port address" + socket.getInetAddress().getHostAddress());


                //socket.setSoTimeout(30000); // inputStream's read times out if no data came after 3 seconds
                ClientHandler newUser = new ClientHandler(socket,this,roles().get(numOfCurUser++));
                clientHandlers.add(newUser);
                oStreams.add(newUser.getWriter());
                count--;
                newUser.start();
                // go back to start of infinite loop and listen for next incoming connection
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
    public String getUsername(BufferedReader reader,PrintWriter writer) throws IOException
    {
        String username = receiveFromAClient(reader);
        while (true){
            if (uniqueUsername(username)) {
                users.add(username);
                System.out.println(users.size() + "---------");
                sendToAClient("True",writer);

                break;
            } else {
                //writer.println("False\n");
                sendToAClient("False",writer);
                System.out.println("False");
                writer.flush();
            }
        }
        if (users.size() == numberOfThreads)
            assignRoles(users.size());
        return username;
    }

    private boolean uniqueUsername(String username)
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
        System.out.println("hello");
        return true;
    }

    private void assignRoles(int players)
    {
        int citizens = determinePlayers(players);
        int mafias = players - citizens - 8;
        if (users.size() != 0){
            Collections.shuffle(users);
            int index = 0;
        /*for (int i = 0; i < citizens; i++)
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
        Doctor doctor = new Doctor(users.get(index++));*/
            Mayor mayor = new Mayor(users.get(index));
        }


    }


    private synchronized static void addNewUser(PrintWriter writer, int numOfUser,String role)
    {
        String[] info = {users.get(numOfUser),role};
        currentUsers.put(writer,info);
        //usersAndRoles.put(users.get(numOfUser),role);

        //roles.add(role);
        oStreams.add(writer);

    }


    // Remove user quit
    private synchronized void removeOldUser(PrintWriter oldUser){
        currentUsers.remove(oldUser);
    }


    // Send message to every user in current users list
    public synchronized void sendMessage(String message){
        for(PrintWriter user:currentUsers.keySet()){
            user.write(message);
            user.flush();

        }
    }
    /**
     * Delivers a message to all (broadcasting)
     */
    public synchronized void SendToAll(String message) {
        for (PrintWriter writer : oStreams) {
            writer.println(message + "\n" );
            writer.flush();
        }
    }
    /**
     * Delivers a message from one client to others
     */
    public synchronized void sendFromClientToAll(String message, PrintWriter excludeUser) {
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
    public void sendFromMafiaToMafia(String message, PrintWriter excludeUser) {
        for (PrintWriter writer : mafiaGroup.values()) {
            if (writer != excludeUser) {
                writer.println(message + "\n");
                writer.flush();
            }
        }
    }

    /**
     * Delivers a message to a client
     */
    public void sendToAClient(String message, PrintWriter writer) {
        writer.println(message + "\n");
        writer.flush();
    }

    /**
     * Receives a message from a client
     */
    public String receiveFromAClient(BufferedReader reader) throws IOException {
        String readFrom  = reader.readLine();
        System.out.println(readFrom);
        return readFrom;
    }

    public String setTime(String curTime)
    {
        String[] times = {"night","day","votingTime"};
        int index = 0;
        for (String time: times) {
            if (time.equals(curTime))
                index = time.indexOf(curTime);
        }

        if( index < times.length - 1)
        {
            return times[index + 1];
        }
        else
            return times[0];

    }

    public int getMafias() {
        return mafias;
    }


    private void startGame()
    {
        int mafias = numberOfThreads - 8 - determinePlayers(numberOfThreads);
        String curTime;//badan claso stic kon va ino hamoon fildesh kon
        String username;
        sendMessage("Mafias");
        for (int i = 0 ; i < mafias; i++)
        {
            //receive names and streams

        }

        sendMessage("God Father?");
        sendMessage("Dr Lector?");
        sendMessage("Mayor");
        sendMessage("Doctor");
        sendMessage("Sniper");
        sendMessage("Psychologist");
        sendMessage("Die hard");

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





