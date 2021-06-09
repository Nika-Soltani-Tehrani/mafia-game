package Client.Citizen;




public class Doctor extends Citizen{

    private String guess;//This field is used to save the one's name which the citizen thinks he is mafia.
    private int extraHeart = 1;
    private int potion = 2;

    //private String username;
    //private String role = "doctor";
    //private String curTime = "night";

    public Doctor(String username)
    {
        super(username);
    }

    public void setHeart()
    {
        if (extraHeart != 0) {
            extraHeart--;
        }
        else
        {
            System.out.println("Doctor died");
            //handle how to close his stream and client service
        }
    }

    public boolean setPotion()
    {
        if (potion != 0) {
            potion--;
            return true;
        }
        else
        {
            return false;
        }
    }

}
