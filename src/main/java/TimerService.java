/**
 * Created by Pablo Canseco on 4/10/2018.
 */
public class TimerService {

    private static TimerService instance;

    public static TimerService getInstance() {
        if (instance == null) {
            instance = new TimerService();
            return instance;
        }
        return instance;
    }
}
