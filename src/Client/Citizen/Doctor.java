package Client.Citizen;




public class Doctor extends Citizen{

    private String guess;//This field is used to save the one's name which the citizen thinks he is mafia.
    private int timesOfHimselfSurvival = 1;
    private int timesOfOtherSurvival = 2;

    private static String username;
    private static String role = "doctor";
    protected String curTime = "night";



}
