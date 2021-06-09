package Client.Citizen;


public class Sniper extends Citizen {

    private String guess;//This field determines the name of that player which Sniper thinks he is mafia
    private Boolean guessIsTrue;//This field checks if the guess of the sniper is true or false.
    private int bullets;
    //private static String username;
    //private static String role = "sniper";
    //protected String curTime = "night";

    public Sniper(String username,int mafias)
    {
        super(username);
        bullets = 2 - mafias;
    }

    public boolean setBullets()
    {
        if (bullets != 0)
        {
            bullets--;
            return true;
        }
        else
        {
            return false;
        }
    }
    //may need a method to check if he should die
}
