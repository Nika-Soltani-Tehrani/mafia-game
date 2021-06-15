import java.util.Timer;
import java.util.TimerTask;

public class TimeChecker {

    Timer timer;

    public TimeChecker(int seconds) {
        this.timer = new Timer();
        timer.schedule(new RemindTask() , seconds* 1000L);
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Time's up! ");
            timer.cancel();
        }
    }
    public static void main(String[] args)
    {
        new TimeChecker(5);
        System.out.println("Task scheduled ");
    }
}
