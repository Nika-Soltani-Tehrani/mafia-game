package Client.Citizen;


import Narrator.ClientHandler;

public class Psychologist extends Citizen{

    private ClientHandler muted;
    private boolean toMute;

    public ClientHandler getMuted() {
        return muted;
    }

    public void setMuted(ClientHandler muted) {
        this.muted = muted;
    }

    public boolean wantToMute() {
        return toMute;
    }

    public void setToMute(boolean toMute) {
        this.toMute = toMute;
    }




}
