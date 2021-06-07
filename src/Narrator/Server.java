package Narrator;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server class
class Server {

    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<String> roles = new ArrayList<>();
    private static ArrayList<PrintWriter> oStreams = new ArrayList<>();
    private static ArrayList<String> times = new ArrayList<>();
    private static String curTime = "night";


    private static LinkedHashMap<PrintWriter,String[]> currentUsers  = new LinkedHashMap<>();
    private static LinkedHashMap<String,String> usersAndRoles  = new LinkedHashMap<>();
    private static LinkedHashMap<PrintWriter,String[]> mafiaGroup = new LinkedHashMap<>();


    public static void main(String[] args) throws IOException {

        {
            times.add("night");
            times.add("day");
            times.add("votingTime");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players");
        int numberOfThreads = scanner.nextInt();
        int numOfCurUser = 0;
        scanner.nextLine();

        //by this method we get players and randomize the list
        getUsers(numberOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("The narrator is waiting for players to join...");
        //serverSocket.setReuseAddress(true);
        int count = numberOfThreads;
        try{
            while (count > 0) //loop for starting the connections with clients
            {

                Socket socket = serverSocket.accept();
                System.out.println("New client connected with port address" + socket.getInetAddress().getHostAddress());

                //socket.setSoTimeout(30000); // inputStream's read times out if no data came after 3 seconds
                executorService.execute(new ConnectionHandler(socket,numOfCurUser++));
                // go back to start of infinite loop and listen for next incoming connection
                System.out.println("+++++");
                count--;
            }


            //first night
            for ( PrintWriter writer: oStreams)
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

            }


            while(users.size() >= 1)
            {
                if (curTime.equals("night"))
                {
                    //for (PrintWriter writer: currentUsers.keySet())
                    //{

                    //}

                }

                if (curTime.equals("day"))
                {

                }

                if (curTime.equals("votingTime"))
                {

                }

                setTime(curTime);
            }


        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();

        }
        finally {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();

            }
        }

    }





    public static class ConnectionHandler implements Runnable {

        private Socket socket;
        private int numOfUser;

        public ConnectionHandler(Socket socket,int numOfCurUser) {
            this.socket = socket;
            this.numOfUser = numOfCurUser;
        }

        public void run() {
            BufferedReader reader = null;
            PrintWriter writer = null;
            String role;
            try {
                //Read from the client
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //Write to the client
                writer = new PrintWriter(socket.getOutputStream(), true);

                role = reader.readLine();
                System.out.println(role);
                addNewUser(writer,numOfUser,role);
                System.out.println( users.get(numOfUser) + " - " + role + " added to the game.");
                //set username to each role
                writer.println(users.get(numOfUser));
                writer.flush();

                //socket.getOutputStream().write('h');

                //socket.getOutputStream().flush();

                // The read loop. Code only exits this loop if connection is lost / client disconnects
                /*while(true) {
                    String line = reader.readLine();
                    if(line == null) break;
                    writer.println("Echo: " + line);
                }*/
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

    private static void getUsers(int count)
    {
        Scanner scanner = new Scanner(System.in);
        int flag = 0;
        while (count > 0){
            System.out.println("Enter your username (Other players will see your username)");
            String name = scanner.nextLine();

            //checking the uniqueness of the username
            if (users.size() > 0) {
                for (String user : users) {
                    if (name.equals(user)) {
                        System.out.println("This username have been chosen. Enter another one: ");
                        flag = 1;
                        break;
                    }
                }
            }
            if (flag == 0) {
                users.add(name);
                System.out.println(name + " added to the game");
                count--;
            }
        }
        Collections.shuffle(users);
    }


    private static synchronized void addNewUser(PrintWriter writer, int numOfUser,String role)
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
    private static synchronized void removeOldUser(PrintWriter oldUser){
        currentUsers.remove(oldUser);
    }


    // Send message to every user in current users list
    private static synchronized void sendMessage(String message){
        for(PrintWriter user:currentUsers.keySet()){
            user.write(message);
            user.flush();

        }
    }

    private static String setTime(String curTime)
    {
        int index = times.indexOf(curTime);
        if( index < times.size() - 1)
        {
            return times.get(index + 1);
        }
        else
            return times.get(0);
    }


}

/*
for ( Map.Entry<PrintWriter, String[]> userInfo : currentUsers.entrySet())
            {
                LinkedHashMap<PrintWriter,String[]> mafiaGroup = new LinkedHashMap<>();
                if  (  userInfo.getValue()[1].equals("god father")
                    || userInfo.getValue()[1].equals("mafia")
                    || userInfo.getValue()[1].equals("dr.Lector")
                    )
                {
                    mafiaGroup.put(userInfo.getKey(),userInfo.getValue());
                }

            }
 */

