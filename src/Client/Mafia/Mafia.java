package Client.Mafia;


public class Mafia{


    //private Boolean isAsleep = true;//This field determines whether mafia is asleep or is active.
    private String username;
    private String role = "mafia";
    private static String curTime = "night";

    public Mafia(String username)
    {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static String getCurTime() {
        return curTime;
    }

    public static void setCurTime(String curTime) {
        Mafia.curTime = curTime;
    }


}
