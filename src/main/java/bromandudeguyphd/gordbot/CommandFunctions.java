package bromandudeguyphd.gordbot;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author BroManDudeGuyPhD
 * Class for command functions so GordBot.Java isn't flooded
 */
public class CommandFunctions {    
    public static String getUptime(long startTime) {
            
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - startTime;

        int days = (int) TimeUnit.MILLISECONDS.toDays(tDelta);
        long hrs = TimeUnit.MILLISECONDS.toHours(tDelta) - (days * 24);
        long min = TimeUnit.MILLISECONDS.toMinutes(tDelta) - (TimeUnit.MILLISECONDS.toHours(tDelta) * 60);
        long sec = TimeUnit.MILLISECONDS.toSeconds(tDelta) - (TimeUnit.MILLISECONDS.toMinutes(tDelta) * 60);

        if(days != 00){
            return String.format("%02dd:%02dh:%02dm:%02ds", days, hrs, min, sec);
        }
        return String.format("%02dh:%02dm:%02ds", hrs, min, sec);
    }
        
    public static Integer dice(int High) {
        int Low = 0;

        int Result;
        Random r = new Random();
        Result = r.nextInt(High - Low) + Low;
        return Result;
    }
}
