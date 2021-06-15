package Client.Mafia;



public class DrLector extends Mafia {


    //private String username;
    private String role = "dr.Lector";
    private String save;
    private int heart = 1;

    /**
     * This method is used to access the person whom dr lector wants to save
     */
    public String getSave() {
        return save;
    }

    /**
     * This method is used to determine whom dr lector wants to save
     * @param save is the person whom dr lector wants to save
     */
    public void setSave(String save) {
        this.save = save;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
