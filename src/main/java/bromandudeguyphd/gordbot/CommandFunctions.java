package bromandudeguyphd.gordbot;

import discord4j.core.DiscordClient;
import discord4j.core.object.data.stored.PresenceBean;
import discord4j.core.object.presence.Presence;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author BroManDudeGuyPhD
 * Class for command functions so GordBot.Java isn't flooded
 */
public class CommandFunctions {
    
//GordBot Commands
    public static String updateStatus(DiscordClient gordbot, String status){
        PresenceBean bean = new PresenceBean();
        bean.setStatus(status);
        Presence presence = new Presence(bean);
        gordbot.updatePresence(presence);
        
        System.out.println("Guildcount: "+gordbot.getGuilds().count().toString());
        System.out.println("Usercount: "+gordbot.getUsers().count().toString());
        System.out.println("Status: "+gordbot.updatePresence(presence));
            
        return "Status Updated";
    }
        
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
        
    
    
    
    
    
    
    
    
//Internal Class Functions
    private static String getTimeFromMilis(long milis) {

        long hrs = TimeUnit.MILLISECONDS.toHours(milis);
        long min = TimeUnit.MILLISECONDS.toMinutes(milis) - (TimeUnit.MILLISECONDS.toHours(milis) * 60);
        long sec = TimeUnit.MILLISECONDS.toSeconds(milis) - (TimeUnit.MILLISECONDS.toMinutes(milis) * 60);

        return String.format("%02dh:%02dm:%02ds", hrs, min, sec);
    }
        
    private static String getDateTime(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            
            return dtf.format(now);
        }
        
    
    public static Integer getRand() {
        int Low = 0;
        int High = 10;

        int Result;
        Random r = new Random();
        Result = r.nextInt(High - Low) + Low;
        return Result;
    }
    
}