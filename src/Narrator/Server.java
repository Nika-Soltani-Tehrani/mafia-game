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

    int mafias;
    int numberOfThreads;
    private Boolean dieHardAction;
    final int port = 1234;
    private String curTime = "day";
    private DrLector drLector = new DrLector();
    private DieHard dieHard = new DieHard();
    private Doctor doctor = new Doctor();
    private Psychologist psychologist = new Psychologist();
    private ArrayList<String> quitedRoles = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<String> votes = new ArrayList<>();
    private ArrayList<String> quitedUsernames = new ArrayList<>();
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private ArrayList<DataOutputStream> oStreams = new ArrayList<>();
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
    public void getUsername(DataInputStream reader,DataOutputStream writer,String username) throws IOException
    {

        System.out.println(username + " we");
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
                username = reader.readUTF();
            }
        }
        //if (users.size() == numberOfThreads)
          //  assignRoles(users.size());

    }

    private boolean uniqueUsername(String username)
    {

        //checking the uniqueness of the username
        if (users.size() >= 1) {
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

        if (psychologist.getMuted().getWriter().equals(excludeUser))
        {
            System.out.println("You are not allowed to say anything today because the psychologist has banned you");
        }

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
        System.out.println(readFrom + "--------");
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
     * When a client is disconnected, removes the associated username and UserThread
     */
    void removeUser(ClientHandler user) throws IOException {

        String clientMessage;
        user.getWriter().writeUTF("Choose one: [watch the rest] / [exit] ");
        clientMessage = user.getReader().readUTF();
        if(clientMessage.equals("watch the rest"))
        {
            user.getWriter().writeUTF("write exit for just watching ");
            quitedUsernames.add(user.getUsername());
            quitedRoles.add(user.getRole());
        }
        if (clientMessage.equals("exit"))
        {
            user.getWriter().writeUTF("write exit for just watching ");
            clientHandlers.remove(user);
            oStreams.remove(user.getWriter());
            usersAndStreams.remove(user.getWriter());
            if (user.getRole().equals("mafia") || user.getRole().equals("godFather")
                    || user.getRole().equals("drLector"))
            {
                mafiaGroup.remove(user.getWriter());
            }
            users.remove(user.getUsername());
            quitedUsernames.add(user.getUsername());
            quitedRoles.add(user.getRole());

        }

        System.out.println("The user " + user.getUsername() + " quited");
    }

    public void dieHardAction()
    {

    }

    public ArrayList<String> getUsers() {
        return users;
    }

    /*public boolean checkReadiness()
    {

    }*/

    public void handleVotes() throws IOException
    {

        boolean decline = false;
        String clientMessage;
        for (ClientHandler user: clientHandlers) {
            if (user.getRole().equals("mayor"))
            {
                user.getWriter().writeUTF("Do you want to decline the voting? ");
                clientMessage = user.getReader().readUTF();
                if (clientMessage.equals("yes") || clientMessage.equals("Yes")) {
                    decline = true;
                    sendToAll("The mayor declined the voting");
                }
                else
                    decline = false;
            }
        }
        if (!decline){
            String currentMax = "";
            int maxCount = 0;
            String current = votes.get(0);
            int count = 0;
            for (int i = 0; i < votes.size(); i++) {
                String item = votes.get(i);
                if (item.equals(current)) {
                    count++;
                } else {
                    if (count > maxCount) {
                        maxCount = count;
                        currentMax = current;
                    }
                    count = 1;
                    current = item;
                }
            }

            for (ClientHandler user : clientHandlers) {

                if (user.getUsername().equals(currentMax)) {
                    if (user.getRole().equals("mafia") || user.getRole().equals("godFather") || user.getRole().equals("drLector")) {
                        if (drLector.getSave().equals(user.getUsername())) {
                            System.out.println("Dr Lector saved " + currentMax);
                        } else {
                            removeUser(user);
                        }
                    } else {
                        if (doctor.getSave().equals(user.getUsername())) {
                            System.out.println("Doctor saved " + currentMax);
                        } else {
                            removeUser(user);
                        }

                    }
                }
            }
        }
    }

    public boolean endOfGame() throws IOException {
        int citizens = users.size() - mafiaGroup.size();
        if (citizens < mafiaGroup.size() )
        {
            sendToAll("Mafias won ");
            return false;
        }
        if (mafiaGroup.size() == 0)
        {
            sendToAll("Citizens won ");
            return false;
        }
        else
            return true;
    }



    public void startGame() throws IOException {

    }

    public String getCurTime() {
        return curTime;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public LinkedHashMap<String, DataOutputStream> getMafiaGroup() {
        return mafiaGroup;
    }

    public LinkedHashMap<String, ClientHandler> getMafiaHandlers() {
        return mafiaHandlers;
    }


    public LinkedHashMap<String, DataOutputStream> getUsersAndStreams() {
        return usersAndStreams;
    }


    public LinkedHashMap<DataOutputStream,String[]> getStreamsAndUsersRoles() {
        return streamsAndUsersRoles;
    }


    public ArrayList<String> getQuitedUsernames() {
        return quitedUsernames;
    }


    public ArrayList<String> getQuitedRoles() {
        return quitedRoles;
    }

    public Psychologist getPsychologist() {
        return psychologist;
    }

    public ArrayList<String> getVotes() {
        return votes;
    }

    public DrLector getDrLector() {
        return drLector;
    }

    public DieHard getDieHard() {
        return dieHard;
    }

    public Doctor getDoctor() {
        return doctor;
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





