package Client.Citizen;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Detective extends Citizen {

    private String guess;//This field indicates that person whom the detective thinks he is mafia
    private static String username;
    private static String role = "detective";
    protected String curTime = "night";


}
