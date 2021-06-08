package Narrator;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server class
class Server {

    final int port = 1234;
    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<String> roles = new ArrayList<>();
    private static ArrayList<PrintWriter> oStreams = new ArrayList<>();
    private static ArrayList<String> times = new ArrayList<>();
    private static String curTime = "night";


    private static LinkedHashMap<PrintWriter,String[]> currentUsers  = new LinkedHashMap<>();
    private static LinkedHashMap<String,String> usersAndRoles  = new LinkedHashMap<>();
    private static LinkedHashMap<PrintWriter,String[]> mafiaGroup = new LinkedHashMap<>();

    private ServerSocket serverSocket;
    //private static ArrayList<Record> records;
    private static String filePath = "server-files/records.txt";

    public Server() throws IOException
    {

        try {
            this.serverSocket = new ServerSocket(1234);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        //records = new ArrayList();
        File file = new File(filePath);
        if (!file.exists()) {
            //this.writeFile(records, filePath);
        }

    }

    public static void main(String[] args) throws IOException
    {
        {
            times.add("night");
            times.add("day");
            times.add("votingTime");
        }

        Server server = new Server();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players");
        int numberOfThreads = scanner.nextInt();
        scanner.nextLine();

        server.makeConnection(numberOfThreads);
            //first night
            /*for ( PrintWriter writer: oStreams)
            {
                writer.println("Welcome to the first night.\n");
                writer.flush();
                writer.println("First, god father wake up.\n");
                writer.flush();
                for ( Map.Entry<PrintWriter, String[]> mafiaMember : mafiaGroup.entrySet())
                {

                    if (mafiaMember.getValue()[1].equals("mafia") || mafiaMember.getValue()[1].equals("dr.Lector"))
                    {
                        int index = roles.indexOf("god father");
                        mafiaMember.getKey().println("god father is " + users.get(index) + "\n");
                        mafiaMember.getKey().flush();
                    }

                }

            }

            for ( PrintWriter writer: oStreams)
            {
                writer.println("Now, dr.Lector wake up.\n");
                writer.flush();
                for ( Map.Entry<PrintWriter, String[]> mafiaMember : mafiaGroup.entrySet())
                {

                    if (mafiaMember.getValue()[1].equals("mafia") || mafiaMember.getValue()[1].equals("god father"))
                    {
                        int index = roles.indexOf("dr.Lector");
                        mafiaMember.getKey().println("dr.Lector is " + users.get(index) + "\n");
                        mafiaMember.getKey().flush();
                    }

                }

            }*/

    }


    private static class ConnectionHandler implements Runnable {

        private Socket socket;
        private int numOfUser;

        public ConnectionHandler(Socket socket,int numOfCurUser) {
            this.socket = socket;
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
                //System.out.println(username);


                role = reader.readLine();
                System.out.println(role);
                //addNewUser(writer,numOfUser,role);
                //System.out.println( users.get(numOfUser) + " - " + role + " added to the game.");
                //set username to each role
                //writer.println(users.get(numOfUser));
                //writer.flush();

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
        usersAndRoles.put(users.get(numOfUser),role);

        roles.add(role);
        oStreams.add(writer);

        if (role.equals("mafia") || role.equals("god-father") || role.equals("dr.Lector"))
        {
            mafiaGroup.put(writer,info);
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

    private String setTime(String curTime)
    {
        int index = times.indexOf(curTime);
        if( index < times.size() - 1)
        {
            return times.get(index + 1);
        }
        else
            return times.get(0);
    }

    private void assignRoles()
    {
        Collections.shuffle(users);

    }

    private void determinePlayers(int numberOfThreads)
    {
        int ordinaries = numberOfThreads - 8;

    }
    public void makeConnection(int numberOfThreads) throws IOException {

        int numOfCurUser = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("The narrator is waiting on port '1234' for players to join...");
        //serverSocket.setReuseAddress(true);
        int count = numberOfThreads;
        try {
            while (count > 0) //loop for starting the connections with clients
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected with port address" + socket.getInetAddress().getHostAddress());


                //socket.setSoTimeout(30000); // inputStream's read times out if no data came after 3 seconds
                executorService.execute(new ConnectionHandler(socket, numOfCurUser++));
                // go back to start of infinite loop and listen for next incoming connection
                count--;
            }

        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

}




