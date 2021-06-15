package Client.Citizen;


public class DieHard extends Citizen  {


    private int maxAsk = 2;
    private boolean wantToAsk = false;
    private int heart = 1;

    public boolean getAsked() {
        return wantToAsk;
    }

    public void setWantToAsk(boolean wantToAsk) {
        this.wantToAsk = wantToAsk;
    }



    public boolean haveNightHeart() {
        if (heart == 1) {
            heart--;
            return true;
        }
        else
            return false;
    }

    public boolean canAsk() {
        if (maxAsk != 0) {
            maxAsk--;
            return true;
        }
        else
            return false;
    }


}
