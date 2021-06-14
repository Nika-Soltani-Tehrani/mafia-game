package Client.Citizen;



public class Citizen {

    private String role = "citizen";

    protected Boolean isAsleep = true;//This field determines whether mafia is asleep or is active.
    private String guess;//This field is used to save the one's name which the citizen thinks he is mafia.





    public Boolean getAsleep() {
        return isAsleep;
    }

    public void setAsleep(Boolean asleep) {
        isAsleep = asleep;
    }

}
