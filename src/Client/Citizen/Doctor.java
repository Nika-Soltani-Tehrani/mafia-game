package Client.Citizen;




public class Doctor extends Citizen{


    private String save;
    private int heart = 1;

    /**
     * This method is used to access the person whom doctor wants to save
     */
    public String getSave() {
        return save;
    }

    /**
     * This method is used to determine whom doctor wants to save
     * @param save is the person whom doctor wants to save
     */
    public void setSave(String save) {
        this.save = save;
    }


    public int getHeart() {
        return heart;
    }

    public boolean haveHeart() {
        if (heart == 1) {
            heart--;
            return true;
        }
        else
            return false;
    }





}
