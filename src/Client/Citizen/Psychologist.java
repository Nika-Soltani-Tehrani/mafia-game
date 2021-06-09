package Client.Citizen;



public class Psychologist extends Citizen{

    private String guess;//This field is used to save the one's name which the citizen thinks he is mafia.
    //private String username;
    private String role = "psychologist";
    //protected String curTime = "night";


    public Psychologist(String username) {
        super(username);
    }
}
