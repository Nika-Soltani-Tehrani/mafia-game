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
    private static ArrayList<DataOutputStream> oStreams = new ArrayList<>();
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static String curTime = "day";
    private LinkedHashMap<String,DataOutputStream> mafiaGroup = new LinkedHashMap<>();
    private LinkedHashMap<String,ClientHandler> mafiaHandlers = new LinkedHashMap<>();
    private LinkedHashMap<String,DataOutputStream> usersAndStreams  = new LinkedHashMap<>();

    private LinkedHashMap<DataOutputStream,String[]> streamsAndUsersRoles  = new LinkedHashMap<>();

    public Server()
    { }

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players");
        server.numberOfThreads = scanner.nextInt();
        scanner.nextLine();
        server.assignRoles(server.numberOfThreads);
        server.makeConnection();
    }


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
                System.out.println("New client connected with port address " + socket.getInetAddress().getHostAddress());


                //socket.setSoTimeout(30000); // inputStream's read times out if no data came after 3 seconds
                String role = roles().get(numOfCurUser++);
                ClientHandler newUser = new ClientHandler(socket,this,role);
                clientHandlers.add(newUser);
                if(role.equals("mafia") || role.equals("drLector") || role.equals("godFather")){
                    mafiaGroup.put(role, newUser.getWriter());
                }

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
    public String getUsername(DataInputStream reader,DataOutputStream writer) throws IOException
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

    private void assignRoles(int players) throws IOException {
        int citizens = determinePlayers(players);
        int mafias = players - citizens - 8;
        int index = 0;
        for (int i = 0; i < citizens; i++)
        {
            Citizen citizen = new Citizen();
        }
        for (int i = 0; i < mafias; i++)
        {
            Mafia mafia = new Mafia();

        }
        Psychologist psychologist = new Psychologist();
        Detective detective = new Detective();
        GodFather godFather = new GodFather();
        DrLector drLector = new DrLector();
        DieHard dieHard = new DieHard();
        Sniper sniper = new Sniper(mafias);
        Doctor doctor = new Doctor();
        Mayor mayor = new Mayor();
    }

    public void assignNames() throws IOException {
        System.out.println("here");
        if (users.size() == numberOfThreads)
        {
            int index = 0;
            for (DataOutputStream writer : oStreams) {
                String username = users.get(index++);
                writer.writeUTF(username);
            }
        }
        else
            assignNames();


    }


    private synchronized static void addNewUser(DataOutputStream writer, int numOfUser,String role)
    {


    }



    /**
     * Delivers a message to all (broadcasting)
     */
    public synchronized void sendToAll(String message) throws IOException {
        for (DataOutputStream writer : oStreams) {
            writer.writeUTF(message);
        }
    }
    /**
     * Delivers a message from one client to others
     */
    public synchronized void sendFromClientToAll(String message, DataOutputStream excludeUser) throws IOException {
        for (DataOutputStream writer : oStreams) {
            System.out.println("now writer is " + writer.toString());
            if (writer != excludeUser) {
                writer.writeUTF(message);
                System.out.println("sent [ " + message + " ] to other clients");
            }
        }
    }

    /**
     * Delivers a message between mafias
     */
    public void sendFromMafiaToMafia(String message, DataOutputStream excludeUser) throws IOException {
        for (DataOutputStream writer : mafiaGroup.values()) {
            if (writer != excludeUser) {
                writer.writeUTF(message);
            }
        }
    }

    /**
     * Delivers a message to a client
     */
    public void sendToAClient(String message, DataOutputStream writer) throws IOException {
        writer.writeUTF(message);
    }

    /**
     * Receives a message from a client
     */
    public String receiveFromAClient(DataInputStream reader) throws IOException {
        String readFrom  = reader.readUTF();
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

    /**
     * Stores username of the newly connected client.
     */
    //void addUser(ClientHandler user) {
    //    oStreams.add(user.getWriter());
    //}

    /**
     * When a client is disconnected, removes the associated username and UserThread
     */
    void removeUser(ClientHandler aUser) {
       // boolean removed = userNames.remove(userName);
        //if (removed) {
            clientHandlers.remove(aUser);
            oStreams.remove(aUser.getWriter());
            System.out.println("The user " + aUser.getUsername() + " quited");
       // }
    }

    public ArrayList<String> getUsers() {
        return users;
    }




    public void startGame() throws IOException {

        sendToAll("Welcome all to the game");

    }

    public String getCurTime() {
        return curTime;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setMafias(int mafias) {
        this.mafias = mafias;
    }

    public static void setUsers(ArrayList<String> users) {
        Server.users = users;
    }

    public static ArrayList<DataOutputStream> getoStreams() {
        return oStreams;
    }

    public static void setoStreams(ArrayList<DataOutputStream> oStreams) {
        Server.oStreams = oStreams;
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void setClientHandlers(ArrayList<ClientHandler> clientHandlers) {
        this.clientHandlers = clientHandlers;
    }

    public static void setCurTime(String curTime) {
        Server.curTime = curTime;
    }

    public LinkedHashMap<String, DataOutputStream> getMafiaGroup() {
        return mafiaGroup;
    }

    public void setMafiaGroup(LinkedHashMap<String, DataOutputStream> mafiaGroup) {
        this.mafiaGroup = mafiaGroup;
    }

    public LinkedHashMap<String, ClientHandler> getMafiaHandlers() {
        return mafiaHandlers;
    }

    public void setMafiaHandlers(LinkedHashMap<String, ClientHandler> mafiaHandlers) {
        this.mafiaHandlers = mafiaHandlers;
    }

    public LinkedHashMap<String, DataOutputStream> getUsersAndStreams() {
        return usersAndStreams;
    }

    public void setUsersAndStreams(LinkedHashMap<String, DataOutputStream> usersAndStreams) {
        this.usersAndStreams = usersAndStreams;
    }

    public LinkedHashMap<DataOutputStream,String[]> getStreamsAndUsersRoles() {
        return streamsAndUsersRoles;
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





