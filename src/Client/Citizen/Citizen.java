package Client.Citizen;



public class Citizen {

    private String username;
    private String role = "citizen";
    protected static String curTime = "night";
    protected Boolean isAsleep = true;//This field determines whether mafia is asleep or is active.
    private String guess;//This field is used to save the one's name which the citizen thinks he is mafia.



    public Citizen(String username) {
        this.username = username;
    }

    public String getTime() {
        return curTime;
    }

    public void setTime(String time) {
        this.curTime = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAsleep() {
        return isAsleep;
    }

    public void setAsleep(Boolean asleep) {
        isAsleep = asleep;
    }

}
