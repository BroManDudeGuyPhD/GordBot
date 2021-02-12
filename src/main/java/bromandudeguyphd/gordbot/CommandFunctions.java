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
        
    public static String dice(String[] args) {
        String finalNumber = "";
        if(args[0].equals(GordBot.COMMAND_PREFIX+"d") || args[0].equals(GordBot.COMMAND_PREFIX+"d ") || args.length > 2){
            
            finalNumber = "you need to put a number, formatted like "+GordBot.COMMAND_PREFIX+"d20";
        }
        
        int random = (int) (Math.random() * Integer.parseInt(args[1].trim().replace("#", "")) + 1);
        
        String[] results = NumberToSingalDigits(random).split(" ");
        for(int i = 0; i < results.length; i++){
            finalNumber += ":"+results[i]+":";
        }

        return finalNumber;


    }


    public static String NumberToSingalDigits(int n){
        String word[]={"zero","one","two","three","four","five","six","seven","eight","nine"};
        int digit;
        String answer="";
        for(int temp=n; temp>0; temp/=10){
            digit=temp%10;
            answer=word[digit]+" "+answer+" ";
        }
        return answer.trim();
    }

    
}
